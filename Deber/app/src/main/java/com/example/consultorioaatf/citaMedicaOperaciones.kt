package com.example.consultorioaatf

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import java.util.Date

class citaMedicaOperaciones : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cita_medica_operaciones)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_operaciones_citaMedica)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val modo = intent.getStringExtra("modo") ?: "crear"
        val citaMedica: CitaMedica? = intent.getParcelableExtra("citaMedica")

        val botonGuardarCitaMedica = findViewById<Button>(R.id.btn_guardar_citaMedica)
        val inputMotivo = findViewById<EditText>(R.id.text_motivo_citaMedica)
        val inputFecha = findViewById<EditText>(R.id.text_fecha_citaMedica)
        val inputCosto = findViewById<EditText>(R.id.text_costo_citaMedica)
        val inputMedico = findViewById<EditText>(R.id.text_medico_citaMedica)
        val inputPacienteId = findViewById<EditText>(R.id.text_numero_citaMedica)

        if (modo == "editar" && citaMedica != null) {
            inputFecha.setText(citaMedica.fecha)
            inputMotivo.setText(citaMedica.motivo)
            inputCosto.setText(citaMedica.costo.toString())
            inputMedico.setText(citaMedica.medico)
            inputPacienteId.setText(citaMedica.pacienteId.toString())
            botonGuardarCitaMedica.text = "Actualizar"
        } else {
            botonGuardarCitaMedica.text = "Crear"
        }

        botonGuardarCitaMedica.setOnClickListener {
            val fechaString = inputFecha.text.toString().trim()
            val motivoString = inputMotivo.text.toString().trim()
            val costoString = inputCosto.text.toString().trim()
            val medicoString = inputMedico.text.toString().trim()
            val pacienteIdString = inputPacienteId.text.toString().trim()

            if (fechaString.isEmpty() || motivoString.isEmpty() || costoString.isEmpty() || medicoString.isEmpty() || pacienteIdString.isEmpty()) {
                mostrarSnackbar("Por favor, llene todos los campos.")
                return@setOnClickListener
            }

            val costo = costoString.toDoubleOrNull()
            if (costo == null || costo < 0) {
                mostrarSnackbar("Por favor, ingrese un costo válido.")
                return@setOnClickListener
            }

            val pacienteId = pacienteIdString.toIntOrNull()
            if (pacienteId == null || pacienteId < 0) {
                mostrarSnackbar("Por favor, ingrese un ID de paciente válido.")
                return@setOnClickListener
            }

            if (modo == "crear") {
                val respuesta = PBaseDeDatos.tablaPacienteCita?.crearCitaMedica(
                    fechaString,
                    motivoString,
                    costo,
                    medicoString,
                    pacienteId
                )
                if (respuesta == true) {
                    mostrarSnackbar("Cita Médica creada exitosamente.")
                    val data = Intent().apply {
                        putExtra("accion", "crear")
                    }
                    setResult(RESULT_OK, data)
                    finish()
                } else {
                    mostrarSnackbar("Error al crear la cita médica.")
                }

            } else if (modo == "editar" && citaMedica != null) {
                val respuesta = PBaseDeDatos.tablaPacienteCita?.actualizarCitaMedica(
                    citaMedica.id,
                    fechaString,
                    motivoString,
                    costo,
                    medicoString,
                    pacienteId
                )

                if (respuesta == true) {
                    mostrarSnackbar("Cita Médica actualizada exitosamente.")
                    setResult(RESULT_OK)
                    finish()
                } else {
                    mostrarSnackbar("Error al actualizar la cita médica.")
                }
            }
        }
    }


    private fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.cl_operaciones_citaMedica),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("Cerrar") { snack.dismiss() }
        snack.show()
    }

}
