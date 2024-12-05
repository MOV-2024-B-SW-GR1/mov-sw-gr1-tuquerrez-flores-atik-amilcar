package org.example.Models

import java.time.LocalDate

data class CitaMedica(
    val id: Int,
    var fecha: LocalDate,
    var motivo: String,
    var costo: Double,
    var medico: String
) {
    override fun toString(): String {
        return "$id,$fecha,$motivo,$costo,$medico"
    }

    companion object {
        fun fromString(data: String): CitaMedica {
            val partes = data.split(",")
            return CitaMedica(
                partes[0].toInt(),
                LocalDate.parse(partes[1]),
                partes[2],
                partes[3].toDouble(),
                partes[4]
            )
        }
    }
}
