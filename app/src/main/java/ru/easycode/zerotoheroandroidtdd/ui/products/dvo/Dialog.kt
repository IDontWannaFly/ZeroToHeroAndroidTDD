package ru.easycode.zerotoheroandroidtdd.ui.products.dvo

internal sealed interface Dialog {
    object Order : Dialog
    object Filter : Dialog
}