package com.example.runnningpetproject.repositories

import com.example.runnningpetproject.db.Run
import com.example.runnningpetproject.db.RunDao
import javax.inject.Inject


class MainRepository @Inject constructor(
    val runDao: RunDao
) {
    suspend fun insertRun(run: Run)  = runDao.add(run)

    suspend fun deleteRun(run:Run) = runDao.delete(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunSortedByDate()

    fun getAllRunsSortedByDistance() = runDao.getAllRunSortedByDistance()

    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunSortedByMillis()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunSortedByAvgSpeed()

    fun getAllRunsSortedByCalories() = runDao.getAllRunSortedByCalories()

    fun getTotalAvgSpeed() =runDao.getTotalAvgSpeed()

    fun getTotalDistance() =runDao.getTotalDistance()

    fun getTotalTimeInMillis() =runDao.getTotalTimeMillis()

    fun getTotalCalories() =runDao.getTotalCalories()





}