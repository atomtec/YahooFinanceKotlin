package com.f11labz.yahooapi.data.network.api


import com.f11labz.yahooapi.data.network.NetWorkStock
import com.squareup.moshi.JsonClass


/**
 * Json Classes to convert Yahoo FinanceAPI
 * {1 item
    "quoteResponse":{2 items
    "result":[...]1 item
    "error":NULL
     }
    }
 */
@JsonClass(generateAdapter = true)
data class ApiResponseContainer(
    val quoteResponse : QuoteResponse
)

@JsonClass(generateAdapter = true)
data class QuoteResponse(
    val result : List<Result>,
    val error : String?
)

@JsonClass(generateAdapter = true)
data class Result(
    val regularMarketChange : Float,
    val regularMarketChangePercent : Float,
    val postMarketPrice : Float?, //This will not  be present during trading
    val postMarketChange : Float?, //This will not be present during trading
    val regularMarketPrice :Float,
    val shortName : String,
    val symbol : String
)

fun List<Result>.asNetWorkModel(): List<NetWorkStock>{
    return map{
        NetWorkStock(
            symbol = it.symbol,
            price = it.regularMarketPrice,
            percentchange = it.regularMarketChangePercent,
            absolutechange = it.regularMarketChange,
            stockname = it.shortName,
            postmarketprice = it.postMarketPrice?: Float.MIN_VALUE,
            postmarketchange = it.postMarketChange?: Float.MIN_VALUE
        )
    }
}

