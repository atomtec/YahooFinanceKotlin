package com.f11labz.yahooapi.data.repository

import com.f11labz.yahooapi.data.database.StockDao
import com.f11labz.yahooapi.data.database.StockEntity
import com.f11labz.yahooapi.data.database.asDomainModel
import com.f11labz.yahooapi.data.database.asListOfSymbols
import com.f11labz.yahooapi.data.domain.AppStock
import com.f11labz.yahooapi.data.network.api.RemoteStockProviderAPI
import com.f11labz.yahooapi.data.network.asDataBaseModel
import com.f11labz.yahooapi.data.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class StockRepository(private val stockDao: StockDao){

    enum class SearchStockStatus { LOADING, ERROR, DONE , NOTFOUND,NONE}

    private val dbStocks : Flow <List<StockEntity>>
        get() = stockDao.getAllStocks()


    val appStock :Flow<List<AppStock>> = dbStocks.map { it.asDomainModel() }

    // The internal MutableLiveData that stores the status of the most recent request
    private  var _status = MutableStateFlow<SearchStockStatus>(SearchStockStatus.NONE)

    // The external immutable LiveData for the request status
    val status: StateFlow<SearchStockStatus>
        get() = _status.asStateFlow()

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

    suspend fun searchAndAddStock(symbol: String) {
        withContext(Dispatchers.IO) {
            _status.value = SearchStockStatus.LOADING
                try {
                    val stock = RemoteStockProviderAPI.getStocksBySymbol(listOf(symbol))
                    if (stock.size > 0) {
                        _status.value = SearchStockStatus.DONE
                        stockDao.insertAll(stock.asDataBaseModel())
                    } else {
                        _status.value = SearchStockStatus.NOTFOUND
                    }

                } catch (Ex: Exception) {
                    //TODO Catch and show and show appropriate message
                    _status.value = SearchStockStatus.ERROR
                }
        }
    }




   /* suspend fun searchAndAddStock(symbol: String){
        withContext(Dispatchers.IO){
            _status.collect {  }
            _status.emit(SearchStockStatus.LOADING)
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

    }*/

    fun startSync(){
        SyncManager.getInstance().startLiveSync()
    }

    fun stopSync(){
        SyncManager.getInstance().stopLiveSync()
    }
}
