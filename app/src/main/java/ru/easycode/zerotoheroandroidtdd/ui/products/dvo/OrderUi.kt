package ru.easycode.zerotoheroandroidtdd.ui.products.dvo

import java.io.Serializable

data class OrderUi(
    val name: String,
    val chosen: Boolean,
) : Serializable {
    enum class Order(val code: String) {
        Alphabet("alphabet"),
        PriceLowToHigh("price: low to high"),
        PriceHighToLow("price: high to low"),
        Unknown("unknown");

        companion object {
            fun from(value: String) =
                entries.find { it.code.equals(value, ignoreCase = true) } ?: Unknown
        }
    }

    companion object {
        fun from(value: String) = OrderUi(
            name = value,
            chosen = false,
        )
    }
}