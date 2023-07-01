package com.example.runnningpetproject.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.runnningpetproject.R
import com.example.runnningpetproject.databinding.FragmentTrackingBinding
import com.example.runnningpetproject.db.Run
import com.example.runnningpetproject.service.Polyline
import com.example.runnningpetproject.service.TrackingService
import com.example.runnningpetproject.ui.viewModels.MainViewModel
import com.example.runnningpetproject.utlis.Constants
import com.example.runnningpetproject.utlis.Constants.ACTION_PAUSE
import com.example.runnningpetproject.utlis.Constants.ACTION_START_RESUME
import com.example.runnningpetproject.utlis.Constants.ACTION_STOP
import com.example.runnningpetproject.utlis.Constants.MAP_ZOOM
import com.example.runnningpetproject.utlis.Constants.POLYLINE_COLOR
import com.example.runnningpetproject.utlis.Constants.POLYLINE_WIDTH
import com.example.runnningpetproject.utlis.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var _binding: FragmentTrackingBinding? = null
    private val binding: FragmentTrackingBinding get() = _binding!!

    private var map : GoogleMap? = null
    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var currentTimeMillies = 0L
    private var menu: Menu? = null

    @set:Inject
    var weight = 80f


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        if (savedInstanceState != null){
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG) as CancelTrackingDialog
            cancelTrackingDialog?.setYesListener {
                stopRun()
            }
        }
        binding.btnToggleRun.setOnClickListener{
            toogleRun()
        }
        binding.btnFinishRun.setOnClickListener{
            zoomToSeeWholeTrack()
            endToRunServiceToDb()
        }

        subscribeToObservers()
    }

    private fun addAllPolylines(){
        for (polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun moveCameraToUser(){
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),MAP_ZOOM
                )
            )
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun toogleRun(){
        if(isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE)
        }
        else sendCommandToService(ACTION_START_RESUME)
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoint.observe(viewLifecycleOwner,Observer{
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeMillies = it
            val formatTime = TrackingUtility.getFormattedStopWatchTime(currentTimeMillies,true)
            binding.tvTimer.text = formatTime
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if(!isTracking && currentTimeMillies > 0L){
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility =View.VISIBLE
        }else if(isTracking){
            binding.btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible = true
            binding.btnFinishRun.visibility =View.GONE
        }
    }

    private fun addLatestPolyline(){
        if (pathPoints.isNotEmpty() && pathPoints.last().size >1 ){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun zoomToSeeWholeTrack(){
        val bound = LatLngBounds.builder()
        for (polyline in pathPoints){
            for (pos in polyline){
                bound.include(pos)
            }
        }
        val minMetric = Math.min(150,Math.min(binding.mapView.width, binding.mapView.height))
        val padding =  minMetric * 0.5
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bound.build(),
                binding.mapView.width,
                binding.mapView.height,
                padding.toInt()            )
        )
    }
    private fun endToRunServiceToDb(){
        map?.snapshot { bmp ->
            var distanceInMetres =0
            for (polyline in pathPoints){
                distanceInMetres += TrackingUtility.calculateDistance(polyline).toInt()
            }
            val avgSpeed = (distanceInMetres.toFloat()/1000f) / (currentTimeMillies/ 1000f / 60f / 60f)
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMetres/1000f)*weight).toInt()
            val run =  Run(0,bmp,dateTimeStamp,avgSpeed,
                distanceInMetres.toFloat(),currentTimeMillies,caloriesBurned)

            viewModel.insertRun(run)
            Snackbar.make(binding.root, "Run saved successful", Snackbar.LENGTH_LONG).show()
            stopRun()

        }
    }





    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendCommandToService(action: String)=
        Intent(requireContext(),TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(currentTimeMillies>0L){
            this.menu?.getItem(0)?.isVisible =true
        }
    }
    private fun stopRun(){
        binding.tvTimer.text = "00:00:00:00"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendCommandToService(ACTION_STOP)
        }
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }



    private fun showCancelDialog(){
        CancelTrackingDialog().apply { 
            setYesListener { 
                stopRun()
            }
        }.show(parentFragmentManager,CANCEL_TRACKING_DIALOG)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.cancel_Tracking  ->{
                 showCancelDialog()
            }
        }
        return super.onOptionsItemSelected(item)

    }
companion object
{
    const val CANCEL_TRACKING_DIALOG = "cancelDialog"
}
}