package org.example.Models

data class Paciente(
    val nombre: String,
    val id: Int,
    var edad: Int,
    var genero: String,
    var tieneSeguro: Boolean,
    val citas: MutableList<CitaMedica> = mutableListOf()
){
    override fun toString(): String {
        val citasString = citas.joinToString("#") { it.toString() }
        return "$nombre,$id,$edad,$genero,$tieneSeguro|$citasString"
    }


    companion object {
        fun fromString(data: String): Paciente {
            val partes = data.split("|")
            val info = partes[0].split(",") // Información básica del paciente.

            val citas = if (partes.size > 1 && partes[1].isNotBlank()) {
                partes[1].split("#").map { CitaMedica.fromString(it) }.toMutableList()
            } else {
                mutableListOf()
            }

            return Paciente(
                nombre = info[0],
                id = info[1].toInt(),
                edad = info[2].toInt(),
                genero = info[3],
                tieneSeguro = info[4].toBoolean(),
                citas = citas
            )
        }
    }

}
