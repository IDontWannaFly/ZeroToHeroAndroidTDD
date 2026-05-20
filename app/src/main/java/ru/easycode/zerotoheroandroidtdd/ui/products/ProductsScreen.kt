package ru.easycode.zerotoheroandroidtdd.ui.products

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.viewModelFactory

@Composable
internal fun ProductsScreen(viewModel: ProductsViewModel) {
    val products by viewModel.productsUiListStateFlow.collectAsState()
    val filters by viewModel.filtersUiListStateFlow.collectAsState()
    val orders by viewModel.ordersUiListStateFlow.collectAsState()
    ProductsScreenContent(
        products = products,
        orders = orders,
        filters = filters,
        onOrderSelected = { viewModel.chooseOrder(it.name) },
        onFiltersUpdated = { updatedFilters ->
            filters.forEach { filter ->
                if (updatedFilters.contains(filter.id) && filter.chosen.not()) {
                    viewModel.chooseFilter(filter.id)
                } else if (updatedFilters.contains(filter.id).not() && filter.chosen) {
                    viewModel.unchooseFilter(filter.id)
                }
            }
        }
    )
}