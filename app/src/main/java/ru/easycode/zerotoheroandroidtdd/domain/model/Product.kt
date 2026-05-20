package ru.easycode.zerotoheroandroidtdd.domain.model

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val os: String,
    val ram: Int,
) {
    val priceFormatted: Double
        get() = price.replace("$", "").toDouble()
}