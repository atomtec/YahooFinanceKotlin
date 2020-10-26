package com.f11labz.yahooapi.data.network.api

import androidx.lifecycle.ViewModelProvider
import com.f11labz.yahooapi.data.database.getDatabase
import com.f11labz.yahooapi.data.network.NetWorkStock
import com.f11labz.yahooapi.data.network.RemoteDataContract
import com.f11labz.yahooapi.data.repository.StockRepository
import com.f11labz.yahooapi.stocklist.StockViewModel
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

private const val API_KEY = "8db4edc1a4mshe03ec111dd29273p102f8cjsnaa461ee69aed"

interface YahooFinanceService {
    @Headers(
        "x-rapidapi-host: ${BASE_URL}",
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
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)).build())
        .build()

    val quoteservice = retrofit.create(YahooFinanceService::class.java)


    @Throws(IOException::class)
    override suspend fun getStocksBySymbol(stockSymbols: List<String>): List<NetWorkStock> {
        val queryOptions : HashMap<String, String> = HashMap()
        queryOptions.put("region","US")
        val symbolString : StringBuilder = java.lang.StringBuilder()
        stockSymbols.forEach {
            symbolString.append(it)
            symbolString.append(",")
        }
        queryOptions.put("symbols",symbolString.toString())
        val quotes : ApiResponseContainer = quoteservice.getQuotes(queryOptions)
        return quotes.quoteResonse.result.asNetWorkModel()
    }
}