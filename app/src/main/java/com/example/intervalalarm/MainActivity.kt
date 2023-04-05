package com.example.intervalalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import com.example.intervalalarm.model.module.notifications.AlarmNotificationService
import com.example.intervalalarm.model.module.timer.CurrentAlarmTimer
import com.example.intervalalarm.ui.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.screens.navigation.NavScreens
import com.example.intervalalarm.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = applicationContext

        val alarmChannel = NotificationChannel(
            AlarmNotificationService.CHANNEL_RINGS_ID,
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val att = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        alarmChannel.description = "Ringing Notifications"
        alarmChannel.setSound(
            Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarm_ringtone),
            att
        )
        AlarmNotificationService(context).deleteChannel(alarmChannel)
        AlarmNotificationService(context).createChannel(alarmChannel)
        Log.d("notify management", "${alarmChannel.sound.path} and created")

        setContent {
            val navController: NavHostController = rememberNavController()
            val viewModel: MainViewModel = hiltViewModel()

//            val callback = this.onBackPressedDispatcher.addCallback(this) {
//                viewModel.backPressedShowDialog()
//                Log.d("onBackPressed ebat", "cho ahuett")
//            }

            Log.d("notify management", "extra is: ${intent.extras?.getInt("from_notification")}")

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
                            intent.extras?.getInt("from_notification")
                        )
                    }
                }
            }
        }
    }
}