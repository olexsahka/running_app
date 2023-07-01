package com.example.runnningpetproject.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runnningpetproject.R
import com.example.runnningpetproject.ui.MainActivity
import com.example.runnningpetproject.utlis.Constants
import com.example.runnningpetproject.utlis.Constants.ACTION_PAUSE
import com.example.runnningpetproject.utlis.Constants.ACTION_START_RESUME
import com.example.runnningpetproject.utlis.Constants.ACTION_STOP
import com.example.runnningpetproject.utlis.Constants.FASTEST_INTERVAL_LOCATION
import com.example.runnningpetproject.utlis.Constants.LOCATION_INTERVAL
import com.example.runnningpetproject.utlis.Constants.NOTIFICATION_ID
import com.example.runnningpetproject.utlis.Constants.TIMER_UPDATE
import com.example.runnningpetproject.utlis.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {
    var isFirstRun = true
    var isServiceKilled = false

    @Inject
    lateinit var  fusedLocationProviderClient : FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder


    lateinit var currNotificationBuilder: NotificationCompat.Builder



    private val timeRunInSeconds = MutableLiveData<Long>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_RESUME->{
                    if (isFirstRun){
                        startNewForeGroundService()
                        isFirstRun = false
                    }
                    else{
                        startTimer()

                    }
                }
                ACTION_PAUSE->{
                    Timber.d("Pause Service")
                    pauseService()
                }
                ACTION_STOP->{
                    killService()
                    Timber.d("Stop Service")
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onCreate() {
        super.onCreate()
        currNotificationBuilder = baseNotificationBuilder
        postInitialVals()
        isTracking.observe(this, Observer {
            updateLocation(it)
            updateNotificationTrackingState(it)
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startNewForeGroundService(){
        startTimer()
        isTracking.postValue(true)
        Timber.d("startForeground Service")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())
        }

        timeRunInSeconds.observe(this, Observer {
            if (!isServiceKilled){
                val notification = currNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(it*1000L))
                notificationManager.notify(NOTIFICATION_ID,notification.build())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    private fun postInitialVals(){
        isTracking.postValue(false)
        pathPoint.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)

    }
    private fun addEmptyPolyline() = pathPoint.value?.apply {
        add(mutableListOf())
        pathPoint.postValue(this)
    } ?: pathPoint.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoint(location : Location?){
        location?.let {
            val pos =LatLng(it.latitude,it.longitude)
            pathPoint.value?.apply {
                last().add(pos)
                pathPoint.postValue(this)
            }
        }
    }

    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!){
                result.locations.let { locations ->
                    for (location in locations){
                        addPathPoint(location)
                        Timber.d("New Location ${location.latitude}, ${location.longitude}")

                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun  updateLocation(isTracking : Boolean){
        if (isTracking){
            if (TrackingUtility.hasLocationPermission(this)){
                val request = com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,
                    LOCATION_INTERVAL).apply {
                    setMinUpdateIntervalMillis(FASTEST_INTERVAL_LOCATION)
                    setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                    setWaitForAccurateLocation(true)
                }.build()
                fusedLocationProviderClient.requestLocationUpdates(request,locationCallback, Looper.getMainLooper()
                )
            }
        }
        else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
    private fun killService(){
        isServiceKilled =true
        isFirstRun = true
        postInitialVals()
        stopForeground(true)
        stopSelf()
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled =false
    }

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled =true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                lapTimes = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(timeRun + lapTimes)
                if(timeRunInMillis.value!! >= lastSecondTimeStamp + 1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }
                delay(TIMER_UPDATE)
            }
            timeRun += lapTimes
        }
    }
    private fun updateNotificationTrackingState(isTracking: Boolean){
        val notificationText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE
            }
            PendingIntent.getService(this,1,pauseIntent,FLAG_IMMUTABLE)
        } else{
            val  resumeIntent =  Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_RESUME
            }
            PendingIntent.getService(this,2,resumeIntent,FLAG_IMMUTABLE)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        currNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currNotificationBuilder,ArrayList<NotificationCompat.Action>())
        }
        currNotificationBuilder = baseNotificationBuilder.addAction(R.drawable.ic_baseline_pause_24,notificationText,pendingIntent)
        notificationManager.notify(NOTIFICATION_ID,currNotificationBuilder.build())
    }

    private var isTimerEnabled = false
    private var lapTimes = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp =0L

    companion object{
        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoint =  MutableLiveData<Polylines>()

    }
}