package com.example.sw2024bgr1_aatf

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    fun mostrarSnackbar(texto:String){
        val snack = Snackbar.make(
            findViewById(R.id.main_ciclo_vida),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.show()
    }

    val callbackContenidoIntentExplicito = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            if(result.data != null){
                val data = result.data?.getStringExtra("nombreModificado")
                mostrarSnackbar("$data")
            }
        }
    }


    val callbackContenidoIntentImplicito =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
                result ->
            if(result.resultCode == Activity.RESULT_OK){
                if(result.data != null){
                    // validacion de contacto
                    if(result.data!!.data != null){
                        val uri: Uri = result.data!!.data!!
                        val cursor = contentResolver.query(
                            uri, null, null, null, null, null
                        )
                        cursor?.moveToFirst()
                        val indiceTelefono = cursor?.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                        val telefono = cursor?.getString(indiceTelefono!!)
                        cursor?.close()
                        mostrarSnackbar("Telefono $telefono")
                    }
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ciclo_vida)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Inicializar BDD
        EBaseDeDatos.tablaEntrenador = ESqliteHelperEntrenador(this)


        val botonCicloVida = findViewById<Button>(R.id.cl_ciclo_vida)
        botonCicloVida
            .setOnClickListener {
                irActividad(ACicloVida::class.java)
            }

        val botonListView = findViewById<Button>(R.id.btn_ir_list_view)
        botonListView
            .setOnClickListener {
                irActividad(BListView::class.java)
            }


        val botonImplicito = findViewById<Button>(R.id.btn_ir_intent_implicito)
        botonImplicito
            .setOnClickListener {
                val intentConRespuesta = Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                )
                callbackContenidoIntentImplicito.launch(intentConRespuesta)
            }

        val botonExplicito = findViewById<Button>(R.id.btn_ir_intent_explicito)
        botonExplicito
            .setOnClickListener {
                val intentExplicito = Intent(
                    this, CIntentExplicitoParametros::class.java
                )
                intentExplicito.putExtra("nombre", "Atik")
                intentExplicito.putExtra("apellido", "Tuquerrez")
                intentExplicito.putExtra("edad", 21)
                intentExplicito.putExtra("entrenador",
                    BEntrenador(1,"Atik","Ejemplo")
                )
                callbackContenidoIntentExplicito.launch(intentExplicito)
            }

        val botonIrSqlite = findViewById<Button>(R.id.btn_sqlite)
        botonIrSqlite
            .setOnClickListener {
                irActividad(ECrudEntrenador::class.java)
            }

        val botonRecylerView = findViewById<Button>(R.id.btn_recycler_view)
        botonRecylerView
            .setOnClickListener {
                irActividad(FRecyclerView::class.java)
            }

        val botonGMaps = findViewById<Button>(R.id.btn_google_maps)
        botonGMaps
            .setOnClickListener {
                irActividad(GGoogleMaps::class.java)
            }

    }
    fun irActividad(clase:Class<*>){
        startActivity(Intent(this, clase))
    }
}