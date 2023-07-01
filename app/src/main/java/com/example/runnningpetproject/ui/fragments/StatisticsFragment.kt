package com.example.runnningpetproject.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runnningpetproject.R
import com.example.runnningpetproject.databinding.FragmentSettingsBinding
import com.example.runnningpetproject.databinding.FragmentSetupBinding
import com.example.runnningpetproject.databinding.FragmentStatisticsBinding
import com.example.runnningpetproject.ui.viewModels.MainViewModel
import com.example.runnningpetproject.ui.viewModels.StaticsViewModel
import com.example.runnningpetproject.utlis.CustomMarkerView
import com.example.runnningpetproject.utlis.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding: FragmentStatisticsBinding get() = _binding!!

    private val viewModel: StaticsViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    private fun subscribeToObserver(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner) {
            it?.let {
                val total = TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = total
            }
        }
        viewModel.totalDistanceRun.observe(viewLifecycleOwner) {
            it?.let {
                val total = it /1000f
                binding.tvTotalDistance.text = "${(round( total*10f)/10f)} km"
            }
        }

        viewModel.totalCaloriesRun.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvTotalCalories.text = "${it} kcal"

            }
        }

        viewModel.totalAvgSpeedRun.observe(viewLifecycleOwner) {
            it?.let {
                val total = round(it*10f) /10f

                binding.tvAverageSpeed.text = "$total km/h"

            }
        }
        viewModel.runsSortedByDate.observe(viewLifecycleOwner){
            it?.let {
               val allAvgSpeed =it.indices.map { i ->BarEntry(i.toFloat(), it[i].avgSpeed) }
               val barDataSet = BarDataSet(allAvgSpeed,"Avg Speed Over Time").apply {
                   valueTextColor =Color.WHITE
                   color = ContextCompat.getColor(requireContext(),R.color.colorAccent)
               }
                binding.barChart.data = BarData(barDataSet)
                binding.barChart.invalidate()
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserver()
        setupBarChart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(
            inflater,
            container,
            false)
        return binding.root
    }

    private fun setupBarChart(){
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)        }

        binding.barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)        }

        binding.barChart.apply {
            description.text = "Avg Speed OverTime"
            legend.isEnabled = false
        }
    }
}