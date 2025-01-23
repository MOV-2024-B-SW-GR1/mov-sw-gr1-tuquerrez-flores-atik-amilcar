package com.example.consultorioaatf

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class PacienteOperaciones : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_paciente_operaciones)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_operaciones_paciente)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val modo = intent.getStringExtra("modo") ?: "crear"
        val paciente: Paciente? = intent.getParcelableExtra("paciente")
        Log.d("DatosRecibidos", "Modo: $modo, Paciente: $paciente")

        val botonGuardarPaciente = findViewById<Button>(R.id.btn_guardar_paciente)
        val inputNombre = findViewById<EditText>(R.id.text_nombre_paciente)
        val inputEdad = findViewById<EditText>(R.id.text_edad_paciente)
        val inputGenero = findViewById<EditText>(R.id.text_genero_paciente)
        val inputTelefono = findViewById<EditText>(R.id.text_telefono_paciente)

        if (modo == "editar" && paciente != null) {
            try {
                inputNombre.setText(paciente.nombre)
                inputEdad.setText(paciente.edad.toString())
                inputGenero.setText(paciente.genero)
                inputTelefono.setText(paciente.telefono)
            } catch (e: Exception) {
                e.printStackTrace()
                mostrarSnackbar("Error al cargar los datos del paciente.")
                finish() // Termina la actividad para evitar un estado incorrecto
            }
        } else {
            botonGuardarPaciente.text = "Crear"
        }

        botonGuardarPaciente.setOnClickListener {
            val nombre = inputNombre.text.toString().trim()
            val edad = inputEdad.text.toString().toInt()
            val genero = inputGenero.text.toString().trim()
            val telefono = inputTelefono.text.toString().trim()

            if (nombre.isEmpty() || genero.isEmpty() || telefono.isEmpty()) {
                mostrarSnackbar("Por favor, llene todos los campos.")
                return@setOnClickListener
            }

            if (modo == "crear") {
                val respuesta = PBaseDeDatos.tablaPacienteCita?.crearPaciente(
                    nombre,
                    edad,
                    genero,
                    telefono
                )

                if (respuesta == true) {
                    mostrarSnackbar("Paciente creado exitosamente.")
                    setResult(RESULT_OK)
                    finish()
                } else {
                    mostrarSnackbar("Error al crear el paciente.")
                }
            } else if (modo == "editar" && paciente != null) {
                val respuesta = PBaseDeDatos.tablaPacienteCita?.actualizarPaciente(
                    paciente.id,
                    nombre,
                    edad,
                    genero,
                    telefono
                )

                if (respuesta == true) {
                    mostrarSnackbar("Paciente actualizado exitosamente.")
                    setResult(RESULT_OK)
                    finish()
                } else {
                    mostrarSnackbar("Error al actualizar el paciente.")
                }
            }
        }

    }

    private fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.cl_operaciones_paciente),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("Cerrar") { snack.dismiss() }
        snack.show()
    }
}