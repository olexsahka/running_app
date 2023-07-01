package com.example.runnningpetproject.adapters

import android.icu.util.Calendar
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runnningpetproject.R
import com.example.runnningpetproject.databinding.ItemRunBinding
import com.example.runnningpetproject.db.Run
import com.example.runnningpetproject.utlis.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter: RecyclerView.Adapter<RunAdapter.ViewHolder>() {


    class ViewHolder(val itemRunBinding: ItemRunBinding) : RecyclerView.ViewHolder(itemRunBinding.root) {

    }

    val diffCallback = object :DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRunBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.itemRunBinding.apply {
            Glide.with(this.root).load(run.img).into( ivRunImage)
            val calendar = Calendar.getInstance().apply {
                 timeInMillis = run.timeMillis
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text =dateFormat.format(calendar.time)
            val avgSpeed = "${run.avgSpeed} km/h"
            tvAvgSpeed.text = avgSpeed
            val distance = "${run.distance /1000} km"
            tvDistance.text = distance
            tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeMillis,false)
            val calories = "${run.calories} kcal"
            tvCalories.text = calories
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}