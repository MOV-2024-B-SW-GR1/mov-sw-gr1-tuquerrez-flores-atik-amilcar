package com.example.consultorioaatf

import android.os.Parcel
import android.os.Parcelable

class Paciente(
    val id: Int,
    val nombre: String,
    var edad: Int,
    var genero: String,
    var telefono: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable {
    val latitud: Double
        get() = latitude
    val longitud: Double
        get() = longitude
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun toString(): String {
        return "$nombre ($edad años, $genero, lugar: $telefono , latitud: $latitude, longitud: $longitude)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeInt(edad)
        parcel.writeString(genero)
        parcel.writeString(telefono)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Paciente> {
        override fun createFromParcel(parcel: Parcel): Paciente {
            return Paciente(parcel)
        }

        override fun newArray(size: Int): Array<Paciente?> {
            return arrayOfNulls(size)
        }
    }
}