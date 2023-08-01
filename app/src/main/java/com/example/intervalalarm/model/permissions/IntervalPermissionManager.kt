package com.example.intervalalarm.model.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

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
                Log.d("permissions", "else in isPermissionGranted at IntervalPermissionManager")
                false
            }
        }
    }
}