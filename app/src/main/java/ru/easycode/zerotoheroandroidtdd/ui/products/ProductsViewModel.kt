package ru.easycode.zerotoheroandroidtdd.ui.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ru.easycode.zerotoheroandroidtdd.domain.ProductsRepository
import ru.easycode.zerotoheroandroidtdd.domain.model.Product
import ru.easycode.zerotoheroandroidtdd.domain.model.ProductFilter
import ru.easycode.zerotoheroandroidtdd.ui.common.RunAsync
import ru.easycode.zerotoheroandroidtdd.ui.products.dvo.FilterUi
import ru.easycode.zerotoheroandroidtdd.ui.products.dvo.OrderUi
import ru.easycode.zerotoheroandroidtdd.ui.products.dvo.ProductListUi
import java.util.concurrent.atomic.AtomicReference

private const val KEY_PRODUCTS = "products"
private const val KEY_ORDERS = "orders"
private const val KEY_FILTERS = "filters"

internal class ProductsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ProductsRepository,
    private val runAsync: RunAsync,
) : ViewModel() {

    private val _productsUiListStateFlow = MutableStateFlow<List<ProductListUi>>(listOf())
    val productsUiListStateFlow = _productsUiListStateFlow.asStateFlow()


    private val _ordersUiListStateFlow = MutableStateFlow(
        value = savedStateHandle.get<List<OrderUi>>(KEY_ORDERS) ?: listOf()
    )
    val ordersUiListStateFlow = _ordersUiListStateFlow.asStateFlow()

    private val _filtersUiListStateFlow = MutableStateFlow(
        value = savedStateHandle.get<List<FilterUi>>(KEY_FILTERS) ?: listOf()
    )
    val filtersUiListStateFlow = _filtersUiListStateFlow.asStateFlow()

    private val _params = AtomicReference<Params>(
        Params(
            products = savedStateHandle.get<List<ProductListUi>>(KEY_PRODUCTS)
                ?.mapNotNull(ProductListUi::toModel)
                ?: listOf(),
            filters = filtersUiListStateFlow.value.map(FilterUi::toModel),
            orders = ordersUiListStateFlow.value.map(OrderUi::name),
            selectedFilters = filtersUiListStateFlow.value
                .mapNotNull {
                    it.takeIf { it.chosen }?.let { filter -> filter.category to filter.id }
                }
                .toMap(),
            selectedOrder = ordersUiListStateFlow.value.firstOrNull { it.chosen }?.name?.let {
                OrderUi.Order.from(it)
            } ?: OrderUi.Order.Alphabet
        )
    )

    private val _commandsFlow = MutableSharedFlow<Command>(
        replay = 0,
        extraBufferCapacity = DEFAULT_BUFFER_SIZE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        observeCommands()
        _commandsFlow.tryEmit(Command.Init)
    }

    private fun observeCommands() {
        runAsync.runFlowCollect(
            scope = viewModelScope,
            flow = _commandsFlow,
        ) {
            handleCommands(it)
        }
    }

    private suspend fun handleCommands(command: Command) {
        when (command) {
            Command.Init -> initData()
        }
    }

    private suspend fun initData() = with(CoroutineScope(currentCoroutineContext())) {
        listOf(
            launch { initProducts() },
            launch { initFilters() },
            launch { initOrders() }
        ).joinAll()
        updateProducts()
    }

    private suspend fun initProducts() {
        if (savedStateHandle.contains(KEY_PRODUCTS)) return
        val products = repository.products()
        updateParams { copy(products = products) }
        savedStateHandle.set(key = KEY_PRODUCTS, value = products.map(ProductListUi::from))
    }

    private suspend fun initFilters() {
        if (savedStateHandle.contains(KEY_FILTERS)) return
        val filters = repository.filters()
        updateParams { copy(filters = filters) }
        val uiFilters = filters.map(FilterUi::from)
        savedStateHandle.set(key = KEY_FILTERS, value = uiFilters)
        _filtersUiListStateFlow.value = uiFilters
        updateFilters()
    }

    private suspend fun initOrders() {
        if (savedStateHandle.contains(KEY_ORDERS)) return
        val orders = repository.orderList()
        updateParams { copy(orders = orders) }
        savedStateHandle.set(key = KEY_ORDERS, value = orders)
        _ordersUiListStateFlow.value = orders.map(OrderUi::from)
        updateOrder()
    }

    fun chooseOrder(name: String) {
        updateParams { copy(selectedOrder = OrderUi.Order.from(name)) }
        updateOrder()
        updateProducts()
    }

    private fun updateOrder() {
        val selectedOrder = _params.get().selectedOrder
        val updatedOrders = ordersUiListStateFlow.value.map {
            it.copy(
                chosen = it.name == selectedOrder.code
            )
        }
        savedStateHandle.set(
            key = KEY_ORDERS,
            value = updatedOrders
        )
        _ordersUiListStateFlow.value = updatedOrders
    }

    fun chooseFilter(id: Int) {
        val filter = _params.get().filters.find { it.id == id } ?: return
        updateParams {
            copy(selectedFilters = selectedFilters.toMutableMap().let {
                it[filter.category] = id
                return@let it.toMap()
            })
        }
        updateFilters()
        updateProducts()
    }

    fun unchooseFilter(id: Int) {
        val filter = _params.get().filters.find { it.id == id } ?: return
        updateParams {
            copy(selectedFilters = selectedFilters.toMutableMap().let {
                if (it[filter.category] == id) it.remove(filter.category)
                return@let it.toMap()
            })
        }
        updateFilters()
        updateProducts()
    }

    private fun updateFilters() {
        val selectedFilters = _params.get().selectedFilters
        val updatedFilters = filtersUiListStateFlow.value.map {
            it.copy(chosen = selectedFilters.containsValue(it.id))
        }
        savedStateHandle.set(key = KEY_FILTERS, value = updatedFilters)
        _filtersUiListStateFlow.value = updatedFilters
    }

    private fun updateProducts() {
        val products = getUiProducts()
        _productsUiListStateFlow.value = products
    }

    private fun getUiProducts(): List<ProductListUi> {
        val products = _params.get().products
        val selectedFiltersIds = _params.get().selectedFilters
        val selectedFilters = _params.get().filters.mapNotNull {
            it.takeIf { selectedFiltersIds.containsValue(it.id) }
        }
        val selectedOrder = _params.get().selectedOrder
        val productsUi = products
            .let { //Apply filters
                products.filter { product ->
                    selectedFilters.all { filter ->
                        when (filter.categoryFormatted) {
                            ProductFilter.Category.OS -> filter.name == product.os
                            ProductFilter.Category.RAM -> filter.name == product.ram.toString()
                            ProductFilter.Category.Unknown -> true
                        }
                    }
                }
            }.let { //Apply order
                when (selectedOrder) {
                    OrderUi.Order.Alphabet -> it.sortedBy { product -> product.name }
                    OrderUi.Order.PriceLowToHigh -> it.sortedBy { product -> product.priceFormatted }
                    OrderUi.Order.PriceHighToLow -> it.sortedByDescending { product -> product.priceFormatted }
                    OrderUi.Order.Unknown,
                    null -> it
                }
            }.map(ProductListUi::from).takeIf {
                it.isNotEmpty()
            } ?: listOf(ProductListUi.Empty)
        return productsUi
    }

    private fun updateParams(reducer: Params.() -> Params) {
        _params.set(reducer(_params.get()))
    }

    data class Params(
        val products: List<Product> = listOf(),
        val filters: List<ProductFilter> = listOf(),
        val orders: List<String> = listOf(),
        val selectedFilters: Map<String, Int> = mapOf(),
        val selectedOrder: OrderUi.Order = OrderUi.Order.Alphabet,
    )

    sealed interface Command {
        object Init : Command
    }

}