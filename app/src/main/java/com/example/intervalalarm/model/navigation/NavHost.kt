package com.example.intervalalarm.model.navigation

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.intervalalarm.view.screens.details.DetailsScreen
import com.example.intervalalarm.view.screens.home.HomeScreen
import com.example.intervalalarm.view.screens.new_alarm.NewAlarmScreen
import com.example.intervalalarm.viewmodel.MainViewModel

@Composable
fun NavScreens(
    navController: NavHostController,
    autoNavigateTo: Int?
) {

    val vm: MainViewModel = hiltViewModel()

    val context = LocalContext.current

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
                openDetails = { navController.navigate(Screens.DetailsScreen.withArgs(it)) },
                openAddNew = { navController.navigate(Screens.NewAlarmScreen.route) },
                state = homeState,
                triggerAlarm = {
                    vm.triggerAlarm(context, it, infoToast = {
                        Toast.makeText(
                            context,
                            "Schedule cleared!",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                },
                deleteAllAlarms = { vm.deleteAllAlarms(it) }
            )
        }

        /** ADD NEW SCREEN */
        composable(route = Screens.NewAlarmScreen.route) {

            NewAlarmScreen(
                state = addNewState,
                alarmsLastIndex = if (homeState.allAlarms.isNotEmpty()) homeState.allAlarms.last().count + 1 else 1,
                popBackStack = { navController.popBackStack() },
                updateDescription = { vm.updateDescription(it) },
                updateSchedule = { vm.updateSchedule(it) },

                updateNewHour = { vm.updateNewHour(it) },
                updateNewMinute = { vm.updateNewMinute(it) },
                updateNewSecond = { vm.updateNewSecond(it) },

                addNewAlarm = { vm.addNewAlarm(context, it) },
                hideBackPressedNewAlarmDialog = { vm.hideBackPressedNewAlarmDialog() },
                clearNewAlarm = { vm.clearNewAlarm() },
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
                state = detailsState,
                popBackStack = { navController.popBackStack() },
                updateEditedTitle = { vm.updateEditedTitle(it) },
                updateEditedDescription = { vm.updateEditedDescription(it) },
                updateEditedSchedule = { vm.updateEditedSchedule(it) },

                updateDetailsWheelStateHour = { vm.updateDetailsWheelStateHour(it) },
                updateDetailsWheelStateMinute = { vm.updateDetailsWheelStateMinute(it) },
                updateDetailsWheelStateSecond = { vm.updateDetailsWheelStateSecond(it) },

                deleteAlarm = { vm.deleteAlarm(context, it) },
                triggerEditableDetails = { vm.triggerEditableDetails() },
                saveEditedAlarm = { vm.saveEditedAlarm(context) },
                clearDetailsScreen = { vm.clearDetailsScreen() },
                hideBackPressedDetailsDialog = { vm.hideBackPressedDetailsDialog() },
                onBackPressed = { vm.showBackPressedDetailsDialog() })
        }
    }
    autoNavigateTo?.let { navController.navigate(route = Screens.DetailsScreen.withArgs(it)) }
}