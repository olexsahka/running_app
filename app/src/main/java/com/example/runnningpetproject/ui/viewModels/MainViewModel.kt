package com.example.runnningpetproject.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runnningpetproject.db.Run
import com.example.runnningpetproject.repositories.MainRepository
import com.example.runnningpetproject.utlis.Sorted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {

    private val runSortedByDate = mainRepository.getAllRunsSortedByDate()
    private val runSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runSortedByCalories = mainRepository.getAllRunsSortedByCalories()
    private val runSortedByTime = mainRepository.getAllRunsSortedByTimeInMillis()
    private val runSortedByAvgSpeed = mainRepository.getAllRunsSortedByAvgSpeed()

    val runs = MediatorLiveData<List<Run>>()
    var sortType = Sorted.DATE

    init {
        runs.addSource(runSortedByDate){ result ->
            if(sortType == Sorted.DATE){
                result.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByAvgSpeed){ result ->
            if(sortType == Sorted.AVG_SPEED){
                result.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByCalories){ result ->
            if(sortType == Sorted.CALORIES){
                result.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByTime){ result ->
            if(sortType == Sorted.RUNNING_TIME){
                result.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByDistance){ result ->
            if(sortType == Sorted.DISTANCE){
                result.let { runs.value = it }
            }
        }
    }

    fun sortRuns(sorted: Sorted) = when(sorted){
        Sorted.DATE -> runSortedByDate.value.let { runs.value = it }
        Sorted.DISTANCE -> runSortedByDistance.value.let { runs.value = it }
        Sorted.RUNNING_TIME -> runSortedByTime.value.let { runs.value = it }
        Sorted.CALORIES-> runSortedByCalories.value.let { runs.value = it }
        Sorted.AVG_SPEED -> runSortedByAvgSpeed.value.let { runs.value = it }
    }.also {
        this.sortType = sorted
    }


    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
}