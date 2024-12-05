package org.example.Utils

import java.io.File

object FileUtil {
    fun leerArchivo(nombreArchivo: String): List<String> {
        val archivo = File(nombreArchivo)
        if (!archivo.exists()) archivo.createNewFile()
        return archivo.readLines()
    }

    fun escribirArchivo(nombreArchivo: String, datos: List<String>) {
        File(nombreArchivo).writeText(datos.joinToString("\n"))
    }
}