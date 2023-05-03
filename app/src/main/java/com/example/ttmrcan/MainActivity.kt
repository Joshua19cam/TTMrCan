package com.example.ttmrcan

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var irRegistro: Button
    lateinit var irMisMascotas: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        irRegistro = findViewById(R.id.buttonIrRegistro)
        irMisMascotas = findViewById(R.id.buttonMisMascotas)

        irRegistro.setOnClickListener {
            val iniciar = Intent(this, RegistroCliente::class.java)
            startActivity(iniciar)
        }
        irMisMascotas.setOnClickListener {
            val iniciarMisMascotas = Intent(this, MisMascotas::class.java)
            startActivity(iniciarMisMascotas)
        }
    }
}