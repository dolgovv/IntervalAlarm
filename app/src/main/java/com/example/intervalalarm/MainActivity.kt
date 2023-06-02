package com.example.intervalalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.intervalalarm.model.module.notifications.AlarmNotificationService
import com.example.intervalalarm.model.module.notifications.NotificationType
import com.example.intervalalarm.model.permissions.IntervalPermissionManager
import com.example.intervalalarm.ui.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.screens.navigation.NavScreens
import com.example.intervalalarm.view.screens.navigation.Screens
import com.example.intervalalarm.viewmodel.MainViewModel
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

        if (Build.VERSION.SDK_INT >= 33) {
            if (!shouldShowRequestPermissionRationale(IntervalPermissionManager.NOTIFICATIONS_PERMISSION_CODE.toString())) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    requestPermissions(
                        listOf<String>(android.Manifest.permission.POST_NOTIFICATIONS).toTypedArray(),
                        NOTIFICATIONS_PERMISSION_CODE
                    )
                }
            }
        }

        val infoChannel = NotificationChannel(
            AlarmNotificationService.CHANNEL_INFO_ID,
            "Info notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        infoChannel.description = "Important information provided by the application"

        val alarmChannel = NotificationChannel(
            AlarmNotificationService.CHANNEL_RINGS_ID,
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val att = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        alarmChannel.description = "Notifies you when the alarm goes off"
        alarmChannel.setSound(
            Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarm_ringtone),
            att
        )

        AlarmNotificationService(context).createChannel(infoChannel)
        AlarmNotificationService(context).createChannel(alarmChannel)

        setContent {

            val navController: NavHostController = rememberNavController()
            navController.clearBackStack(Screens.DetailsScreen.route)

            val viewModel: MainViewModel = hiltViewModel()

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
                            vm = viewModel,
                            autoNavigateTo = intent.extras?.getInt("from_notification"),
                        )
                    }
                }
            }
        }
    }
}