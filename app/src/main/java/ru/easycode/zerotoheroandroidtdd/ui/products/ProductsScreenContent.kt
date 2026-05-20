package ru.easycode.zerotoheroandroidtdd.ui.products

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.easycode.zerotoheroandroidtdd.ui.components.MainButton
import ru.easycode.zerotoheroandroidtdd.ui.components.MainOutlinedButton
import ru.easycode.zerotoheroandroidtdd.ui.products.dvo.Dialog
import ru.easycode.zerotoheroandroidtdd.ui.products.dvo.FilterUi
import ru.easycode.zerotoheroandroidtdd.ui.products.dvo.OrderUi
import ru.easycode.zerotoheroandroidtdd.ui.products.dvo.ProductListUi
import ru.easycode.zerotoheroandroidtdd.ui.theme.ZeroToHeroAndroidTDDTheme

@Composable
internal fun ProductsScreenContent(
    products: List<ProductListUi>,
    orders: List<OrderUi>,
    filters: List<FilterUi>,
    onOrderSelected: (OrderUi) -> Unit,
    onFiltersUpdated: (List<Int>) -> Unit
) {
    var dialogToShow by remember { mutableStateOf<Dialog?>(null) }
    Scaffold(
        modifier = Modifier,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MainOutlinedButton(
                    modifier = Modifier
                        .testTag("order button"),
                    text = "order",
                    onClick = { dialogToShow = Dialog.Order }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Products",
                )
                Spacer(modifier = Modifier.weight(1f))
                MainOutlinedButton(
                    modifier = Modifier
                        .testTag("filters button"),
                    text = "filters",
                    onClick = { dialogToShow = Dialog.Filter }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .testTag("ProductsLazyColumn")
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(products) { index, item ->
                when (item) {
                    is ProductListUi.Base -> {
                        Column(
                            modifier = Modifier
                                .testTag("Product at $index")
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .testTag("Product name at $index"),
                                text = item.name
                            )
                            Text(
                                modifier = Modifier
                                    .testTag("Product os at $index"),
                                text = item.os
                            )
                            Text(
                                modifier = Modifier
                                    .testTag("Product ram at $index"),
                                text = item.ram.toString()
                            )
                            Text(
                                modifier = Modifier
                                    .testTag("Product price at $index"),
                                text = item.price
                            )
                        }
                    }

                    ProductListUi.Empty -> Text(
                        modifier = Modifier
                            .testTag("nothing found")
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = "Nothing found",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        dialogToShow?.let { dialog ->
            Dialog(
                onDismissRequest = { dialogToShow = null }
            ) {
                Column(
                    modifier = Modifier
                        .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (dialog) {
                        Dialog.Filter -> FiltersDialogContent(
                            filters = filters,
                            onSelectedFiltersUpdated = {
                                onFiltersUpdated(it)
                                dialogToShow = null
                            }
                        )

                        Dialog.Order -> OrdersDialogContent(
                            orders = orders,
                            onOrderSelected = {
                                onOrderSelected(it)
                                dialogToShow = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.OrdersDialogContent(
    orders: List<OrderUi>,
    onOrderSelected: (OrderUi) -> Unit,
) {
    Text(
        text = "Order"
    )
    orders.forEach { order ->
        Text(
            modifier = Modifier
                .testTag("Order option ${order.name}")
                .selectable(
                    selected = order.chosen,
                    onClick = { onOrderSelected(order) }
                )
                .border(
                    width = 2.dp,
                    color = if (order.chosen) {
                        Color.Green
                    } else {
                        Color.Red
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            text = order.name
        )
    }
}

@Composable
private fun ColumnScope.FiltersDialogContent(
    filters: List<FilterUi>,
    onSelectedFiltersUpdated: (List<Int>) -> Unit
) {
    var selectedFilters by remember(filters) {
        mutableStateOf(
            filters.mapNotNull { it.takeIf { it.chosen }?.id }.toSet()
        )
    }
    Text(
        text = "Filters"
    )
    Spacer(modifier = Modifier)
    filters.forEach { filter ->
        val isSelected = selectedFilters.contains(filter.id)
        Row(
            modifier = Modifier
                .testTag("filter ${filter.category} ${filter.value}")
                .selectable(
                    selected = isSelected,
                    onClick = {
                        selectedFilters = selectedFilters.toMutableSet().let {
                            if (isSelected) it.remove(filter.id)
                            else it.add(filter.id)
                            it.toSet()
                        }
                    }
                )
                .border(
                    width = 2.dp,
                    color = if (isSelected) {
                        Color.Green
                    } else {
                        Color.Red
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = filter.category
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = filter.value,
                textAlign = TextAlign.End
            )
        }
    }
    MainButton(
        modifier = Modifier.testTag("save button"),
        text = "save",
        onClick = { onSelectedFiltersUpdated(selectedFilters.toList()) }
    )
}

@Preview
@Composable
private fun ProductsScreenContentPreview() = ZeroToHeroAndroidTDDTheme {
    ProductsScreenContent(
        products = listOf(
            ProductListUi.Base(
                id = 1,
                name = "Device A",
                price = "300$",
                os = "Android",
                ram = 6
            ),
            ProductListUi.Base(id = 3, name = "Device B", price = "400$", os = "iOS", ram = 6),
            ProductListUi.Base(
                id = 2,
                name = "Device C",
                price = "200$",
                os = "Android",
                ram = 4
            ),
            ProductListUi.Base(id = 4, name = "Device D", price = "500$", os = "iOS", ram = 8)
        ),
        orders = listOf(
            OrderUi(name = "alphabet", chosen = true),
            OrderUi(name = "price: low to high", chosen = false),
            OrderUi(name = "price: high to low", chosen = false)
        ),
        filters = listOf(
            FilterUi(id = 1, category = "os", value = "Android", chosen = false),
            FilterUi(id = 2, category = "os", value = "iOS", chosen = false),
            FilterUi(id = 3, category = "RAM", value = "4", chosen = false),
            FilterUi(id = 4, category = "RAM", value = "6", chosen = false),
            FilterUi(id = 5, category = "RAM", value = "8", chosen = false),
        ),
        onOrderSelected = { },
        onFiltersUpdated = { }
    )
}