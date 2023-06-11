package com.example.ttmrcan

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.ttmrcan.databinding.FragmentFragmentoLoginBinding
import com.example.ttmrcan.databinding.FragmentFragmentoRegistroClienteBinding
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
 * Use the [FragmentoRegistroCliente.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoRegistroCliente : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoRegistroClienteBinding
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
        this.binding = FragmentFragmentoRegistroClienteBinding.inflate(inflater, container, false)

        return binding.root
    }

    var usuario = Usuario(-1,"", "","","",""
        ,"","",-1,"","","",0,
        0,"",1)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Lo asociamos al componente en el archivo .XML
        binding.buttonRegistrar.setOnClickListener {
            camposVacios()
        }

    }

    fun camposVacios(){
        if(binding.editNombre.text.isNullOrEmpty()){
            showToast("El campo 'Nombre' está vacío")
            return
        }
        if(binding.editApellido.text.isNullOrEmpty()){
            showToast("El campo 'Apellido' está vacío")
            return
        }
        if(binding.editTelefono.text.isNullOrEmpty()){
            showToast("El campo 'Teléfono' está vacío")
            return
        }
        if(binding.editCorreo.text.isNullOrEmpty()){
            showToast("El campo 'Correo Electrónico' está vacío")
            return
        }
        //Para que tenga bien la estructura email
        if (!validarCorreoElectronico(binding.editCorreo.text.toString())) {
            // El correo electrónico es válido
            showToast("Dirección de correo electrónico no válida")
            return
        }
        if(binding.editEstado.text.isNullOrEmpty()){
            showToast("El campo 'Estado' está vacío")
            return
        }
        if(binding.editCiudad.text.isNullOrEmpty()){
            showToast("El campo 'Ciudad' está vacío")
            return
        }
        if(binding.editColonia.text.isNullOrEmpty()){
            showToast("El campo 'Colonia' está vacío")
            return
        }
        if(binding.editCP.text.isNullOrEmpty()){
            showToast("El campo 'Código postal' está vacío")
            return
        }
        if(binding.editCalle.text.isNullOrEmpty()){
            showToast("El campo 'Calle' está vacío")
            return
        }
        if(binding.editNumE.text.isNullOrEmpty()){
            showToast("El campo 'Número exterior' está vacío")
            return
        }
        if(binding.editContra.text.isNullOrEmpty()){
            showToast("El campo 'Contraseña' está vacío")
            return
        }
        if(binding.editConfContra.text.isNullOrEmpty()){
            showToast("El campo 'Confirmar contraseña' está vacío")
            return
        }
        if(binding.editConfContra.text.toString() != binding.editContra.text.toString()){
            showToast("Las contraseñas ingresadas no coinciden ")
            return
        }

        //accion
        //agregarUsuario()

        val fragmentoCapcha = FragmentoCapcha()

        val args = Bundle()

        args.putString("nombre_usuario", binding.editNombre.text.toString())
        args.putString("apellido_usuario", binding.editApellido.text.toString())
        args.putString("telefono_usuario", binding.editTelefono.text.toString())
        args.putString("email_usuario", binding.editCorreo.text.toString())
        args.putString("estado_usuario", binding.editEstado.text.toString())
        args.putString("ciudad_usuario", binding.editCiudad.text.toString())
        args.putString("colonia_usuario", binding.editColonia.text.toString())
        args.putInt("cp_usuario", binding.editCP.text.toString().toInt())
        args.putString("calle_usuario", binding.editCalle.text.toString())
        args.putString("num_ext_usuario", binding.editNumE.text.toString())
        args.putString("password_usuario", binding.editContra.text.toString())

        fragmentoCapcha.arguments = args

        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_rigth_to_left, // entrada para el fragmento que se está agregando
            R.anim.exit_left, // salida para el fragmento actual
            R.anim.enter_left_to_rigth, // entrada para el fragmento actualizado
            R.anim.exit_rigth // salida para el fragmento actualizado
        )
        fragmentTransaction.replace(R.id.frameContainerLogin, fragmentoCapcha)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun validarCorreoElectronico(texto: String): Boolean {
        val patronCorreoElectronico: Pattern = Patterns.EMAIL_ADDRESS
        return patronCorreoElectronico.matcher(texto).matches()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoRegistroCliente.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoRegistroCliente().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}