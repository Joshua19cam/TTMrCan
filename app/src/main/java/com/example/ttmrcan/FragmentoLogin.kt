package com.example.ttmrcan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.ttmrcan.databinding.FragmentFragmentoLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoLogin.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoLogin : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoLoginBinding
    private lateinit var inflater: LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.inflater = inflater
        this.binding = FragmentFragmentoLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    private var backPressedTime = 0L
    var token = Token(-1)
    lateinit var usuario: Usuario
    //val sharedPreferencesLogin = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferencesLogin = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE)
        val correoGuardado = sharedPreferencesLogin.getString("correo", null)

        if (correoGuardado != null) {
            // Los datos del usuario ya se han guardado. Inicia automáticamente la siguiente pantalla de la aplicación.
            corroborarEstaus(correoGuardado)
        } else {
            // Los datos del usuario no se han guardado. Muestra la pantalla de login para que el usuario inicie sesión.

            binding.btnIngresar.setOnClickListener {
                val correo = binding.editTextCorreoLogIn.text.toString()
                val contrasena = binding.editTextPasswordLogIn.text.toString()

                if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val call = RetrofitClient.webServ.checarLogin(correo,contrasena)
                        activity?.runOnUiThread{
                            if (call.isSuccessful){
                                token=call.body()!!
                                if (token.existe_usuario==1){
                                    // Los datos de inicio de sesión son correctos. Guarda los datos del usuario en SharedPreferences y pasa a la siguiente pantalla.
                                    val editor = sharedPreferencesLogin.edit()
                                    editor.putString("correo", correo)
                                    editor.apply()
                                    //TODO cuando si existe el usuario, siguiente pantalla
                                    corroborarEstaus(correo)
                                }else{
                                    Toast.makeText(activity,"Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                                }

                            }else{
                                Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(activity, "Por favor ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Esto es para que se salga de la app estando en el fragment login
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime < 2000) {
                    requireActivity().finish()
                } else {
                    backPressedTime = currentTime
                    Toast.makeText(requireContext(), "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show()
                }
            }
        })



// ESTOS SON LOS LISTENER DE LOS BOTONES

        binding.tvOlvidasteContraseA.setOnClickListener{
            val fragmentoRecuperarC = FragmentoRecuperarContrasena()
            val fragmentTransaction = requireFragmentManager().beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.enter_rigth_to_left, // entrada para el fragmento que se está agregando
                R.anim.exit_left, // salida para el fragmento actual
                R.anim.enter_left_to_rigth, // entrada para el fragmento actualizado
                R.anim.exit_rigth // salida para el fragmento actualizado
            )
            fragmentTransaction.replace(R.id.frameContainerLogin, fragmentoRecuperarC)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.tvRegistrarse.setOnClickListener {
            val fragmentoRegistroCliente = FragmentoRegistroCliente()
            val fragmentTransaction = requireFragmentManager().beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.enter_rigth_to_left, // entrada para el fragmento que se está agregando
                R.anim.exit_left, // salida para el fragmento actual
                R.anim.enter_left_to_rigth, // entrada para el fragmento actualizado
                R.anim.exit_rigth // salida para el fragmento actualizado
            )
            fragmentTransaction.replace(R.id.frameContainerLogin, fragmentoRegistroCliente)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
            /*val intent = Intent(activity, RegistroCliente::class.java)
            startActivity(intent)*/
        }

    }

    fun corroborarEstaus(email : String){

        val sharedPreferencesUsuario = requireActivity().getSharedPreferences("idUsuario", Context.MODE_PRIVATE)

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerIdUsuario(email)
            activity?.runOnUiThread{
                if (call.isSuccessful){
                    usuario=call.body()!!
                    if (usuario.estatus_usuario!=2){
                        //Lo manda al perfil que NO esta dado de alta
                        val fragmentoClienteNR = FragmentoClienteNR()
                        // Almacena El id y el email del usuario en sharedPreferences para utilizarlos en los demas fragments
                        sharedPreferencesUsuario.edit().putString("email",usuario.email_usuario).apply()
                        // Cambio del fragment Login al fragment ClienteNR cuando no está dado de alta en el sistema
                        val fragmentTransaction = requireFragmentManager().beginTransaction()
                        fragmentTransaction.setCustomAnimations(
                            R.anim.enter_rigth_to_left, // entrada para el fragmento que se está agregando
                            R.anim.exit_left, // salida para el fragmento actual
                            R.anim.enter_left_to_rigth, // entrada para el fragmento actualizado
                            R.anim.exit_rigth // salida para el fragmento actualizado
                        )
                        fragmentTransaction.replace(R.id.frameContainerLogin, fragmentoClienteNR)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                        // Se limpian los edit text
                        /*binding.editTextCorreoLogIn.setText("")
                        binding.editTextPasswordLogIn.setText("")*/


                    }else {
                        //lo manda al perfil que esta dado de alta --Lo de pala
                        sharedPreferencesUsuario.edit().putInt("id",usuario.id_usuario).apply()
                        sharedPreferencesUsuario.edit().putString("email",usuario.email_usuario).apply()
                        sharedPreferencesUsuario.edit().putString("nombreCompleto","${usuario.nombre_usuario} ${usuario.apellido_usuario}").apply()
                        sharedPreferencesUsuario.edit().putString("img",usuario.foto_usuario).apply()
                        // Cambio del fragment Login a la activity MenuCliente cuando ya está dado de alta en el sistema
                        val intent = Intent(activity, MenuClienteR::class.java)
                        startActivity(intent)
                        // Se limpian los edit text
                        /*binding.editTextCorreoLogIn.setText("")
                        binding.editTextPasswordLogIn.setText("")*/
                    }

                }else{
                    Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoLogin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoLogin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}