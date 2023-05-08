package com.example.ttmrcan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE)
        val emailUsuario = arguments?.getString("emailUsuario")
        val idUsuario = arguments?.getString("idUsuario")

        mostrarPerfilNR(emailUsuario.toString())

        // Agregar un listener al botón "Cerrar sesión"
        binding.btnCerrarSesion.setOnClickListener {
            // Eliminar los datos guardados en SharedPreferences
            val editor = sharedPreferences.edit()
            editor.remove("correo")
            editor.remove("contraseña")
            editor.apply()

            // Abrir la pantalla de inicio de sesión

            val fragmentoLogin = FragmentoLogin()

            val fragmentTransaction = requireFragmentManager().beginTransaction()
            fragmentTransaction.replace(R.id.frameContainerLogin, fragmentoLogin)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }

        binding.btnVerMascotas.setOnClickListener {

            val intent = Intent(activity, MisMascotas::class.java)
            intent.putExtra("idUsuario",idUsuario)
            startActivity(intent)
        }

    }

    fun mostrarPerfilNR(email : String){

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerIdUsuario(email)
            activity?.runOnUiThread{
                if(call.isSuccessful){
                    usuarioNR = call.body()!!
                    binding.textNombreCliente.setText("${usuarioNR.nombre_usuario} ${usuarioNR.apellido_usuario}")
                    binding.textViewInfoDatos.setText("La Informacionde la mascota es: \n ${usuarioNR.id_usuario} " +
                            "\n ${usuarioNR.nombre_usuario} \n ${usuarioNR.apellido_usuario}")
                    Toast.makeText(activity,"Ya jala",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(activity,"ERROR CONSULTAR TODOS",Toast.LENGTH_SHORT).show()
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