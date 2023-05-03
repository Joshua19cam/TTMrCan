package com.example.ttmrcan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class RegistroClienteV : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_cliente_v)

        supportFragmentManager.commit {
            replace<FragmentoCapcha>(R.id.frameContainer)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }
    }
}