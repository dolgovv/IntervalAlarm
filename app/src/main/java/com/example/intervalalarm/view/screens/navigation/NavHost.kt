package com.example.intervalalarm.view.screens.navigation

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.intervalalarm.model.module.timer.CurrentAlarmTimer
import com.example.intervalalarm.view.screens.details.DetailsScreen
import com.example.intervalalarm.view.screens.home.HomeScreen
import com.example.intervalalarm.view.screens.new_alarm.NewAlarmScreen
import com.example.intervalalarm.viewmodel.MainViewModel


@Composable
fun NavScreens(
    navController: NavHostController,
    vm: MainViewModel,
    autoNavigateTo: Int?
) {

    val homeState by vm.homeScreenUiState.collectAsState()
    val detailsState by vm.detailsScreenUiState.collectAsState()
    val addNewState by vm.addNewAlarmUiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route
    ) {

        /** HOME SCREEN */
        composable(route = Screens.HomeScreen.route) {

            HomeScreen(
                vm = vm,
                navController = navController,
                state = homeState
            )
        }

        /** ADD NEW SCREEN */
        composable(route = Screens.NewAlarmScreen.route) {
            NewAlarmScreen(
                vm = vm,
                navController = navController,
                onBackPressed = { vm.showBackPressedNewAlarmDialog() }
            )
        }

        /** DETAILS SCREEN */
        composable(route = Screens.DetailsScreen.route + "/{count}",
            arguments = listOf(
                navArgument("count") { type = NavType.IntType }
            )) { entry ->
            homeState.allAlarms.find { it.count == entry.arguments!!.getInt("count") }?.let {
                vm.updateDetailsScreen(it)
            }
            DetailsScreen(
                vm = vm,
                state = detailsState,
                navController = navController,
                currentAlarm = detailsState.chosenAlarm,
                onBackPressed = { vm.showBackPressedDetailsDialog() })
        }
    }

    autoNavigateTo?.let {
        navController.navigate(route = Screens.DetailsScreen.withArgs(it))
    }
}