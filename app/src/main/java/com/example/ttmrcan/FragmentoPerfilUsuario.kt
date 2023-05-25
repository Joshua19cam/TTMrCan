package com.example.ttmrcan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import com.example.ttmrcan.databinding.FragmentFragmentoLoginBinding
import com.example.ttmrcan.databinding.FragmentFragmentoPerfilUsuarioBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoPerfilUsuario.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoPerfilUsuario : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoPerfilUsuarioBinding
    private lateinit var inflater: LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        // Permitir el manejo de eventos de retroceso en este fragmento
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Manejar el evento de retroceso llamando al método predeterminado de la actividad
            requireActivity().onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.inflater = inflater
        this.binding = FragmentFragmentoPerfilUsuarioBinding.inflate(inflater, container, false)

        return binding.root
    }

    lateinit var usuario: Usuario
    lateinit var consultas: TotalConsultas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO
        val sharedPreferencesUsuario = requireContext().getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        val valorObtenidoEmail = sharedPreferencesUsuario.getString("email","")
        val valorObtenidoId = sharedPreferencesUsuario.getInt("id",0)

        mostrarPerfilNR(valorObtenidoEmail.toString(),valorObtenidoId)

    }


    @SuppressLint("SetTextI18n")
    fun mostrarPerfilNR(email : String,id : Int){

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerIdUsuario(email)
            val call2 = RetrofitClient.webServ.obtenerConsultas(id)
            activity?.runOnUiThread{
                if(call.isSuccessful || call2.isSuccessful){
                    usuario = call.body()!!
                    consultas = call2.body()!!
                    binding.textViewInfoP.setText(
                        "${usuario.nombre_usuario} ${usuario.apellido_usuario}\n${usuario.telefono_usuario}\n${usuario.email_usuario}"
                    )
                    binding.textViewInfoCitas.setText("Citas estéticas: ${consultas.consulta_esteticas}\nCitas médicas: ${consultas.consulta_medicas}\nCitas vacunación: ${consultas.consulta_vacunacion}")
                }else{
                    Toast.makeText(activity,"Error al consultar usuario", Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment FragmentoPerfilUsuario.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoPerfilUsuario().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}