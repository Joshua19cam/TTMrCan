package com.example.ttmrcan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class MisMascotas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_mascotas)

        val recivId = intent.getStringExtra("idUsuario")

        val bundle = Bundle()
        bundle.putString("recivId", recivId)

        val fragment = FragmentoListaMascotas()
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainerMisMascotas, fragment)
            .commit()

       /* supportFragmentManager.commit {
            replace<FragmentoListaMascotas>(R.id.frameContainerMisMascotas)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }*/
    }
}