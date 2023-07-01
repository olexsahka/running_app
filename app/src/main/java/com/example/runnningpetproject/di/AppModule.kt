package com.example.runnningpetproject.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runnningpetproject.db.RunningDatabase
import com.example.runnningpetproject.utlis.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runnningpetproject.utlis.Constants.KEY_NAME
import com.example.runnningpetproject.utlis.Constants.KEY_WEIGHT
import com.example.runnningpetproject.utlis.Constants.RunningDatabaseName
import com.example.runnningpetproject.utlis.Constants.SHARED_PREF_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RunningDatabaseName
    ).build()


    @Singleton
    @Provides
    fun provideRunDao(db:RunningDatabase) = db.getRunDao()


    @Singleton
    @Provides
    fun providesSharedPreferences(@ApplicationContext app:Context) =
        app.getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(KEY_NAME,"") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(KEY_WEIGHT,80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE,true)

}