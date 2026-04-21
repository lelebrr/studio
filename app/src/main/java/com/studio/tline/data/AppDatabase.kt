package com.studio.tline.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.studio.tline.data.dao.CarDao
import com.studio.tline.data.models.EditedCar

@Database(entities = [EditedCar::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tline_studio_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
