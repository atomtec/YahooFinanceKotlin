package com.f11labz.yahooapi.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.f11labz.yahooapi.data.database.StockDataBase
import com.f11labz.yahooapi.data.database.asDomainModel
import com.f11labz.yahooapi.data.database.asListOfSymbols
import com.f11labz.yahooapi.data.network.asDataBaseModel
import com.f11labz.yahooapi.data.network.sdk.RemoteStockProviderSDK
import com.f11labz.yahooapi.data.domain.AppStock
import com.f11labz.yahooapi.data.network.api.RemoteStockProviderAPI
import com.f11labz.yahooapi.data.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception

class StockRepository(private val database: StockDataBase){

    enum class SearchStockStatus { LOADING, ERROR, DONE , NOTFOUND}

    private val dbStocks = database.stockDao.getAllStocks()
    val appStock:LiveData<List<AppStock>> = Transformations
        .map(dbStocks)
    {
       it.asDomainModel()
    }

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

            val dbStocks = database.stockDao.getAllStocksSuspend()
            try{
                val remoteStock = RemoteStockProviderAPI.getStocksBySymbol(dbStocks.asListOfSymbols())
                database.stockDao.insertAll(remoteStock.asDataBaseModel())
            }
            catch (Ex : Exception){
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
                    database.stockDao.insertAll(stock.asDataBaseModel())
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
