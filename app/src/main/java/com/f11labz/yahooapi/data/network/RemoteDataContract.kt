package com.f11labz.yahooapi.data.network

import java.io.IOException

/**
 * All remote data providers should implement
 * this interface be it API or SDK
 */
interface RemoteDataContract {

    @Throws(IOException::class)
    suspend fun getStocksBySymbol(stockSymbols: List<String>): List<NetWorkStock>
}