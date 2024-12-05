package org.example

import org.example.Models.CitaMedica
import org.example.Models.Paciente
import org.example.Services.PacienteService
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Scanner

fun main() {
    val pacienteService = PacienteService("pacientes.txt")
    val scanner = Scanner(System.`in`)

    fun leerEntero(mensaje: String): Int {
        while (true) {
            print(mensaje)
            val input = scanner.nextLine()
            try {
                return input.toInt()
            } catch (e: NumberFormatException) {
                println(" Por favor, ingresa un número entero válido.")
            }
        }
    }

    fun leerDouble(mensaje: String): Double {
        while (true) {
            print(mensaje)
            val input = scanner.nextLine()
            try {
                return input.toDouble()
            } catch (e: NumberFormatException) {
                println("⚠Por favor, ingresa un número decimal válido.")
            }
        }
    }

    fun leerFecha(mensaje: String): LocalDate {
        while (true) {
            print(mensaje)
            val input = scanner.nextLine()
            try {
                return LocalDate.parse(input)
            } catch (e: DateTimeParseException) {
                println("Por favor, ingresa una fecha válida en formato YYYY-MM-DD.")
            }
        }
    }

    fun leerBooleano(mensaje: String): Boolean {
        while (true) {
            print(mensaje)
            val input = scanner.nextLine().lowercase()
            if (input == "true" || input == "false") {
                return input.toBoolean()
            } else {
                println("Por favor, ingresa 'true' o 'false'.")
            }
        }
    }

    while (true) {
        println("\n=========================")
        println("   Menú Pacientes ")
        println("=========================")
        println("1️⃣  Registrar Paciente")
        println("2️⃣  Ver Pacientes")
        println("3️⃣  Agregar Cita Médica")
        println("4️⃣  Ver Citas de un Paciente")
        println("5️⃣  Editar Cita Médica")
        println("6️⃣  Eliminar Paciente")
        println("7️⃣  Salir")
        print("\nElige una opción: ")

        when (scanner.nextLine().toIntOrNull()) {
            1 -> {
                println("\n--- Registrar Paciente ---")
                print(" Nombre del Paciente: ")
                val nombre = scanner.nextLine()
                val id = leerEntero(" ID del Paciente: ")
                val edad = leerEntero(" Edad: ")
                print(" Género: ")
                val genero = scanner.nextLine()
                val tieneSeguro = leerBooleano(" ¿Tiene seguro médico? (true/false): ")

                val nuevoPaciente = Paciente(nombre, id, edad, genero, tieneSeguro)
                pacienteService.crearPaciente(nuevoPaciente)
                println(" Paciente '$nombre' registrado exitosamente.")
            }

            2 -> {
                println("\n--- Lista de Pacientes ---")
                val pacientes = pacienteService.leerPacientes()
                if (pacientes.isEmpty()) {
                    println(" No hay pacientes registrados.")
                } else {
                    pacientes.forEach { paciente ->
                        println("\nNombre: ${paciente.nombre}")
                        println("ID: ${paciente.id}")
                        println("Edad: ${paciente.edad}")
                        println("Género: ${paciente.genero}")
                        println("Seguro: ${if (paciente.tieneSeguro) "Sí" else "No"}")
                    }
                }
            }

            3 -> {
                println("\n--- Agregar Cita Médica ---")
                val idPaciente = leerEntero(" ID del Paciente: ")
                val paciente = pacienteService.leerPacientes().find { it.id == idPaciente }
                if (paciente == null) {
                    println(" Paciente con ID '$idPaciente' no encontrado.")
                    continue
                }

                val idCita = leerEntero(" ID de la Cita: ")
                val fecha = leerFecha(" Fecha de la Cita (YYYY-MM-DD): ")
                print(" Motivo de la Cita: ")
                val motivo = scanner.nextLine()
                val costo = leerDouble(" Costo: ")
                print("️ Médico: ")
                val medico = scanner.nextLine()

                val nuevaCita = CitaMedica(idCita, fecha, motivo, costo, medico)
                pacienteService.agregarCita(idPaciente, nuevaCita)
                println(" Cita médica agregada exitosamente.")
            }

            4 -> {
                println("\n--- Ver Citas de un Paciente ---")
                val idPaciente = leerEntero(" ID del Paciente: ")
                val paciente = pacienteService.leerPacientes().find { it.id == idPaciente }
                if (paciente == null) {
                    println(" Paciente con ID '$idPaciente' no encontrado.")
                    continue
                }

                if (paciente.citas.isEmpty()) {
                    println(" El paciente no tiene citas médicas registradas.")
                } else {
                    paciente.citas.forEach { cita ->
                        println("\n ID de la Cita: ${cita.id}")
                        println(" Fecha: ${cita.fecha}")
                        println(" Motivo: ${cita.motivo}")
                        println(" Costo: ${cita.costo}")
                        println(" Médico: ${cita.medico}")
                    }
                }
            }

            5 -> {
                println("\n--- Editar Cita Médica ---")
                val idPaciente = leerEntero(" ID del Paciente: ")
                val paciente = pacienteService.leerPacientes().find { it.id == idPaciente }
                if (paciente == null) {
                    println(" Paciente con ID '$idPaciente' no encontrado.")
                    continue
                }

                val idCita = leerEntero(" ID de la Cita: ")
                val cita = paciente.citas.find { it.id == idCita }
                if (cita == null) {
                    println(" Cita con ID '$idCita' no encontrada.")
                    continue
                }

                println("\n--- Editar información de la cita ---")
                val nuevaFecha = leerFecha(" Nueva fecha (YYYY-MM-DD): ")
                val nuevoCosto = leerDouble(" Nuevo costo: ")

                pacienteService.actualizarCita(idPaciente, idCita, nuevaFecha, cita.motivo, nuevoCosto, cita.medico)
                println(" Cita médica actualizada exitosamente.")
            }

            6 -> {
                println("\n--- Eliminar Paciente ---")
                val idPaciente = leerEntero(" ID del Paciente a eliminar: ")
                if (pacienteService.leerPacientes().none { it.id == idPaciente }) {
                    println(" Paciente con ID '$idPaciente' no encontrado.")
                } else {
                    pacienteService.eliminarPaciente(idPaciente)
                    println(" Paciente eliminado exitosamente.")
                }
            }

            7 -> {
                println(" ¡Hasta pronto!")
                break
            }

            else -> println(" Opción inválida. Por favor, intenta de nuevo.")
        }
    }
}