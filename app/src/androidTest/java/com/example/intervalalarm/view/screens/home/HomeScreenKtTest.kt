package com.example.intervalalarm.view.screens.home

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyPress
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.espresso.Espresso
import com.example.intervalalarm.MainActivity
import com.example.intervalalarm.model.data.DataModule
import com.example.intervalalarm.model.navigation.Screens
import com.example.intervalalarm.view.screens.details.DetailsScreen
import com.example.intervalalarm.view.screens.details.states.DetailsScreenUiState
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import com.example.intervalalarm.view.screens.home.states.HomeScreenUiState
import com.example.intervalalarm.view.screens.new_alarm.NewAlarmScreen
import com.example.intervalalarm.view.screens.new_alarm.states.AddNewScreenUiState
import com.example.intervalalarm.view.theme.IntervalAlarmTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(DataModule::class)
class HomeScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()
    private lateinit var navController: NavHostController

    private val alarmsList = mutableListOf(
        AlarmUiState(id = "1", count = 1, status = AlarmStatus.Disabled),
        AlarmUiState(id = "2", count = 2, status = AlarmStatus.Enabled)
    )

    @Before
    fun setUp() {

        hiltRule.inject()
        composeRule.setContent {
            navController = rememberNavController()
            IntervalAlarmTheme {
                NavHost(
                    navController = navController,
                    startDestination = Screens.HomeScreen.route
                ) {

                    composable(route = Screens.HomeScreen.route) {
                        HomeScreen(
                            state = HomeScreenUiState(allAlarms = alarmsList),
                            openDetails = {},
                            openAddNew = {
                                navController.navigate(Screens.NewAlarmScreen.route)
                            },
                            triggerAlarm = {},
                            deleteAllAlarms = {}
                        )
                    }

                    composable(route = Screens.NewAlarmScreen.route) {
                        NewAlarmScreen(
                            state = AddNewScreenUiState(),
                            alarmsLastIndex = if (alarmsList.isNotEmpty()) alarmsList.last().count + 1 else 1,
                            popBackStack = { navController.popBackStack() },
                            updateDescription = {},
                            updateSchedule = {},

                            updateNewHour = {},
                            updateNewMinute = {},
                            updateNewSecond = {},

                            addNewAlarm = {},
                            hideBackPressedNewAlarmDialog = {},
                            clearNewAlarm = {},
                            onBackPressed = {}
                        )
                    }

                    composable(route = Screens.DetailsScreen.route + "/{count}",
                        arguments = listOf(
                            navArgument("count") { type = NavType.IntType }
                        )) { entry ->
                        alarmsList.find { it.count == entry.arguments!!.getInt("count") }?.let {
//                            vm.updateDetailsScreen(it)
                        }
                        DetailsScreen(
                            state = DetailsScreenUiState(),
                            popBackStack = { navController.popBackStack() },
                            updateEditedTitle = { },
                            updateEditedDescription = { },
                            updateEditedSchedule = { },

                            updateDetailsWheelStateHour = { },
                            updateDetailsWheelStateMinute = { },
                            updateDetailsWheelStateSecond = { },

                            deleteAlarm = { },
                            triggerEditableDetails = { },
                            saveEditedAlarm = { },
                            clearDetailsScreen = { },
                            hideBackPressedDetailsDialog = { },
                            onBackPressed = { })
                    }
                }
            }
        }
    }

    @Test
    fun triggerAlarmTest() {
        alarmsList.add(
            alarmsList.lastIndex + 1,
            AlarmUiState(id = "3", count = 3, status = AlarmStatus.Disabled)
        )

        composeRule.onNodeWithContentDescription("Alarm Card no. 1").assertExists()
        composeRule.onNodeWithContentDescription("Alarm Card no. 1: Trigger button").assertExists()
            .performClick()
        composeRule.onNodeWithText("START NOW").assertExists().performClick()
        composeRule.onNodeWithText("STOP IT").assertExists()
    }

    @Test
    fun navigationTestHomeToAddAndBack() {
        composeRule.onNodeWithContentDescription("Main Button").assertExists()
            .performClick()
        println(navController.currentDestination?.route.toString())
        Assert.assertEquals(Screens.NewAlarmScreen.route, navController.currentDestination?.route)
        Espresso.pressBack()
        Assert.assertEquals(Screens.HomeScreen.route, navController.currentDestination?.route)
    }
}