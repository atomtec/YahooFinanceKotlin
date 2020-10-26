package com.f11labz.yahooapi.data.domain

/**
 * This class is the domain for this App , used to show in the UI
 * be manipulated
 */

data class AppStock(
    val symbol: String? = null,
    val price: Float = 0f,
    val percentchange: Float = 0f,
    val absolutechange: Float = 0f,
    val stockname: String? = null

) {}



