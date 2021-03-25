package com.f11labz.yahooapi.data.repository

import androidx.lifecycle.*
import com.f11labz.yahooapi.data.database.*
import com.f11labz.yahooapi.data.network.asDataBaseModel
import com.f11labz.yahooapi.data.network.sdk.RemoteStockProviderSDK
import com.f11labz.yahooapi.data.domain.AppStock
import com.f11labz.yahooapi.data.network.api.RemoteStockProviderAPI
import com.f11labz.yahooapi.data.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception

class StockRepository(private val stockDao: StockDao){

    enum class SearchStockStatus { LOADING, ERROR, DONE , NOTFOUND}

    private val dbStocks : Flow <List<StockEntity>>
        get() = stockDao.getAllStocks()


    val appStock :Flow<List<AppStock>> = dbStocks.map { it.asDomainModel() }
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<SearchStockStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<SearchStockStatus>
        get() = _status

    /**
     * Refresh Stock in DB
     */

    suspend fun refreshStocks(){
        withContext(Dispatchers.IO){

            val dbStocks = stockDao.getAllStocks().first()
            try {
                val remoteStock =
                    RemoteStockProviderAPI.getStocksBySymbol(dbStocks.asListOfSymbols())
                stockDao.insertAll(remoteStock.asDataBaseModel())
            } catch (Ex: Exception) {
                //Just LOg as this will be background
            }

        }
    }

    suspend fun searchAndAddStock(symbol: String){
        withContext(Dispatchers.IO){
            _status.postValue(SearchStockStatus.LOADING)
            try {
                val stock = RemoteStockProviderAPI.getStocksBySymbol(listOf(symbol))
                if(stock.size > 0){
                    _status.postValue(SearchStockStatus.DONE)
                    stockDao.insertAll(stock.asDataBaseModel())
                }
                else{
                    _status.postValue(SearchStockStatus.NOTFOUND)
                }

            }
            catch (Ex : Exception){
                //TODO Catch and show and show appropriate message
                _status.postValue(SearchStockStatus.ERROR)
            }

        }

    }

    fun startSync(){
        SyncManager.getInstance().startLiveSync()
    }

    fun stopSync(){
        SyncManager.getInstance().stopLiveSync()
    }
}
