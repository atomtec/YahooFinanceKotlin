package com.f11labz.yahooapi.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.f11labz.yahooapi.data.database.getDatabase
import com.f11labz.yahooapi.data.repository.StockRepository

class RefreshStockWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val dao = getDatabase(applicationContext).stockDao
        val repository = StockRepository(dao)
        try {
            repository.refreshStocks()
        } catch (e: Exception) {
            return Result.retry()
        }
        return Result.success()
    }
}