package com.f11labz.yahooapi.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.f11labz.yahooapi.data.domain.AppStock

@Entity(tableName = "stock_table")
data class StockEntity (
    @PrimaryKey
    val symbol: String,
    val price: Float,
    val percentchange: Float,
    val absolutechange: Float,
    val stockname: String,
    val postmarketprice : Float,
    val postmarketchange : Float
) { }

/**
 * Map StockEntity to domain Entity
 */

fun List<StockEntity>.asDomainModel(): List<AppStock> {
    return map {
        AppStock(
            symbol = it.symbol,
            price = it.price,
            percentchange = it.percentchange,
            absolutechange = it.absolutechange,
            stockname = it.stockname,
            postmarketpercentchange = it.postmarketchange,
            postmarketabsolutechange = it.postmarketprice
        )
    }
}

fun List<StockEntity>.asListOfSymbols(): List<String> {
    return map {
        it.symbol
    }
}