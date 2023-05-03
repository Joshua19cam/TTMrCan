package com.example.ttmrcan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class MisMascotas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_mascotas)

        supportFragmentManager.commit {
            replace<FragmentoListaMascotas>(R.id.frameContainerMisMascotas)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }
    }
}