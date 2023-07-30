package com.example.intervalalarm.test.ui_components.wheelpicker

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.navigation.testing.TestNavHostController
import com.example.intervalalarm.view.common_components.WheelIntervalPicker
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class WheelPickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun init() {

        composeTestRule.setContent {
//            navController = TestNavHostController(LocalContext.current)
//            navController.navigatorProvider.addNavigator(ComposeNavigator())
            WheelIntervalPicker(
                isEnabled = false,
                updateHour = {}, updateMinute = {}, updateSecond = {})

        }
    }

    @After
    fun closeTest() {
    }


    @Test
    fun wheelPickerHoursTouch() {
        var checkHour = 1

        while (checkHour < 24) {
            composeTestRule.onNodeWithContentDescription("Hour $checkHour").assertExists()
                .performClick()
            composeTestRule.onNodeWithContentDescription("Hour ${checkHour + 1}").assertExists()
            checkHour += 1
        }

        while (checkHour > 0) {
            composeTestRule.onNodeWithContentDescription("Hour $checkHour").assertExists()
                .performClick()
            composeTestRule.onNodeWithContentDescription("Hour ${checkHour - 1}").assertExists()
            checkHour -= 1
        }
    }

    @Test
    fun wheelPickerMinutesTouch() {
        var checkMinute = 1

        while (checkMinute < 60) {
            composeTestRule.onNodeWithContentDescription("Minute $checkMinute").assertExists()
                .performClick()
            composeTestRule.onNodeWithContentDescription("Minute ${checkMinute + 1}").assertExists()
            checkMinute += 1
        }

        while (checkMinute > 0) {
            composeTestRule.onNodeWithContentDescription("Minute $checkMinute").assertExists()
                .performClick()
            composeTestRule.onNodeWithContentDescription("Minute ${checkMinute - 1}").assertExists()
            checkMinute -= 1
        }
    }

    @Test
    fun wheelPickerSecondsTouch() {
        var checkSecond = 1

        while (checkSecond < 60) {
            composeTestRule.onNodeWithContentDescription("Second $checkSecond").assertExists()
                .performClick()
            composeTestRule.onNodeWithContentDescription("Second ${checkSecond + 1}").assertExists()
            checkSecond += 1
        }

        while (checkSecond > 0) {
            composeTestRule.onNodeWithContentDescription("Second $checkSecond").assertExists()
                .performClick()
            composeTestRule.onNodeWithContentDescription("Second ${checkSecond - 1}").assertExists()
            checkSecond -= 1
        }
    }

    @Test
    fun wheelPickerHoursSwipe() {
        var checkHour = Random.nextInt(0, 23)
        var i = 0

        while (i < 24) {
            composeTestRule.onNodeWithContentDescription("Wheel Picker: Hour Lazy Column")
                .performScrollToIndex(checkHour)
            composeTestRule.onNodeWithContentDescription("Hour ${checkHour - 1}").assertExists()
            composeTestRule.onNodeWithContentDescription("Hour $checkHour").assertExists()
            composeTestRule.onNodeWithContentDescription("Hour ${checkHour + 1}").assertExists()
            checkHour = Random.nextInt(0, 23)
            i += 1
        }
    }

    @Test
    fun wheelPickerMinutesSwipe() {
        var checkMinute = Random.nextInt(0, 59)
        var i = 0

        while (i < 30) {
            composeTestRule.onNodeWithContentDescription("Wheel Picker: Minute Lazy Column")
                .performScrollToIndex(checkMinute)
            composeTestRule.onNodeWithContentDescription("Minute ${checkMinute - 1}").assertExists()
            composeTestRule.onNodeWithContentDescription("Minute $checkMinute").assertExists()
            composeTestRule.onNodeWithContentDescription("Minute ${checkMinute + 1}").assertExists()
            checkMinute = Random.nextInt(0, 59)
            i += 1
        }
    }

    @Test
    fun wheelPickerSecondsSwipe() {
        var checkSecond = Random.nextInt(0, 59)
        var i = 0

        while (i < 30) {
            composeTestRule.onNodeWithContentDescription("Wheel Picker: Second Lazy Column")
                .performScrollToIndex(checkSecond)
            composeTestRule.onNodeWithContentDescription("Second ${checkSecond - 1}").assertExists()
            composeTestRule.onNodeWithContentDescription("Second $checkSecond").assertExists()
            composeTestRule.onNodeWithContentDescription("Second ${checkSecond + 1}").assertExists()
            checkSecond = Random.nextInt(0, 59)
            i += 1
        }
    }
}