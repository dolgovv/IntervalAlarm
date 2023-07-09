package com.example.intervalalarm.model.navigation

import android.os.Build
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.intervalalarm.MainActivity
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
                state = addNewState,
                list = homeState.allAlarms,
                vm = vm,
                navController = navController,
                onBackPressed = {
                    if (
                        addNewState.title.isNotEmpty()
                        || addNewState.description.isNotEmpty()
                        || addNewState.wheelPickerState.currentHour != 0
                        || addNewState.wheelPickerState.currentMinute != 0
                        || addNewState.wheelPickerState.currentSecond != 0
                        || addNewState.schedule.isNotEmpty()
                    ) {
                        vm.showBackPressedNewAlarmDialog()
                    } else {
                        navController.popBackStack()
                    }
                }
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
    autoNavigateTo?.let { navController.navigate(route = Screens.DetailsScreen.withArgs(it)) }
}