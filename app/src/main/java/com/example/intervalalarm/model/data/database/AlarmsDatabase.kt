package com.example.intervalalarm.model.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AlarmEntity::class], version = 6,
    exportSchema = true
)
abstract class AlarmsDatabase: RoomDatabase() {
    abstract fun alarmsDao(): AlarmsDAO
}