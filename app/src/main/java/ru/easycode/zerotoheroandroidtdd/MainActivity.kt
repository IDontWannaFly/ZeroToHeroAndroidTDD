package ru.easycode.zerotoheroandroidtdd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.easycode.zerotoheroandroidtdd.di.ServiceLocator
import ru.easycode.zerotoheroandroidtdd.domain.ProductsRepository
import ru.easycode.zerotoheroandroidtdd.ui.products.ProductsScreen
import ru.easycode.zerotoheroandroidtdd.ui.products.ProductsViewModel
import ru.easycode.zerotoheroandroidtdd.ui.products.dvo.FilterUi
import ru.easycode.zerotoheroandroidtdd.ui.products.dvo.ProductListUi
import ru.easycode.zerotoheroandroidtdd.ui.theme.ZeroToHeroAndroidTDDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZeroToHeroAndroidTDDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel by viewModels<ProductsViewModel> {
                        object : AbstractSavedStateViewModelFactory() {
                            override fun <T : ViewModel> create(
                                key: String,
                                modelClass: Class<T>,
                                handle: SavedStateHandle
                            ): T {
                                return ProductsViewModel(
                                    savedStateHandle = handle,
                                    repository = ServiceLocator.productsRepository,
                                    runAsync = ServiceLocator.runAsync,
                                ) as T
                            }
                        }
                    }
                    ProductsScreen(viewModel = viewModel)
                }
            }
        }
    }
}