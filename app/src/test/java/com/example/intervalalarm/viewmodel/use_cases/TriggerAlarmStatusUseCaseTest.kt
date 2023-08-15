package com.example.intervalalarm.viewmodel.use_cases

import android.content.Context
import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.test.model.FakeAlarmsRepository
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class TriggerAlarmStatusUseCaseTest {
    private var repository: FakeAlarmsRepository = FakeAlarmsRepository()
    private val SUT = TriggerAlarmStatusUseCase(repository)
    private val context: Context = mockk()
    val addNewAlarmUseCase = AddNewAlarmUseCase(repository)
    private lateinit var testAlarm: AlarmEntity

    @Test
    fun triggerAllAlarms() {

        var random: Boolean

        for (i in 1..200) {
            random = Random.nextBoolean()
            testAlarm = AlarmUiState(
                id = i.toString(),
                count = i,
                status = if (random) AlarmStatus.Enabled else AlarmStatus.Disabled
            ).toEntity()

            addNewAlarmUseCase(testAlarm)
            println(testAlarm.id + " added" + if (testAlarm.isActive) " active" else " not active")
            assertTrue(repository.fakeDb.contains(testAlarm))
            assertEquals(random, testAlarm.isActive)

            SUT.invoke(context, testAlarm.toUiState())
            println(testAlarm.id + if (random == !repository.fakeDb[testAlarm.alarmCount - 1].isActive) " triggered" else " not triggered")
            assertEquals(random, !repository.fakeDb[testAlarm.alarmCount - 1].isActive)

        }
    }
}