package com.example.runnningpetproject.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.runnningpetproject.R
import com.example.runnningpetproject.databinding.FragmentSettingsBinding
import com.example.runnningpetproject.utlis.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runnningpetproject.utlis.Constants.KEY_NAME
import com.example.runnningpetproject.utlis.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = _binding!!

    @Inject
    lateinit var sharedPref:SharedPreferences



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        loadDataFromSharedPrefs()
        binding.btnApplyChanges.setOnClickListener {
            val success = applyChangesSettings()
            if (success)
                Snackbar.make(requireView(),"Saved changes successfully",Snackbar.LENGTH_LONG).show()
            else
                Snackbar.make(requireView(),"Please enter all the fields",Snackbar.LENGTH_LONG).show()
        }
        return binding.root
    }

    private fun loadDataFromSharedPrefs()= binding.apply {
        etName.setText(sharedPref.getString(KEY_NAME,""))
        etWeight.setText(sharedPref.getFloat(KEY_WEIGHT,80f).toString())
    }

    private fun applyChangesSettings():Boolean{
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty() )
            return false
        sharedPref.edit()
            .putString(KEY_NAME,name)
            .putFloat(KEY_WEIGHT,weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE,false)
            .apply()

        return true

    }
}