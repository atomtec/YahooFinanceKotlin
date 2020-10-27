package com.f11labz.yahooapi.data.network.api

import androidx.lifecycle.ViewModelProvider
import com.f11labz.yahooapi.data.database.getDatabase
import com.f11labz.yahooapi.data.network.NetWorkStock
import com.f11labz.yahooapi.data.network.RemoteDataContract
import com.f11labz.yahooapi.data.repository.StockRepository
import com.f11labz.yahooapi.stocklist.StockViewModel
import com.squareup.moshi.JsonDataException
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap

private const val BASE_URL = "https://apidojo-yahoo-finance-v1.p.rapidapi.com"
private const val API_HOST = "apidojo-yahoo-finance-v1.p.rapidapi.com"
//Leaving it here for demo need to hide it.
private const val API_KEY = "8db4edc1a4mshe03ec111dd29273p102f8cjsnaa461ee69aed"

interface YahooFinanceService {
    @Headers(
        "x-rapidapi-host: ${API_HOST}",
        "x-rapidapi-key: ${API_KEY}"
    )
    @GET("/market/v2/get-quotes")
    suspend fun getQuotes(
        @QueryMap params: Map<String, String> ): ApiResponseContainer
}

object RemoteStockProviderAPI : RemoteDataContract {

    private val logging = HttpLoggingInterceptor()

    private val httpClient =  OkHttpClient.Builder();

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .client(httpClient.addInterceptor(
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)).build())//Remove from release
        .build()

    val quoteservice = retrofit.create(YahooFinanceService::class.java)


    @Throws(IOException::class)
    override suspend fun getStocksBySymbol(stockSymbols: List<String>): List<NetWorkStock> {
        val queryOptions : HashMap<String, String> = HashMap()
        var fetchedStocks = listOf<NetWorkStock>()
        queryOptions.put("region","US")
        if(stockSymbols.isNotEmpty()) {
            val symbolString: StringBuilder = java.lang.StringBuilder()
            stockSymbols.forEach {
                symbolString.append(it)
                symbolString.append(",")
            }
            queryOptions.put("symbols", symbolString.toString())
            try {
                val quotes: ApiResponseContainer = quoteservice.getQuotes(queryOptions)
                fetchedStocks = quotes.quoteResponse.result.asNetWorkModel()
            } catch (ex: JsonDataException) {
                //As per the api if the always present fields are missing
                //stock is invalid , the sometimes missing fields are
                //taken care in data classes

            }
        }

        return fetchedStocks
    }
}