package com.example.intervalalarm.model.data

import android.content.Context
import androidx.room.Room
import com.example.intervalalarm.model.data.database.AlarmsDAO
import com.example.intervalalarm.model.data.database.AlarmsDatabase
import com.example.intervalalarm.model.data.repository.AlarmsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AlarmsDatabase =
        Room.databaseBuilder(
            context,
            AlarmsDatabase::class.java,
            "alarms_database"
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideDAO(db: AlarmsDatabase): AlarmsDAO {
        return db.alarmsDao()
    }

    @Provides
    @Singleton
    fun provideRepository(dao: AlarmsDAO): AlarmsRepository {
        return AlarmsRepository(dao = dao)
    }
}