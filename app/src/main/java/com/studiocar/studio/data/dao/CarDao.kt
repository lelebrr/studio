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

    @Query("SELECT * FROM edited_cars ORDER BY timestamp DESC LIMIT 10")
    fun getRecentHistory(): Flow<List<EditedCar>>

    // Histórico por vendedor (#15)
    @Query("SELECT * FROM edited_cars WHERE vendorId = :vendorId ORDER BY timestamp DESC")
    fun getCarsByVendor(vendorId: String): Flow<List<EditedCar>>

    // Lote por batchId (#3)
    @Query("SELECT * FROM edited_cars WHERE batchId = :batchId ORDER BY timestamp DESC")
    fun getCarsByBatchId(batchId: String): Flow<List<EditedCar>>

    // Busca por VIN (#2)
    @Query("SELECT * FROM edited_cars WHERE vinCode = :vinCode ORDER BY timestamp DESC")
    fun getCarsByVin(vinCode: String): Flow<List<EditedCar>>

    // Relatório de uso (#17) — contagem hoje
    @Query("SELECT COUNT(*) FROM edited_cars WHERE timestamp >= :startOfDay")
    suspend fun getEditCountSince(startOfDay: Long): Int

    // Total de edições
    @Query("SELECT COUNT(*) FROM edited_cars")
    suspend fun getTotalEditCount(): Int

    // Contagem por vendedor
    @Query("SELECT vendorName, COUNT(*) as count FROM edited_cars WHERE vendorName IS NOT NULL GROUP BY vendorName ORDER BY count DESC")
    suspend fun getEditCountByVendor(): List<VendorCount>
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

    @Query("SELECT * FROM favorite_presets WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultPreset(): FavoritePreset?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: FavoritePreset)

    @Delete
    suspend fun deletePreset(preset: FavoritePreset)

    @Query("UPDATE favorite_presets SET isDefault = 0")
    suspend fun clearDefaults()

    @Query("UPDATE favorite_presets SET isDefault = 1 WHERE id = :presetId")
    suspend fun setDefault(presetId: Int)
}
