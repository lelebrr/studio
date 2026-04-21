package com.studio.tline.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa um carro editado salvo no histórico.
 */
@Entity(tableName = "edited_cars")
data class EditedCar(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalPhotoPath: String,
    val resultPhotoPath: String,
    val backgroundName: String,
    val floorName: String,
    val timestamp: Long = System.currentTimeMillis()
)
