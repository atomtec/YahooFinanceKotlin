package com.f11labz.yahooapi

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

private val  NUMBER_FORMAT by lazy { NumberFormat.getNumberInstance()
        as DecimalFormat }.also {
    it.value.maximumFractionDigits = 2
    it.value.minimumFractionDigits = 2

}

private val  NUMBER_FORMAT_WITH_PLUS by lazy { NumberFormat.getNumberInstance()
        as DecimalFormat }.also {
    it.value.maximumFractionDigits = 2
    it.value.minimumFractionDigits = 2
    it.value.positivePrefix = "+"

}

private val PERCENTAGE_FORMAT by lazy { NumberFormat.getPercentInstance(Locale.getDefault())
 as DecimalFormat }.also {
    it.value.positivePrefix = "+"
    it.value.maximumFractionDigits = 2
    it.value.minimumFractionDigits = 2
}
fun formatToNumber(price:Float):String{
    return NUMBER_FORMAT.format(price)
}

fun formatToNumberWithPlus(price:Float):String{
    return NUMBER_FORMAT_WITH_PLUS.format(price)
}


fun formatToPercentage(percentChange:Float):String{
    return PERCENTAGE_FORMAT.format(percentChange)
}

