package com.example.runnningpetproject.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.runnningpetproject.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StaticsViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {
    val totalTimeRun = mainRepository.getTotalTimeInMillis()
    val totalDistanceRun = mainRepository.getTotalDistance()
    val totalCaloriesRun = mainRepository.getTotalCalories()
    val totalAvgSpeedRun = mainRepository.getTotalAvgSpeed()


    val runsSortedByDate = mainRepository.getAllRunsSortedByDate()

}