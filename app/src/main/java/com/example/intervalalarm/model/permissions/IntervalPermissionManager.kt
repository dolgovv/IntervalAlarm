package com.example.intervalalarm.model.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.intervalalarm.MainActivity

class IntervalPermissionManager {

    companion object {
        const val NOTIFICATIONS_PERMISSION_CODE = 101
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return when (permission) {

            android.Manifest.permission.POST_NOTIFICATIONS ->

                if (Build.VERSION.SDK_INT >= 33) {
                    context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }

            //ADD OTHER PERMISSION SCENARIOS

            else -> {
                false
            }
        }
    }
}