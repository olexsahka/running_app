package com.example.runnningpetproject.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.google.android.material.circularreveal.CircularRevealHelper.Strategy

@Dao
interface RunDao {
    @Insert(onConflict = REPLACE)
    suspend fun add(run: Run)

    @Delete
    suspend fun delete(run: Run)

    @Query("SELECT * FROM runningTable ORDER BY timestampStart DESC")
    fun getAllRunSortedByDate(): LiveData<List<Run>>

    @Query("SELECT * FROM runningTable ORDER BY timeMillis DESC")
    fun getAllRunSortedByMillis(): LiveData<List<Run>>

    @Query("SELECT * FROM runningTable ORDER BY calories DESC")
    fun getAllRunSortedByCalories(): LiveData<List<Run>>

    @Query("SELECT * FROM runningTable ORDER BY avgSpeed DESC")
    fun getAllRunSortedByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM runningTable ORDER BY distance DESC")
    fun getAllRunSortedByDistance(): LiveData<List<Run>>

    @Query("SELECT SUM(timeMillis) FROM runningTable")
    fun getTotalTimeMillis(): LiveData<Long>

    @Query("SELECT SUM(calories) FROM runningTable")
    fun getTotalCalories(): LiveData<Int>

    @Query("SELECT SUM(distance) FROM runningTable")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(avgSpeed) FROM runningTable")
    fun getTotalAvgSpeed(): LiveData<Float>
}