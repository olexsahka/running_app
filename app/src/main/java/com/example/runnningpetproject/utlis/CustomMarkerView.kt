package com.example.runnningpetproject.utlis

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.runnningpetproject.R
import com.example.runnningpetproject.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    val runs: List<Run>,
    c: Context,
    layoutId: Int
): MarkerView(c,layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f,-height.toFloat())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null)
            return
        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeMillis
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        (findViewById<TextView>(R.id.tvDate)).text =dateFormat.format(calendar.time)
        val avgSpeed = "${run.avgSpeed} km/h"
        (findViewById<TextView>(R.id.tvAvgSpeed)).text = avgSpeed
        val distance = "${run.distance /1000} km"
        (findViewById<TextView>(R.id.tvDistance)).text = distance
        (findViewById<TextView>(R.id.tvDuration)).text = TrackingUtility.getFormattedStopWatchTime(run.timeMillis,false)
        val calories = "${run.calories} kcal"
        (findViewById<TextView>(R.id.tvCaloriesBurned)).text = calories

    }
}