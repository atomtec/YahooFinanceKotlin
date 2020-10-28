package com.f11labz.yahooapi.data.sync

import android.util.Log
import androidx.work.*
import java.text.SimpleDateFormat
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

    private val tz = TimeZone.getTimeZone("America/New_York")
    private val cal = Calendar.getInstance(tz)
    private val startTime = "09:30:00"
    private val stopTime = "16:30:00"
    private val formatter: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")
    private val dateFrom = formatter.parse(startTime)
    private val dateTo = formatter.parse(stopTime)

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

    private fun hasMarketStarted(): Boolean{
        val timeNow  = String.format("%02d" , cal.get(Calendar.HOUR_OF_DAY))+":"+
        String.format("%02d" , cal.get(Calendar.MINUTE))+":"+
                String.format("%02d" , cal.get(Calendar.SECOND))

        val dateNow: Date? = formatter.parse(timeNow)
        Log.i(TAG,"NewYork Time Now is " + timeNow)//Doing for US add other regions later
        val isTimeWithinRange = dateFrom.before(dateNow) && dateTo.after(dateNow)
        val isWeekend = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        Log.i(TAG,"Day of the week  " + cal.get(Calendar.DAY_OF_WEEK))
        return isTimeWithinRange && !isWeekend

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
                if(hasMarketStarted()){ //Sync Only when Market has started trading
                   fetchNow()
                }
                else{
                    Log.i(TAG,"Market has not started no sync required")
                }

            }
        }, 1000, SYNC_INTERVAL.toLong()) //every 15 seconds*/
    }

    fun stopLiveSync() {
        Log.i(TAG, "LiveSync() Stop")
        mLiveTimer?.cancel()
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