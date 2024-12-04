package com.example.sw2024bgr1_aatf

class BBaseDatosMemoria {
    companion object{
        val arregloBEntrenador = arrayListOf<BEntrenador>()
        init {
            arregloBEntrenador.add(BEntrenador(1,"Atik","a@a.com"))
            arregloBEntrenador.add(BEntrenador(2,"Tuquerrez","b@b.com"))
            arregloBEntrenador.add(BEntrenador(3,"Luis","l@l.com"))
        }
    }
}