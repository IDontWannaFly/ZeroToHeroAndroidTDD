package ru.easycode.zerotoheroandroidtdd.domain.model

data class ProductFilter(
    val id: Int,
    val category: String,
    val name: String,
) {
    val categoryFormatted: Category = Category.from(category)
    enum class Category {
        OS,
        RAM,
        Unknown;

        companion object {
            fun from(value: String): Category {
                return entries.find { it.name.equals(value, ignoreCase = true) } ?: Unknown
            }
        }
    }
}