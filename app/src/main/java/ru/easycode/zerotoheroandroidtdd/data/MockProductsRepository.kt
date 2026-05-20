package ru.easycode.zerotoheroandroidtdd.data

import ru.easycode.zerotoheroandroidtdd.domain.ProductsRepository
import ru.easycode.zerotoheroandroidtdd.domain.model.Product
import ru.easycode.zerotoheroandroidtdd.domain.model.ProductFilter

internal class MockProductsRepository : ProductsRepository {
    override suspend fun products(): List<Product> {
        return listOf(
            Product(id = 1, name = "Device A", price = "300$", os = "Android", ram = 6),
            Product(id = 2, name = "Device C", price = "200$", os = "Android", ram = 4),
            Product(id = 3, name = "Device B", price = "400$", os = "iOS", ram = 6),
            Product(id = 4, name = "Device D", price = "500$", os = "iOS", ram = 8)
        )
    }

    override suspend fun orderList(): List<String> {
        return listOf(
            "alphabet",
            "price: low to high",
            "price: high to low",
        )
    }

    override suspend fun filters(): List<ProductFilter> {
        return listOf(
            ProductFilter(id = 1, category = "os", name = "Android"),
            ProductFilter(id = 2, category = "os", name = "iOS"),
            ProductFilter(id = 3, category = "RAM", name = "4"),
            ProductFilter(id = 4, category = "RAM", name = "6"),
            ProductFilter(id = 5, category = "RAM", name = "8"),
        )
    }
}