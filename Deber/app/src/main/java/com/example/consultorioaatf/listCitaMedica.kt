package com.example.consultorioaatf

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class listCitaMedica : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listaCitaMedica = mutableListOf<CitaMedica>()
    private var idPaciente = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_cita_medica)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_list_citaMedica)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.lv_citaMedica)
        val txtCitaMedica = findViewById<TextView>(R.id.txt_citaMedica)
        val btnAnadirCitaMedica = findViewById<Button>(R.id.btn_crear_citaMedica)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaCitaMedica.map { it.motivo })
        listView.adapter = adapter

        val paciente = intent.getParcelableExtra<Paciente>("paciente")
        if (paciente != null) {
            idPaciente = paciente.id
        }
        txtCitaMedica.setText("${paciente?.nombre?.toUpperCase(Locale.ROOT)}'S Citas Médicas")
        registerForContextMenu(listView)
        cargarDatosDesdeBaseDeDatos(idPaciente)

        btnAnadirCitaMedica.setOnClickListener {
            irActividad(citaMedicaOperaciones::class.java)
        }
    }

    var posicionItemSeleccionado = -1
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        // llenamos opciones del menu
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_cita_medica, menu)
        menu?.findItem(R.id.verCitasMedicas)?.isVisible = false
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val posicion = info.position
        posicionItemSeleccionado = posicion
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val accion = data?.getStringExtra("accion")
            if (accion == "crear" || accion == "editar") {
                cargarDatosDesdeBaseDeDatos(idPaciente)
            }
        }
    }


    override fun onContextItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_editar_cita -> {
                val citaMedicaSeleccionada = listaCitaMedica[posicionItemSeleccionado]
                irActividad(citaMedicaOperaciones::class.java, citaMedicaSeleccionada)
                return true
            }

            R.id.mi_eliminar_cita -> {
                abrirDialogo()
                return true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.cl_list_citaMedica),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("Cerrar") { snack.dismiss() }
        snack.show()
    }

    fun abrirDialogo() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Desea Eliminar")
        builder.setPositiveButton(
            "Aceptar",
            DialogInterface.OnClickListener{ dialog, which ->
                val reparacionSeleccionada = listaCitaMedica[posicionItemSeleccionado]
                val id = reparacionSeleccionada.id

                // Llamar al método de eliminación
                val eliminado = PBaseDeDatos.tablaPacienteCita?.eliminarCitaMedica(id)
                if (eliminado == true) {
                    mostrarSnackbar("Reparación eliminada correctamente.")
                    cargarDatosDesdeBaseDeDatos(idPaciente) // Refrescar la lista
                } else {
                    mostrarSnackbar("Error al eliminar la reparación.")
                }
            }
        )
        builder.setNegativeButton(
            "Cancelar",
            null
        )
        val dialogo = builder.create()
        dialogo.show()
    }

    fun cargarDatosDesdeBaseDeDatos(idPaciente: Int) {
        try {
            val citaMedica = PBaseDeDatos.tablaPacienteCita?.obtenerCitasPorPaciente(idPaciente)
            listaCitaMedica.clear()
            if (citaMedica != null) {
                listaCitaMedica.addAll(citaMedica)
            }
            adapter.clear()
            adapter.addAll(listaCitaMedica.map { citaMedica ->
                val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formato de entrada, según el formato almacenado en la base de datos
                val sdfOutput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Formato deseado de salida
                val fechaFormateada = try {
                    val date = sdfInput.parse(citaMedica.fecha) // Convierte la cadena a Date
                    sdfOutput.format(date) // Formatea la fecha
                } catch (e: Exception) {
                    "Fecha inválida" // Manejo en caso de error
                }
                "${citaMedica.motivo} - $fechaFormateada"
            })
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            mostrarSnackbar("Error al cargar las citas médicas: ${e.message}")
        }
    }


    fun irActividad(clase: Class<*>, citaMedica: CitaMedica? = null) {
        val intentExplicito = Intent(this, clase)
        intentExplicito.putExtra("idPaciente", idPaciente)

        if (citaMedica != null) {
            intentExplicito.putExtra("modo", "editar")
            intentExplicito.putExtra("citaMedica", citaMedica)
        } else {
            intentExplicito.putExtra("modo", "crear")
        }

        startActivityForResult(intentExplicito, 1)
        Log.d("ListCitaMedica", "Cargando citas para el paciente con ID: $idPaciente")
    }
}