package com.example.ttmrcan

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ttmrcan.databinding.FragmentFragmentoCapchaBinding
import com.example.ttmrcan.databinding.FragmentFragmentoListaMascotasBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoCapcha.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoCapcha : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoCapchaBinding
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
        this.binding = FragmentFragmentoCapchaBinding.inflate(inflater, container, false)

        return binding.root
    }

    var usuario = Usuario(-1,"", "","","",""
        ,"","",-1,"","","",0,
        0,"",1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nombreUsuario = arguments?.getString("nombre_usuario")
        usuario.nombre_usuario = nombreUsuario.toString()
        val apellidoUsuario = arguments?.getString("apellido_usuario")
        usuario.apellido_usuario = apellidoUsuario.toString()
        val telefonoUsuario = arguments?.getString("telefono_usuario")
        usuario.telefono_usuario = telefonoUsuario.toString()
        val emailUsuario = arguments?.getString("email_usuario")
        usuario.email_usuario = emailUsuario.toString()
        val estadoUsuario = arguments?.getString("estado_usuario")
        usuario.estado_usuario = estadoUsuario.toString()
        val ciudadUsuario = arguments?.getString("ciudad_usuario")
        usuario.ciudad_usuario = ciudadUsuario.toString()
        val coloniaUsuario = arguments?.getString("colonia_usuario")
        usuario.colonia_usuario = coloniaUsuario.toString()
        val cpUsuario = arguments?.getInt("cp_usuario")
        usuario.cp_usuario = cpUsuario.toString().toInt()
        val calleUsuario = arguments?.getString("calle_usuario")
        usuario.calle_usuario = calleUsuario.toString()
        val numExUsuario = arguments?.getString("num_ext_usuario")
        usuario.num_ext_usuario = numExUsuario.toString()
        val passwordUsuario = arguments?.getString("password_usuario")
        usuario.password_usuario = passwordUsuario.toString()


        val fragmentoPerfilNR = FragmentoClienteNR()

        binding.checkboxCapcha.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Acción a realizar cuando se marca el checkbox
                agregarUsuario()

                Handler().postDelayed({
                    // Acción a realizar después de x segundos
                    val args = Bundle()

                    args.putString("email_usuario_capcha", usuario.email_usuario)

                    fragmentoPerfilNR.arguments = args
                    val fragmentTransaction = requireFragmentManager().beginTransaction()
                    fragmentTransaction.replace(R.id.frameContainerLogin, fragmentoPerfilNR)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()

                }, 2000)
            }
        }
    }

    fun agregarUsuario(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.agregarUsuario(usuario)
            activity?.runOnUiThread{
                if (call.isSuccessful){
                    //Toast.makeText(this@RegistroCliente,call.body().toString(), Toast.LENGTH_SHORT).show()
                    mostrarDialogo()
                }else{
                    Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogo() {
        val dialogo = activity?.let { Dialog(it, R.style.CustomDialogStyle) }
        dialogo?.setContentView(R.layout.dialogo_cambio_exitoso)
        val titulo = dialogo?.findViewById<TextView>(R.id.dialogo_correcto)
        titulo?.text = "Se ha registrado exitosamente"
        dialogo?.setCancelable(true)
        dialogo?.show()
        Handler().postDelayed({
            dialogo?.dismiss()
        }, 4000)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoCapcha.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoCapcha().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}