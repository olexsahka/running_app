package com.example.runnningpetproject.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "runningTable")
data class Run (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var img: Bitmap? ,
    var timestampStart: Long,
    var avgSpeed: Float,
    var distance: Float,
    var timeMillis: Long,
    var calories: Int
){

}