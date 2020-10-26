package com.f11labz.yahooapi.data.network.sdk

import com.f11labz.yahooapi.data.network.NetWorkStock
import com.f11labz.yahooapi.data.network.RemoteDataContract
import yahoofinance.YahooFinance

/**
 * This provider is backed by Yahoo Finance SDK , we can have many other
 * providers backed by direct API for example
 */


object RemoteStockProviderSDK : RemoteDataContract {

    override suspend fun getStocksBySymbol(stockSymbols: List<String>): List<NetWorkStock> {
        val networkStocks = arrayListOf<NetWorkStock>()
        val symbolArray = stockSymbols.toTypedArray()
        val  quotes = YahooFinance.get(symbolArray)
        for ((symbol,stock) in quotes){
            stock?.let {
                val quote = it.quote
                quote?.let {
                    quote.price?.let {
                        val price: Float = quote.price.toFloat()
                        val change: Float = quote.change.toFloat()
                        val percentChange: Float = quote.changeInPercent.toFloat()
                        val remoteStock =
                            NetWorkStock(symbol, price, percentChange, change, stock.name)
                        networkStocks.add(remoteStock)
                    }
                }
            }
        }
        return networkStocks;
    }

}

