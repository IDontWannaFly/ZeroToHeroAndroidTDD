package ru.easycode.zerotoheroandroidtdd.ui.products.dvo

import android.os.Parcelable
import ru.easycode.zerotoheroandroidtdd.domain.model.Product
import java.io.Serializable

internal sealed interface ProductListUi : Serializable {

    data class Base(
        val id: Int,
        val name: String,
        val price: String,
        val os: String,
        val ram: Int,
    ) : ProductListUi

    object Empty : ProductListUi {
        private fun readResolve(): Any = Empty
    }

    fun toModel() = when (this) {
        is Base -> Product(
            id = id,
            name = name,
            price = price,
            os = os,
            ram = ram,
        )
        Empty -> null
    }

    companion object {
        fun from(model: Product): ProductListUi {
            return Base(
                id = model.id,
                name = model.name,
                price = model.price,
                os = model.os,
                ram = model.ram
            )
        }
    }
}