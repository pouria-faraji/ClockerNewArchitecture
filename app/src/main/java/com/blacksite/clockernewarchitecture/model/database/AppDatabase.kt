package com.blacksite.clockernewarchitecture.model.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.content.Context
import com.blacksite.clockernewarchitecture.application.Settings
import com.blacksite.clockernewarchitecture.model.interfaces.ClockDao


@Database(entities = arrayOf(Clock::class), version = Settings.DATABASE_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clockDao(): ClockDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java!!, Settings.DATABASE_NAME)
                                // Wipes and rebuilds instead of migrating
                                // if no Migration object.
                                // Migration is not part of this practical.
                                .allowMainThreadQueries()
                                .fallbackToDestructiveMigration()
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }


}