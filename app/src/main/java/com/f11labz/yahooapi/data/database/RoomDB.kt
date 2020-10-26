package com.f11labz.yahooapi.data.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Keeping all DB related stuff in this file
 */
@Dao
interface StockDao {

    @Query("SELECT * from stock_table ORDER BY symbol ASC")
    fun getAllStocks(): LiveData<List<StockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( stocks: List<StockEntity>)

    @Query("SELECT * from stock_table ORDER BY symbol ASC")
    suspend fun getAllStocksSuspend(): List<StockEntity> //Non Live Data version for

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stock: StockEntity?)

    @Query("DELETE FROM stock_table")
    fun deleteAll() //TODO add delete by symbol
}

@Database(entities = [StockEntity::class], version = 1)
abstract class StockDataBase: RoomDatabase() {
    abstract val stockDao: StockDao
}

private lateinit var INSTANCE: StockDataBase
//Toplevel
fun getDatabase(context: Context): StockDataBase {
    synchronized(StockDataBase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                StockDataBase::class.java,
                "stockdatabase")
                .fallbackToDestructiveMigration() //Not defining DB migration for this exercise
                .build()
        }
    }
    return INSTANCE
}