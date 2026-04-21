package com.studiocar.studio.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa um carro editado salvo no histórico.
 * V2.0 — Expandido com metadados de veículo, vendedor, lote e legenda.
 */
@Entity(tableName = "edited_cars")
data class EditedCar(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalPhotoPath: String = "",
    val resultPhotoPath: String = "",
    val backgroundName: String = "",
    val floorName: String = "",
    val timestamp: Long = System.currentTimeMillis(),

    // Metadados do Veículo (VIN Scanner #2)
    val vinCode: String? = null,
    val carBrand: String? = null,
    val carModel: String? = null,
    val carYear: String? = null,
    val carColor: String? = null,

    // Vendedor (#15)
    val vendorId: String? = null,
    val vendorName: String? = null,

    // Tipo de foto (#12)
    val photoType: String = "EXTERIOR",

    // Batch (#3)
    val batchId: String? = null,

    // Legenda (#4)
    val caption: String? = null,

    // Export (#16)
    val exportedSizes: String? = null
)
