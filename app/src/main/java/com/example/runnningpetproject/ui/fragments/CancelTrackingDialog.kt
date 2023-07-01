package com.example.runnningpetproject.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.R
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelTrackingDialog : DialogFragment() {

    private var yesListener: (() ->  Unit)? = null

    fun setYesListener(listener: () -> Unit){
        yesListener = yesListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return  MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog_AppCompat)
            .setTitle("Cancel this Run?")
            .setMessage("Are you sure?")
            .setIcon(com.example.runnningpetproject.R.drawable.ic_baseline_delete_24)
            .setPositiveButton("Yes"){_,_ ->
                yesListener?.let { yes ->
                    yes()


                }
            }
            .setNegativeButton("No"){dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()

    }
}