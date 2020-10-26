package com.f11labz.yahooapi.data.sync

import android.util.Log
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Sync stratgey is like this , when the app is opened
 * we will poll after every xxx seconds and when the app is closed
 * all sync will be closed
 */
class SyncManager //Singleton
private constructor() {
    private var mLiveTimer: Timer? = null
    private val networkContraints: Constraints
         get() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    //Unique fetchnow to avid processing dupliacte request
    fun fetchNow() {
        Log.i(TAG, "syncNow() started")
        val oneShotRequest: WorkRequest = OneTimeWorkRequest.Builder(RefreshStockWorker::class.java)
            .setConstraints(networkContraints)
            .addTag("one_shot_tag")
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()

        WorkManager
            .getInstance()
            .enqueueUniqueWork(
                "one_shot_unique", ExistingWorkPolicy.KEEP,
                (oneShotRequest as OneTimeWorkRequest)
            )
    }



    /**
     * PeriodicWorkRequest have minimum time of 15 minutes
     * so here I am doing it manually till the app is open in foreground
     * once the app is stopped sync stops
     * Syncing every 15 seconds
     */
    fun startLiveSync() {
        Log.i(TAG, "startLiveSync() started")
        if (mLiveTimer == null) mLiveTimer = Timer()
        mLiveTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                fetchNow()
            }
        }, 1000, SYNC_INTERVAL.toLong()) //every 15 seconds
    }

    fun stopLiveSync() {
        Log.i(TAG, "LiveSync() Stop")
        mLiveTimer?.let{
            it.cancel()
        }
        mLiveTimer = null
        WorkManager.getInstance().cancelAllWork()
    }

    companion object {
        private lateinit var INSTANCE: SyncManager
        private const val SYNC_INTERVAL = 15000
        private val TAG = SyncManager::class.java.simpleName

        @Synchronized
        fun getInstance(): SyncManager {
            if (!::INSTANCE.isInitialized)  {
                INSTANCE = SyncManager()
            }
            return INSTANCE
        }
    }

}