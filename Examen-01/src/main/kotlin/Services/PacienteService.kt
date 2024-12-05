package org.example.Services

import org.example.Models.CitaMedica
import org.example.Models.Paciente
import org.example.Utils.FileUtil
import java.time.LocalDate

class PacienteService(private val archivo: String) {

    fun crearPaciente(paciente: Paciente) {
        val pacientes = leerPacientes().toMutableList()
        pacientes.add(paciente)
        guardarPacientes(pacientes)
    }

    fun leerPacientes(): List<Paciente> {
        return FileUtil.leerArchivo(archivo).map { Paciente.fromString(it) }
    }

    fun actualizarPaciente(id: Int, edad: Int?, genero: String?, tieneSeguro: Boolean?) {
        val pacientes = leerPacientes().toMutableList()
        val index = pacientes.indexOfFirst { it.id == id }
        if (index != -1) {
            edad?.let { pacientes[index].edad = it }
            genero?.let { pacientes[index].genero = it }
            tieneSeguro?.let { pacientes[index].tieneSeguro = it }
            guardarPacientes(pacientes)
        }
    }

    fun eliminarPaciente(id: Int) {
        val pacientes = leerPacientes().filter { it.id != id }
        guardarPacientes(pacientes)
    }

    fun agregarCita(idPaciente: Int, nuevaCita: CitaMedica) {
        val pacientes = leerPacientes().toMutableList()
        val pacienteIndex = pacientes.indexOfFirst { it.id == idPaciente }
        if (pacienteIndex != -1) {
            pacientes[pacienteIndex].citas.add(nuevaCita)
            guardarPacientes(pacientes)
        }
    }

    fun eliminarCita(idPaciente: Int, idCita: Int) {
        val pacientes = leerPacientes().toMutableList()
        val index = pacientes.indexOfFirst { it.id == idPaciente }
        if (index != -1) {
            pacientes[index].citas.removeIf { it.id == idCita }
            guardarPacientes(pacientes)
        }
    }

    fun actualizarCita(idPaciente: Int, idCita: Int, nuevaFecha: LocalDate?, nuevoMotivo: String?, nuevoCosto: Double?, nuevoMedico: String?) {
        val pacientes = leerPacientes().toMutableList()
        val pacienteIndex = pacientes.indexOfFirst { it.id == idPaciente }
        if (pacienteIndex != -1) {
            val citas = pacientes[pacienteIndex].citas
            val citaIndex = citas.indexOfFirst { it.id == idCita }
            if (citaIndex != -1) {
                nuevaFecha?.let { citas[citaIndex].fecha = it }
                nuevoMotivo?.let { citas[citaIndex].motivo = it }
                nuevoCosto?.let { citas[citaIndex].costo = it }
                nuevoMedico?.let { citas[citaIndex].medico = it }
                guardarPacientes(pacientes)
            }
        }
    }

    private fun guardarPacientes(pacientes: List<Paciente>) {
        FileUtil.escribirArchivo(archivo, pacientes.map { it.toString() })
    }
}