package com.example.runnningpetproject.utlis

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.example.runnningpetproject.service.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtility {
    fun hasLocationPermission(context: Context) =
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        else
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

    fun getFormattedStopWatchTime (ms: Long, includeMillis : Boolean = false):String{
        var millieSecond = ms
        val hours = TimeUnit.MILLISECONDS.toHours(millieSecond)
        millieSecond -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millieSecond)
        millieSecond -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millieSecond)
        millieSecond -= TimeUnit.SECONDS.toMillis(seconds)
        millieSecond /= 10

        if (!includeMillis){
            return "${if (hours < 10) "0" else ""}$hours:"+
                    "${if (minutes < 10) "0" else ""}$minutes:"+
                    "${if (seconds < 10) "0" else ""}$seconds"
        }
        else
            return "${if (hours < 10) "0" else ""}$hours:"+
                    "${if (minutes < 10) "0" else ""}$minutes:"+
                    "${if (seconds < 10) "0" else ""}$seconds:" +
                    "${if (millieSecond < 10) "0" else ""}$millieSecond"

    }

    fun calculateDistance(polyline: Polyline): Float{
        var dist = 0f
        for (i in 0..polyline.size-2){
            val pos1 = polyline[i]
            val pos2 = polyline[i+1]
            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            dist += result[0]
        }
        return dist
    }


}