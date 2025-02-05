package com.example.consultorioaatf

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class PacienteCitaBaseDatos(
    context: Context?
): SQLiteOpenHelper(
    context,
    "paciente-cita_db",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val scriptSQLCrearTablaPaciente =
            """
                CREATE TABLE PACIENTE(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    edad INTEGER,
                    genero VARCHAR(10),
                    telefono VARCHAR(10),
                    latitude REAL,
                    longitude REAL
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaPaciente)

        val scriptSQLCrearTablaCitaMedica =
            """
                CREATE TABLE CITA_MEDICA(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha VARCHAR(10),
                    motivo VARCHAR(100),
                    costo REAL,
                    medico VARCHAR(50),
                    pacienteId INTEGER,
                    FOREIGN KEY(pacienteId) REFERENCES PACIENTE(id) ON DELETE CASCADE
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaCitaMedica)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun obtenerTodosLosPacientes(): List<Paciente> {
        val listaPacientes: MutableList<Paciente> = ArrayList()
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = "SELECT * FROM PACIENTE"
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, null)

        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val id = resultadoConsultaLectura.getInt(0)
                val nombre = resultadoConsultaLectura.getString(1)
                val edad = resultadoConsultaLectura.getInt(2)
                val genero = resultadoConsultaLectura.getString(3)
                val telefono = resultadoConsultaLectura.getString(4)
                val latitude = resultadoConsultaLectura.getDouble(5)
                val longitude = resultadoConsultaLectura.getDouble(6)
                val paciente = Paciente(id, nombre, edad, genero, telefono, latitude, longitude)
                listaPacientes.add(paciente)
            } while (resultadoConsultaLectura.moveToNext())
        }
        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return listaPacientes
    }

    fun crearPaciente(
        nombre: String?,
        edad: Int,
        genero: String?,
        telefono: String?,
        latitude: Double,
        longitude: Double
    ): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues()
        valoresGuardar.put("nombre", nombre)
        valoresGuardar.put("edad", edad)
        valoresGuardar.put("genero", genero)
        valoresGuardar.put("telefono", telefono)
        valoresGuardar.put("latitude", latitude)
        valoresGuardar.put("longitude", longitude)
        val resultadoGuardar = baseDatosEscritura.insert("PACIENTE", null, valoresGuardar)
        baseDatosEscritura.close()
        return resultadoGuardar != -1L
    }

    fun eliminarPaciente(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val parametrosConsultaDelete = arrayOf(id.toString())
        val resultadoEliminar = baseDatosEscritura.delete("PACIENTE", "id=?", parametrosConsultaDelete)
        baseDatosEscritura.close()
        return resultadoEliminar != -1
    }

    fun actualizarPaciente(
        id: Int,
        nombre: String?,
        edad: Int,
        genero: String?,
        telefono: String?,
        latitude: Double,
        longitude: Double
    ): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresAActualizar = ContentValues()
        valoresAActualizar.put("nombre", nombre)
        valoresAActualizar.put("edad", edad)
        valoresAActualizar.put("genero", genero)
        valoresAActualizar.put("telefono", telefono)
        valoresAActualizar.put("latitude", latitude)
        valoresAActualizar.put("longitude", longitude)
        val parametrosConsultaActualizar = arrayOf(id.toString())
        val resultadoActualizar = baseDatosEscritura.update(
            "PACIENTE",
            valoresAActualizar,
            "id=?",
            parametrosConsultaActualizar
        )
        baseDatosEscritura.close()
        return resultadoActualizar != -1
    }

    // Cita Medica
    fun obtenerCitasPorPaciente(pacienteId: Int): List<CitaMedica> {
        val listaCitas = mutableListOf<CitaMedica>()
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = "SELECT * FROM CITA_MEDICA WHERE pacienteId = ?"
        val parametrosConsultaLectura = arrayOf(pacienteId.toString())
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, parametrosConsultaLectura)

        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val id = resultadoConsultaLectura.getInt(0)
                val fecha = resultadoConsultaLectura.getString(1)
                val motivo = resultadoConsultaLectura.getString(2)
                val costo = resultadoConsultaLectura.getDouble(3)
                val medico = resultadoConsultaLectura.getString(4)

                val citaMedica = CitaMedica(id, fecha, motivo, costo, medico, pacienteId)
                listaCitas.add(citaMedica)
            } while (resultadoConsultaLectura.moveToNext())
        }
        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return listaCitas
    }

    fun crearCitaMedica(fecha: String, motivo: String, costo: Double, medico: String, pacienteId: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues()
        valoresGuardar.put("fecha", fecha)
        valoresGuardar.put("motivo", motivo)
        valoresGuardar.put("costo", costo)
        valoresGuardar.put("medico", medico)
        valoresGuardar.put("pacienteId", pacienteId)
        val resultadoGuardar = baseDatosEscritura.insert("CITA_MEDICA", null, valoresGuardar)
        baseDatosEscritura.close()
        return resultadoGuardar != -1L
    }

    fun eliminarCitaMedica(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val parametrosConsultaDelete = arrayOf(id.toString())
        val resultadoEliminar = baseDatosEscritura.delete("CITA_MEDICA", "id=?", parametrosConsultaDelete)
        baseDatosEscritura.close()
        return resultadoEliminar != -1
    }

    fun actualizarCitaMedica(id: Int, fecha: String, motivo: String, costo: Double, medico: String, pacienteId: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresAActualizar = ContentValues()
        valoresAActualizar.put("fecha", fecha)
        valoresAActualizar.put("motivo", motivo)
        valoresAActualizar.put("costo", costo)
        valoresAActualizar.put("medico", medico)
        valoresAActualizar.put("pacienteId", pacienteId)
        val parametrosConsultaActualizar = arrayOf(id.toString())
        val resultadoActualizar = baseDatosEscritura.update("CITA_MEDICA", valoresAActualizar, "id=?", parametrosConsultaActualizar)
        baseDatosEscritura.close()
        return resultadoActualizar != -1

    }
}
