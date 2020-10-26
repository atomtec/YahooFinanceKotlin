package com.f11labz.yahooapi

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

private val  DOLLAR_FORMAT_WITH_PLUS by lazy { NumberFormat.getCurrencyInstance(Locale.US)
        as DecimalFormat }.also {
    it.value.positivePrefix = "+$"
}

private val DOLLAR_FORMAT = NumberFormat.getCurrencyInstance(Locale.US)
        as DecimalFormat

private val PERCENTAGE_FORMAT by lazy { NumberFormat.getPercentInstance(Locale.getDefault())
 as DecimalFormat }.also {
    it.value.positivePrefix = "+"
    it.value.maximumFractionDigits = 2
    it.value.minimumFractionDigits = 2
}
fun formatToCurrency(price:Float):String{
    return DOLLAR_FORMAT.format(price)
}

fun formatToCurrencyWithPlus(change:Float):String {
    return DOLLAR_FORMAT_WITH_PLUS.format(change)
}

fun formatToPercentage(percentChange:Float):String{
    return PERCENTAGE_FORMAT.format(percentChange)
}

