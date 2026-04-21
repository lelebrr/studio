package com.studiocar.studio.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.studiocar.studio.data.dao.CarDao
import com.studiocar.studio.data.dao.FavoritePresetDao
import com.studiocar.studio.data.models.EditedCar
import com.studiocar.studio.data.models.FavoritePreset

@Database(
    entities = [EditedCar::class, FavoritePreset::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao
    abstract fun favoritePresetDao(): FavoritePresetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studiocar_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
