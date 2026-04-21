package com.studiocar.studio.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Preset favorito de fundo/piso salvo pelo usuário.
 */
@Entity(tableName = "favorite_presets")
data class FavoritePreset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val backgroundName: String,
    val floorName: String,
    val isDefault: Boolean = false,
    val userId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
