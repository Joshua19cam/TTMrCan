package com.example.ttmrcan

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class FlashScreen : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener{

    private var networkChangeReceiver: NetworkChangeReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_screen)

        networkChangeReceiver = NetworkChangeReceiver()
        networkChangeReceiver?.let {
            it.init(this)
            it.setOnNetworkChangeListener(object : NetworkChangeReceiver.NetworkChangeListener {
                override fun onNetworkAvailable() {
                    // La conexión a Internet está disponible
                    startTimer()
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                    supportActionBar?.hide()
                }

                override fun onNetworkUnavailable() {
                    // No hay conexión a Internet, muestra el diálogo
                    mostrarDialogo()
                }
            })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        networkChangeReceiver?.release(this)
    }

    private fun mostrarDialogo() {
        val dialogo = this.let { Dialog(it, R.style.CustomDialogStyle) }
        dialogo.setContentView(R.layout.dialogo_no_conexion)
        val titulo = dialogo.findViewById<TextView>(R.id.dialogo_correcto)
        val subtitulo = dialogo.findViewById<TextView>(R.id.dialogo_correcto_sub)
        val btn = dialogo.findViewById<Button>(R.id.buttonOk)
        titulo?.text = "Sin conexión a Internet"
        subtitulo?.text = "No estás conectado a Internet. Por favor, verifica tu conexión e inténtalo nuevamente."
        btn?.setOnClickListener {
            finishAffinity()
        }

        dialogo?.setCancelable(false)
        dialogo?.show()
    }

    private fun startTimer(){

        object: CountDownTimer(3000, 1000){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {

                supportFragmentManager.commit {
                    replace<FragmentoLogin>(R.id.frameContainerLogin)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                /*val intent = Intent(applicationContext, LogIn::class.java).apply{}
                startActivity(intent)*/
            }

        }.start()
    }

    override fun onNetworkAvailable() {
        TODO("Not yet implemented")
    }

    override fun onNetworkUnavailable() {
        TODO("Not yet implemented")
    }
}