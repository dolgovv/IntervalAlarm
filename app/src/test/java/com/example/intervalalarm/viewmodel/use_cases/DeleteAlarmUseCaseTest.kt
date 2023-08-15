package com.example.intervalalarm.viewmodel.use_cases

import android.content.Context
import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.test.model.FakeAlarmsRepository
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteAlarmUseCaseTest {
    private var repository: FakeAlarmsRepository = FakeAlarmsRepository()
    private val SUT = DeleteAlarmUseCase(repository)
    private val context: Context = mockk()

    private lateinit var testAlarm: AlarmEntity

    @Before
    fun init() {
        val addNewAlarmUseCase = AddNewAlarmUseCase(repository)
        for (i in 1..200) {
            testAlarm = AlarmUiState(id = i.toString(), count = i).toEntity()
            addNewAlarmUseCase(testAlarm)
            println(testAlarm.id + " added " + repository.fakeDb.contains(testAlarm).toString())
            assertTrue(repository.fakeDb.contains(testAlarm))
        }
    }

    @Test
    fun deleteNewAlarmUseCaseTest() {

        while (repository.fakeDb.isNotEmpty()) {

            testAlarm = AlarmUiState(
                id = repository.fakeDb.size.toString(),
                count = repository.fakeDb.size
            ).toEntity()

            SUT.invoke(context, testAlarm)
            println(testAlarm.id + " deleted " + repository.fakeDb.contains(testAlarm).toString())
            assertFalse(repository.fakeDb.contains(testAlarm))
        }
    }
}