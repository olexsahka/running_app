package com.example.runnningpetproject.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runnningpetproject.R
import com.example.runnningpetproject.databinding.FragmentSetupBinding
import com.example.runnningpetproject.utlis.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {
    private var _binding: FragmentSetupBinding? = null
    private val binding: FragmentSetupBinding get() = _binding!!


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var isFirstOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstOpen){
            val navOpt = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment,savedInstanceState,navOpt)
        }

        binding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success)
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            else
                Snackbar.make(requireView(),"Please enter all the fields", Snackbar.LENGTH_LONG).show()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupBinding.inflate(inflater,container,false)
        return binding.root
    }


    private fun writePersonalDataToSharedPref():Boolean{
        val name = binding.inputNameIET.text.toString()
        val weight = binding.inputWeightIET.text.toString()
        if (name.isEmpty() || weight.isEmpty() )
            return false
        sharedPreferences.edit()
            .putString(Constants.KEY_NAME,name)
            .putFloat(Constants.KEY_WEIGHT,weight.toFloat())
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE,false)
            .apply()
        return true

    }
}