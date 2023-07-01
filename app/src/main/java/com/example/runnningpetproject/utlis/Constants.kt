package com.example.runnningpetproject.utlis

import android.app.NotificationChannel
import android.graphics.Color

object Constants {
    const val RunningDatabaseName = "running_db"
    const val REQUEST_LOCATION_PERMISSIONS = 0
    const val ACTION_START_RESUME = "ACTION_START_RESUME"
    const val ACTION_PAUSE = "ACTION_PAUSE"
    const val ACTION_STOP = "ACTION_STOP"
    const val ACTION_SHOW_FRAGMENT = "ACTION_SHOW_FRAGMENT"

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "tracking"
    const val NOTIFICATION_ID = 123

    const val LOCATION_INTERVAL = 5000L
    const val FASTEST_INTERVAL_LOCATION = 2000L
    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f

    const val TIMER_UPDATE = 50L

    const val SHARED_PREF_NAME ="sharedPref"
    const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"
    const val KEY_NAME = "KEY_NAME"
    const val KEY_WEIGHT = "KEY_WIGHT"

}