package com.example.intervalalarm

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.intervalalarm.model.alarm_functionality.notifications.AlarmNotificationService
import com.example.intervalalarm.model.alarm_functionality.notifications.NotificationChannelType
import com.example.intervalalarm.model.permissions.IntervalPermissionManager
import com.example.intervalalarm.view.theme.IntervalAlarmTheme
import com.example.intervalalarm.model.navigation.NavScreens
import com.example.intervalalarm.model.navigation.Screens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val NOTIFICATIONS_PERMISSION_CODE =
            IntervalPermissionManager.NOTIFICATIONS_PERMISSION_CODE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = applicationContext
        AlarmNotificationService(context).createChannel(NotificationChannelType.InfoChannel)
        AlarmNotificationService(context).createChannel(NotificationChannelType.AlarmChannel)

        if (Build.VERSION.SDK_INT >= 33) {
            if (!shouldShowRequestPermissionRationale(IntervalPermissionManager.NOTIFICATIONS_PERMISSION_CODE.toString())) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    requestPermissions(
                        listOf(android.Manifest.permission.POST_NOTIFICATIONS).toTypedArray(),
                        NOTIFICATIONS_PERMISSION_CODE
                    )
                }
            }
        }

        setContent {

            val navController: NavHostController = rememberNavController()
            navController.clearBackStack(Screens.DetailsScreen.route)

            IntervalAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Top
                    ) {

                        NavScreens(
                            navController = navController,
                            autoNavigateTo = intent.extras?.getInt("from_notification"),
                        )
                    }
                }
            }
        }
    }
}