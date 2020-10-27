package com.f11labz.yahooapi.data.domain

/**
 * This class is the domain for this App , used to show in the UI
 * be manipulated
 */

data class AppStock(
    val symbol: String = "",
    val price: Float = 0f,
    val percentchange: Float = 0f,
    val absolutechange: Float = 0f,
    val stockname: String = "",
    val postmarketpercentchange : Float = 0f,
    val postmarketabsolutechange : Float = 0f

) {}



