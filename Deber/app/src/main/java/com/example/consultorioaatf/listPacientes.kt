package com.example.consultorioaatf

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class listPacientes : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listaPacientes = mutableListOf<Paciente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_pacientes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_lista_pacientes)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.lv_paciente)
        val botonAnadirVehiculo = findViewById<Button>(R.id.btn_crear_paciente)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaPacientes.map { it.nombre })
        listView.adapter = adapter

        registerForContextMenu(listView)

        cargarDatosDesdeBaseDeDatos()

        botonAnadirVehiculo.setOnClickListener {
            irActividad(PacienteOperaciones::class.java) // Pasa el requestCode 1
        }


    }

    var posicionItemSeleccionado = -1 // VARIABLE GLOBAL
    override fun onCreateContextMenu(
        menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        // llenamos opciones del menu
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_paciente, menu)
        // obtener id
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val posicion = info.position
        posicionItemSeleccionado = posicion
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cargarDatosDesdeBaseDeDatos() // Refresca la lista
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editar -> {
                val pacienteSeleccionado = listaPacientes[posicionItemSeleccionado]
                irActividad(PacienteOperaciones::class.java, pacienteSeleccionado)

                return true
            }
            R.id.eliminar -> {
                abrirDialogo()
                return true
            }
            R.id.verCitasMedicas -> {
                val pacienteSeleccionado = listaPacientes[posicionItemSeleccionado]
                irActividad(listCitaMedica::class.java, pacienteSeleccionado)
                return true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    fun abrirDialogo() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Desea Eliminar?")
        builder.setPositiveButton(
            "Aceptar",
            DialogInterface.OnClickListener { dialog, which ->
                val pacienteSeleccionado = listaPacientes[posicionItemSeleccionado]
                val id = pacienteSeleccionado.id

                // Llamar al método de eliminación
                val eliminado = PBaseDeDatos.tablaPacienteCita?.eliminarPaciente(id)
                if (eliminado == true) {
                    mostrarSnackbar("Paciente eliminado correctamente.")
                    cargarDatosDesdeBaseDeDatos() // Refrescar la lista
                } else {
                    mostrarSnackbar("Error al eliminar el paciente.")
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

    fun cargarDatosDesdeBaseDeDatos() {
        val paciente = PBaseDeDatos.tablaPacienteCita?.obtenerTodosLosPacientes()
        listaPacientes.clear()
        if (paciente != null) {
            listaPacientes.addAll(paciente)
        }
        adapter.clear()
        adapter.addAll(listaPacientes.map { it.nombre })
        adapter.notifyDataSetChanged()
    }

    fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.cl_lista_pacientes),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("Cerrar") { snack.dismiss() }
        snack.show()
    }

    fun irActividad(clase: Class<*>, paciente: Paciente? = null) {
        val intentExplicito = Intent(this, clase)
        if (paciente != null) {
            intentExplicito.putExtra("modo", "editar")
            intentExplicito.putExtra("paciente", paciente)
        } else {
            intentExplicito.putExtra("modo", "crear")
        }
        startActivityForResult(intentExplicito, 1)
        Log.d("PacienteSeleccionado", "Paciente: $paciente")
    }

}