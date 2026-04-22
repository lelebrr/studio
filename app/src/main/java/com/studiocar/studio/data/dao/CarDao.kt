package com.studiocar.studio.data.dao

import androidx.room.*
import com.studiocar.studio.data.models.EditedCar
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {
    @Query("SELECT * FROM edited_cars ORDER BY timestamp DESC")
    fun getAllCars(): Flow<List<EditedCar>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: EditedCar)

    @Delete
    suspend fun deleteCar(car: EditedCar)
}
