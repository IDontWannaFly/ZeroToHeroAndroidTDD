package ru.easycode.zerotoheroandroidtdd.domain

import ru.easycode.zerotoheroandroidtdd.domain.model.Product
import ru.easycode.zerotoheroandroidtdd.domain.model.ProductFilter

interface ProductsRepository {
    suspend fun products(): List<Product>
    suspend fun orderList(): List<String>
    suspend fun filters(): List<ProductFilter>
}