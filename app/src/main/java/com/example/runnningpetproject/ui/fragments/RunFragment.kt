package com.example.runnningpetproject.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runnningpetproject.R
import com.example.runnningpetproject.adapters.RunAdapter
import com.example.runnningpetproject.databinding.FragmentRunBinding
import com.example.runnningpetproject.databinding.FragmentSetupBinding
import com.example.runnningpetproject.ui.viewModels.MainViewModel
import com.example.runnningpetproject.utlis.Constants.REQUEST_LOCATION_PERMISSIONS
import com.example.runnningpetproject.utlis.Sorted
import com.example.runnningpetproject.utlis.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run),EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentRunBinding? = null
    private val binding: FragmentRunBinding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var  runAdapter: RunAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
        initRecycleView()

        when(viewModel.sortType){
            Sorted.DATE -> binding.spFilter.setSelection(0)
            Sorted.RUNNING_TIME -> binding.spFilter.setSelection(1)
            Sorted.DISTANCE -> binding.spFilter.setSelection(2)
            Sorted.AVG_SPEED -> binding.spFilter.setSelection(3)
            Sorted.CALORIES -> binding.spFilter.setSelection(4)

        }
        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> viewModel.sortRuns(Sorted.DATE)
                    1 -> viewModel.sortRuns(Sorted.RUNNING_TIME)
                    2 -> viewModel.sortRuns(Sorted.DISTANCE)
                    3 -> viewModel.sortRuns(Sorted.AVG_SPEED)
                    4 -> viewModel.sortRuns(Sorted.CALORIES)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        viewModel.runs.observe(viewLifecycleOwner) {
            runAdapter.submitList(it)
        }

        binding.fab.setOnClickListener{
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

    }

    private fun initRecycleView() = binding.rvRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunBinding.inflate(inflater,container,false)
        return binding.root
    }
    private fun requestPermission(){
        if(TrackingUtility.hasLocationPermission(requireContext())){
            return
        }
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "You need accept location permissions",
                REQUEST_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        else{
            EasyPermissions.requestPermissions(
                this,
                "You need accept location permissions",
                REQUEST_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
        else{
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
}