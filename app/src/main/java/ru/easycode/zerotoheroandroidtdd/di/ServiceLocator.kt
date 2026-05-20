package ru.easycode.zerotoheroandroidtdd.di

import ru.easycode.zerotoheroandroidtdd.data.MockProductsRepository
import ru.easycode.zerotoheroandroidtdd.domain.ProductsRepository
import ru.easycode.zerotoheroandroidtdd.ui.common.DefaultRunAsync
import ru.easycode.zerotoheroandroidtdd.ui.common.RunAsync

internal object ServiceLocator {
    val productsRepository: ProductsRepository = MockProductsRepository()
    val runAsync: RunAsync = DefaultRunAsync()
}