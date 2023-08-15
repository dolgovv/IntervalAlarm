package com.example.intervalalarm.viewmodel.use_cases

import android.content.Context
import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.test.model.FakeAlarmsRepository
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AddNewAlarmUseCaseTest {
    private val repository: FakeAlarmsRepository = FakeAlarmsRepository()
    private lateinit var testAlarm: AlarmEntity

    @Test
    fun addNewAlarmUseCaseTest() {
        val SUT = AddNewAlarmUseCase(repository)
        for (i in 1..200) {
            testAlarm = AlarmUiState(id = i.toString(), count = i).toEntity()

            SUT.invoke(testAlarm)
            assertTrue(repository.fakeDb.contains(testAlarm))
            println(testAlarm.id + " added")
        }
    }
}