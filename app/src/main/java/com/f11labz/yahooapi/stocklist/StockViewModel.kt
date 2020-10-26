package com.f11labz.yahooapi.stocklist

import androidx.lifecycle.*
import com.f11labz.yahooapi.data.domain.AppStock
import com.f11labz.yahooapi.data.repository.StockRepository
import kotlinx.coroutines.launch
import java.io.IOException

class StockViewModel(private val repository: StockRepository) : ViewModel(),LifecycleObserver{



    val status: LiveData<StockRepository.SearchStockStatus> = repository.status


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        repository.startSync()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        repository.stopSync()
    }
    //Get DB Data
    private val _stocks = repository.appStock
    val stocks : LiveData<List<AppStock>>
        get() = _stocks

    init {
        refreshStock()
    }

    private fun refreshStock() {
        viewModelScope.launch {
            startRefresh()
        }
    }

    private suspend fun startRefresh(){
        repository.refreshStocks()
    }

    fun addStock(symbol: String){
        viewModelScope.launch {
            searchAndAddStock(symbol)
        }
    }

    private suspend fun searchAndAddStock(symbol : String ){
         repository.searchAndAddStock(symbol)
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val repository: StockRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StockViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StockViewModel(repository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}