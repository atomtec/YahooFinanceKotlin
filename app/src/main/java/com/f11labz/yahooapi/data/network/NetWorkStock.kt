package com.f11labz.yahooapi.data.network

import com.f11labz.yahooapi.data.database.StockEntity

data class NetWorkStock(
    val symbol: String,
    val price: Float ,
    val percentchange: Float ,
    val absolutechange: Float ,
    val stockname: String,
    val postmarketprice : Float = Float.MIN_VALUE, //When not available do not show
    val postmarketchange : Float = Float.MIN_VALUE //When not available do not show

) {}

/**
 * Map StockEntity to domain Entity
 */

fun List<NetWorkStock>.asDataBaseModel(): List<StockEntity> {
    return map {
        StockEntity(
            symbol = it.symbol,
            price = it.price,
            percentchange = it.percentchange,
            absolutechange = it.absolutechange,
            stockname = it.stockname ,
            postmarketprice = it.postmarketprice,
            postmarketchange = it.postmarketchange
        )
    }
}