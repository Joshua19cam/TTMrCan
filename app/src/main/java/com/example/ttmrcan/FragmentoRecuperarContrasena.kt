package com.example.ttmrcan

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.ttmrcan.databinding.FragmentFragmentoRecuperarContrasenaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoRecuperarContrasena.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoRecuperarContrasena : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoRecuperarContrasenaBinding
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
        this.binding = FragmentFragmentoRecuperarContrasenaBinding.inflate(inflater, container, false)

        return binding.root
    }
     var correoDestino = CorreoDestino("")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO
        binding.btnEnviarP.setOnClickListener {
            val correoOP = binding.editTextCorreoOP.text.toString()
            correoDestino.email_usuario = correoOP

            if (correoOP.isNotEmpty()) {
                if (validarCorreoElectronico(binding.editTextCorreoOP.text.toString())) {
                    // El correo electrónico es válido
                    CoroutineScope(Dispatchers.IO).launch {
                        val call = RetrofitClient.webServ.enviarCorreo(correoDestino)
                        activity?.runOnUiThread{
                            if (call.isSuccessful){
                                mostrarDialogo()
                                requireActivity().onBackPressed()
                                //Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                } else {
                    // El correo electrónico no es válido, mostrar un Toast
                    Toast.makeText(activity, "Dirección de correo electrónico no válida", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(activity,"Aun no se ingresa ningun correo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun validarCorreoElectronico(texto: String): Boolean {
        val patronCorreoElectronico: Pattern = Patterns.EMAIL_ADDRESS
        return patronCorreoElectronico.matcher(texto).matches()
    }

    private fun mostrarDialogo() {
        val dialogo = activity?.let { Dialog(it, R.style.CustomDialogStyle) }
        dialogo?.setContentView(R.layout.dialogo_cambio_exitoso)
        val titulo = dialogo?.findViewById<TextView>(R.id.dialogo_correcto)
        titulo?.text = "Mensaje enviado. Por favor, revise su bandeja de entrada."
        dialogo?.setCancelable(true)
        dialogo?.show()
        Handler().postDelayed({
            dialogo?.dismiss()
        }, 3000)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoRecuperarContrasena.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoRecuperarContrasena().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}