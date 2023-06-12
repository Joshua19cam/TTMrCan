package com.example.ttmrcan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.ttmrcan.databinding.NavHeaderBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuClienteR : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener {

    private var networkChangeReceiver: NetworkChangeReceiver? = null

    lateinit var toggle: ActionBarDrawerToggle
    var usuario = Usuario(-1,"", "","","",""
        ,"","",-1,"","","",0,
        0,"",1)
//    lateinit var bindingNavH : NavHeaderBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_cliente_r)

        networkChangeReceiver = NetworkChangeReceiver()
        networkChangeReceiver?.let {
            it.init(this)
            it.setOnNetworkChangeListener(object : NetworkChangeReceiver.NetworkChangeListener {
                override fun onNetworkAvailable() {
                    // La conexión a Internet está disponible
                    /*val sharedPreferencesUsuario = getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
                    val valorNombre = sharedPreferencesUsuario.getString("nombreCompleto",null)
                    val valorCorreo = sharedPreferencesUsuario.getString("email",null)
                    val valorImagen = sharedPreferencesUsuario.getString("img",null)*/

                    val sharedPreferencesLogin = getSharedPreferences("login", Context.MODE_PRIVATE)
                    val correoGuardado = sharedPreferencesLogin.getString("correo", null)

                    obtenerDatos(correoGuardado.toString())

                    val fragmentManager = supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()

                    val misMascotasFragment = FragmentoListaMascotas()
                    val citasFragment = FragmentoCitas()
                    val perfilFragment = FragmentoPerfilUsuario()

                    fragmentTransaction.replace(R.id.fragment_container, misMascotasFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()

                    val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
                    val navView : NavigationView = findViewById(R.id.navigation_view)

                    //TODO Lo de nav header ejemplo

                    /*val navigationView = findViewById<NavigationView>(R.id.navigation_view)
                    val headerView = navigationView.getHeaderView(0)
                    val nameTextView = headerView.findViewById<TextView>(R.id.tvUserName)
                    val emailTextView = headerView.findViewById<TextView>(R.id.tvUserEmail)
                    val imgTv = headerView.findViewById<ImageView>(R.id.ivUserName)



                    if(usuario.foto_usuario!="0"){
                        val uniqueId = System.currentTimeMillis().toString()
                        Glide.with(this@MenuClienteR).load(usuario.foto_usuario).signature(ObjectKey(uniqueId)).into(imgTv)
                    }

                    nameTextView.text = usuario.nombre_usuario
                    emailTextView.text = usuario.email_usuario*/



                    toggle = ActionBarDrawerToggle(this@MenuClienteR, drawerLayout, R.string.open,R.string.close )
                    drawerLayout.addDrawerListener(toggle)
                    toggle.syncState()

                    supportActionBar?.setDisplayHomeAsUpEnabled(true)


                    navView.setNavigationItemSelectedListener {

                        it.isChecked = true

                        when(it.itemId){
                            R.id.nav_mascotas -> {
                                replaceFragment(misMascotasFragment, it.title.toString())
                                drawerLayout.closeDrawer(GravityCompat.START)

                            }
                            R.id.nav_citas -> {
                                replaceFragment(citasFragment, it.title.toString())
                                drawerLayout.closeDrawer(GravityCompat.START)
                            }
                            R.id.nav_perfil -> {
                                replaceFragment(perfilFragment, it.title.toString())
                                drawerLayout.closeDrawer(GravityCompat.START)
                            }

                            R.id.nav_logout -> {
                                // Al presionar el botón cerrar sesión se deben eliminar los datos amacenados en Sharedpreferences
                                val sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)
                                sharedPreferences.edit().clear().apply()
                                val sharedPreferencesUsuario = getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
                                sharedPreferencesUsuario.edit().clear().apply()

                                finish()
                                val intent = Intent(this@MenuClienteR, FlashScreen::class.java)
                                startActivity(intent)
                            }
                        }
                        true
                    }

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .add(R.id.fragment_container, FragmentoListaMascotas())
//                .commit()
//        }


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

    override fun onBackPressed() {
        // Obtener el fragmento actual en la pila de fragmentos
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is FragmentoCitas) {
            // Reemplazar FragmentoB con FragmentoA en la pila de fragmentos al manejar el evento de retroceso
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentoListaMascotas())
                .commit()
        }else if (currentFragment is FragmentoPerfilUsuario){
            // Reemplazar FragmentoB con FragmentoA en la pila de fragmentos al manejar el evento de retroceso
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentoListaMascotas())
                .commit()
        }
        else {
            // Manejar el evento de retroceso predeterminado si no estamos en FragmentoB
            super.onBackPressed()
        }
    }

    private fun replaceFragment(fragment: Fragment, title : String){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
        setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
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

    fun obtenerDatos(email: String){

        val sharedPreferencesLogin = getSharedPreferences("login", Context.MODE_PRIVATE)
        val idGuardado = sharedPreferencesLogin.getInt("ip", 0)

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerIdUsuario(email)
            runOnUiThread{
                if(call.isSuccessful){
                    usuario = call.body()!!
                    val navigationView = findViewById<NavigationView>(R.id.navigation_view)
                    val headerView = navigationView.getHeaderView(0)
                    val nameTextView = headerView.findViewById<TextView>(R.id.tvUserName)
                    val emailTextView = headerView.findViewById<TextView>(R.id.tvUserEmail)
                    val imgTv = headerView.findViewById<ImageView>(R.id.ivUserName)

                    sharedPreferencesLogin.edit().putInt("ip",usuario.id_usuario).apply()

                    if(usuario.foto_usuario!="0"){
                        val uniqueId = System.currentTimeMillis().toString()
                        Glide.with(this@MenuClienteR).load(usuario.foto_usuario).signature(ObjectKey(uniqueId)).into(imgTv)
                    }

                    nameTextView.text = usuario.nombre_usuario
                    emailTextView.text = usuario.email_usuario

                }else{
                    Toast.makeText(this@MenuClienteR,"Error al consultar usuario",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onNetworkAvailable() {
        TODO("Not yet implemented")
    }

    override fun onNetworkUnavailable() {
        TODO("Not yet implemented")
    }

}