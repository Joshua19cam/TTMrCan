package com.example.ttmrcan

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.ttmrcan.databinding.FragmentFragmentoClienteNRBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoClienteNR.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoClienteNR : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoClienteNRBinding
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
        this.binding = FragmentFragmentoClienteNRBinding.inflate(inflater, container, false)

        return binding.root
    }
    //Aqui se realiza toda la programacion del fragment

    lateinit var usuarioNR: Usuario
    private var backPressedTime = 0L
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Se manda llamar las variables guardadas en SharedPreferences guardadas anteriormente

        val sharedPreferencesLogin = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE)
        val correoGuardado = sharedPreferencesLogin.getString("correo", null)

        val emailUsuario = arguments?.getString("email_usuario_capcha")
        sharedPreferencesLogin.edit().putString("email",emailUsuario).apply()

        // Función que se encarga de llamar al metodo para obtener los datos del correo ingresado
        if(correoGuardado.isNullOrEmpty()){
            mostrarPerfilNR(emailUsuario.toString())
        }else{
            mostrarPerfilNR(correoGuardado.toString())
        }

        binding.btnCerrarSesion.setOnClickListener {
            // Al presionar el botón cerrar sesión se deben eliminar los datos amacenados en Sharedpreferences
            val sharedPreferences = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            val sharedPreferencesUsuario = requireContext().getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
            sharedPreferencesUsuario.edit().clear().apply()
            val fragmentoLogin = FragmentoLogin()
            val fragmentTransaction = requireFragmentManager().beginTransaction()
            fragmentTransaction.replace(R.id.frameContainerLogin, fragmentoLogin)
            fragmentTransaction.commit()

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

    }


    @SuppressLint("SetTextI18n")
    fun mostrarPerfilNR(email : String){
        val sharedPreferencesLogin = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE)

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerIdUsuario(email)
            activity?.runOnUiThread{
                if(call.isSuccessful){
                    usuarioNR = call.body()!!

                    sharedPreferencesLogin.edit().putInt("ip",usuarioNR.id_usuario).apply()

                    binding.textNombreCliente.setText(
                        "${usuarioNR.nombre_usuario} ${usuarioNR.apellido_usuario}"
                    )
                    binding.textViewInfoDatos.setText(
                        "Información personal:\n${usuarioNR.nombre_usuario} ${usuarioNR.apellido_usuario}\n${usuarioNR.telefono_usuario}\n${usuarioNR.email_usuario}")

                }else{
                    Toast.makeText(activity,"Error al consultar usuario",Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment FragmentoClienteNR.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoClienteNR().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}