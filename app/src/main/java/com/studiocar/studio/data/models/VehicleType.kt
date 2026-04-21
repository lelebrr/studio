package com.studiocar.studio.data.models

/**
 * Representa os tipos de veículos suportados pelo StudioCar para
 * as silhuetas de enquadramento inteligente.
 */
enum class VehicleType(val label: String) {
    HATCH("Hatch"),
    SEDAN("Sedan"),
    SUV("SUV"),
    CROSSOVER("Crossover"),
    MINIVAN("Minivan"),
    PICKUP("Picape"),
    STATION_WAGON("Station Wagon"),
    COUPE("Coupé")
}
