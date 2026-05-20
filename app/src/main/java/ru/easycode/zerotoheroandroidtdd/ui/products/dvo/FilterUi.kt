package ru.easycode.zerotoheroandroidtdd.ui.products.dvo

import ru.easycode.zerotoheroandroidtdd.domain.model.ProductFilter
import java.io.Serializable

data class FilterUi(
    val id: Int,
    val category: String,
    val value: String,
    val chosen: Boolean,
) : Serializable {

    fun toModel() = ProductFilter(
        id = id,
        category = category,
        name = value
    )

    companion object {
        fun from(model: ProductFilter) = FilterUi(
            id = model.id,
            category = model.category,
            value = model.name,
            chosen = false,
        )
    }
}