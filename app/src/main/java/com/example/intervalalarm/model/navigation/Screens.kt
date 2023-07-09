package com.example.intervalalarm.model.navigation

sealed class Screens(val route: String) {
    object HomeScreen : Screens(route = "home_screen")
    object DetailsScreen : Screens(route = "details_screen")
    object NewAlarmScreen : Screens(route = "new_alarm_screen")

    fun withArgs(alarmCount: Int): String {
        return buildString {
            append(route)
            append("/$alarmCount")
        }
    }
}