package com.studiocar.studio.data.dao

import androidx.room.*
import com.studiocar.studio.data.models.EditedCar
import com.studiocar.studio.data.models.FavoritePreset
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

/**
 * Resultado de contagem por vendedor.
 */
data class VendorCount(
    val vendorName: String,
    val count: Int
)

@Dao
interface FavoritePresetDao {
    @Query("SELECT * FROM favorite_presets ORDER BY timestamp DESC")
    fun getAllPresets(): Flow<List<FavoritePreset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: FavoritePreset)

    @Delete
    suspend fun deletePreset(preset: FavoritePreset)

    @Query("UPDATE favorite_presets SET isDefault = 0")
    suspend fun clearDefaults()

    @Query("UPDATE favorite_presets SET isDefault = 1 WHERE id = :presetId")
    suspend fun setDefault(presetId: Int)
}
