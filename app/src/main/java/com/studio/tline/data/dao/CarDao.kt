package com.studio.tline.data.dao

import androidx.room.*
import com.studio.tline.data.models.EditedCar
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {
    @Query("SELECT * FROM edited_cars ORDER BY timestamp DESC")
    fun getAllCars(): Flow<List<EditedCar>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: EditedCar)

    @Delete
    suspend fun deleteCar(car: EditedCar)

    @Query("SELECT * FROM edited_cars ORDER BY timestamp DESC LIMIT 10")
    fun getRecentHistory(): Flow<List<EditedCar>>
}
