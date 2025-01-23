package com.example.consultorioaatf

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CitaMedica(
    val id: Int,
    var fecha: String,
    var motivo: String,
    var costo: Double,
    var medico: String,
    var pacienteId: Int
) : Parcelable {

    // Constructor que recibe un Parcel
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()?:"", // Convertir la fecha desde String
        parcel.readString() ?: "",  // Aseguramos que no sea null
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readInt()
    )

    // Escribir los datos en el Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(fecha)
        parcel.writeString(motivo)
        parcel.writeDouble(costo)
        parcel.writeString(medico)
        parcel.writeInt(pacienteId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CitaMedica> {
        override fun createFromParcel(parcel: Parcel): CitaMedica {
            return CitaMedica(parcel)
        }

        override fun newArray(size: Int): Array<CitaMedica?> {
            return arrayOfNulls(size)
        }
    }
}