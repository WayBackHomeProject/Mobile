package com.ssafy.waybackhome.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionChecker(private val activityOrFragment : Any?) {
    private lateinit var context: Context

    private var grantListener : OnGrantedListener? = null
    private var rejectionListener : OnRejectedListener? = null

    fun setOnGrantedListener(listener: OnGrantedListener){
        grantListener = listener
    }
    fun setOnRejectedListener(listener: OnRejectedListener){
        rejectionListener = listener
    }
    fun checkPermission(context: Context, permissions: Array<String>, launchForPermission : Boolean = true): Boolean {
        this.context = context
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission( context, permission ) != PackageManager.PERMISSION_GRANTED ) {
                if(launchForPermission) requestPermissionLauncher.launch(permissions)
                return false
            }
        }
        return true
    }

    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> = when (activityOrFragment) {
        is AppCompatActivity -> {
            activityOrFragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()){
                checkPermissionRequest(it)
            }
        }

        is Fragment -> {
            activityOrFragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()){
                checkPermissionRequest(it)
            }
        }

        else -> {
            throw RuntimeException("Requires Activity or Fragment.")
        }
    }

    private val defaultRejection = OnRejectedListener{
        Toast.makeText(context, "Permission rejected.", Toast.LENGTH_SHORT).show()
        moveToSettings()
    }

    private val defaultGrant = OnGrantedListener{
        Toast.makeText(context, "All permissions are granted.", Toast.LENGTH_SHORT).show()
    }

    private fun checkPermissionRequest(result: Map<String, Boolean>){
        val onRejected = rejectionListener ?: defaultRejection
        val onGranted = grantListener ?: defaultGrant
        if(result.values.contains(false)){
            onRejected.onRejected(result)
        }else{
            onGranted.onGranted(result)
        }
    }
    fun moveToSettings() {
        val alertDialog = AlertDialog.Builder( context )
        alertDialog.setTitle("Permission required.")
        alertDialog.setMessage("Grant permissions in settings?")
        alertDialog.setPositiveButton("OK") { dialogInterface, _ ->
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + context.packageName))
            context.startActivity(intent)
            dialogInterface.cancel()
        }
        alertDialog.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.cancel() }
        alertDialog.show()
    }
}