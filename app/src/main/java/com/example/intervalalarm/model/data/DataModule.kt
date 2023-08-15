package com.example.intervalalarm.model.data

import android.content.Context
import androidx.room.Room
import com.example.intervalalarm.model.data.database.AlarmsDAO
import com.example.intervalalarm.model.data.database.AlarmsDatabase
import com.example.intervalalarm.model.data.repository.AlarmsRepository
import com.example.intervalalarm.model.data.repository.AlarmsRepositoryDefault
import com.example.intervalalarm.viewmodel.use_cases.AddNewAlarmUseCase
import com.example.intervalalarm.viewmodel.use_cases.AlarmsUseCases
import com.example.intervalalarm.viewmodel.use_cases.DeleteAlarmUseCase
import com.example.intervalalarm.viewmodel.use_cases.SaveDescriptionUseCase
import com.example.intervalalarm.viewmodel.use_cases.SaveScheduleUseCase
import com.example.intervalalarm.viewmodel.use_cases.SaveTitleUseCase
import com.example.intervalalarm.viewmodel.use_cases.TriggerAlarmStatusUseCase
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
    fun provideRepository(dao: AlarmsDAO): AlarmsRepositoryDefault {
        return AlarmsRepository(dao = dao)
    }

    @Provides
    @Singleton
    fun provideUseCases(repository: AlarmsRepositoryDefault): AlarmsUseCases {
        return AlarmsUseCases(
            addNewUseCase = AddNewAlarmUseCase(repository),
            deleteUseCase = DeleteAlarmUseCase(repository),
            saveDescriptionUseCase = SaveDescriptionUseCase(repository),
            saveScheduleUseCase = SaveScheduleUseCase(repository),
            saveTitleUseCase = SaveTitleUseCase(repository),
            triggerAlarmStatusUseCase = TriggerAlarmStatusUseCase(repository)
        )
    }
}