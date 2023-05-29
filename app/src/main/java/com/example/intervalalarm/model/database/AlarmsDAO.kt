package com.example.intervalalarm.model.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmsDAO {

    /** UTILITIES */
    @Query("SELECT * FROM alarms_table ORDER BY alarmCount ASC")
    fun getAlarmsCounted(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms_table WHERE isActive = :status")
    fun getEnabledAlarms(status: Boolean = true): Flow<List<AlarmEntity>>

    /** HOME SCREEN */
    @Query("UPDATE alarms_table SET isActive=:status WHERE id = :id")
    suspend fun triggerStatus(id: String, status: Boolean)

    @Query("UPDATE alarms_table SET isActive=:status WHERE alarmCount = :count")
    suspend fun triggerStatusByCount(count: Int, status: Boolean)

    @Query("DELETE FROM alarms_table")
    suspend fun deleteAll()

    /** ADD NEW SCREEN */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAlarm(alarm: AlarmEntity)

    /** DETAILS SCREEN */
    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Query("UPDATE alarms_table SET title =:title WHERE id = :id")
    suspend fun updateTitle(id: String, title: String)

    @Query("UPDATE alarms_table SET description =:description WHERE id = :id")
    suspend fun updateDescription(id: String, description: String)

    @Query("UPDATE alarms_table SET schedule =:schedule WHERE id = :id")
    suspend fun updateSchedule(id: String, schedule: String)
//    @Query("UPDATE alarms_table SET isActive =:isActive WHERE id = :id")
//    suspend fun changeStatus(id: String, isActive: Boolean)

    @Query("UPDATE alarms_table SET schedule = :blankSchedule WHERE id = :id")
    suspend fun clearSchedule(id: String, blankSchedule: String = "")

    @Query("UPDATE alarms_table SET hours = :newHour WHERE id = :id ")
    suspend fun saveHour(id: String, newHour: Int)

    @Query("UPDATE alarms_table SET minutes = :newMinute WHERE id = :id ")
    suspend fun saveMinute(id: String, newMinute: Int)

    @Query("UPDATE alarms_table SET seconds = :newSecond WHERE id = :id ")
    suspend fun saveSecond(id: String, newSecond: Int)

    @Query("UPDATE alarms_table SET isActive = :status WHERE isActive != :status")
    suspend fun disableAll(status: Boolean = false)

    @Query("SELECT * FROM alarms_table ORDER BY alarmCount ASC")
    fun getGetGet(): Flow<List<AlarmEntity>>
}