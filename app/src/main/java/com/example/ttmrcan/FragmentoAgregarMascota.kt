package com.example.ttmrcan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ttmrcan.databinding.ActivityRegistroClienteBinding
import com.example.ttmrcan.databinding.FragmentFragmentoAgregarMascotaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoAgregarMascota.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoAgregarMascota : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoAgregarMascotaBinding
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
        this.binding = FragmentFragmentoAgregarMascotaBinding.inflate(inflater, container, false)

        return binding.root

    }


    var mascota = Mascota(1,"","","",
        "","","",0,"",-1)
    var usuarioID = 6
    var isEditando = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCancelar.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.buttonGuardar.setOnClickListener {
            var isValido = validarCampos()
            if(isValido){
                if(!isEditando){
                    agregarMascota()
                }
            }
        }
    }

    fun validarCampos(): Boolean{
        return !(binding.editNombreMascotaA.text.isNullOrEmpty()||binding.editSexoMascotaA.text.isNullOrEmpty()
                ||binding.editColorMascotaA.text.isNullOrEmpty()||binding.editRazaMascotaA.text.isNullOrEmpty()
                ||binding.editFechaMascotaA.text.isNullOrEmpty())
    }
    fun agregarMascota(){
        this.mascota.nombre_mascota = binding.editNombreMascotaA.text.toString()
        this.mascota.sexo_mascota = binding.editSexoMascotaA.text.toString()
        this.mascota.color_mascota = binding.editColorMascotaA.text.toString()
        this.mascota.raza_mascota = binding.editRazaMascotaA.text.toString()
        this.mascota.fecha_nacimiento_mascota = binding.editFechaMascotaA.text.toString()
        this.mascota.id_usuario = usuarioID

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.agregarMascota(mascota)
            activity?.runOnUiThread{
                if (call.isSuccessful){
                    Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                    limpiarObjeto()
                }else{
                    Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun limpiarCampos(){
        binding.editNombreMascotaA.setText("")
        binding.editSexoMascotaA.setText("")
        binding.editColorMascotaA.setText("")
        binding.editRazaMascotaA.setText("")
        binding.editFechaMascotaA.setText("")
    }

    fun limpiarObjeto(){
        this.mascota.id_mascota = -1
        this.mascota.nombre_mascota = ""
        this.mascota.color_mascota = ""
        this.mascota.raza_mascota = ""
        this.mascota.padecimientos_mascota = ""
        this.mascota.especie_mascota = ""
        this.mascota.fecha_nacimiento_mascota = ""
        this.mascota.sexo_mascota = ""
        this.mascota.id_usuario = -1
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoAgregarMascota.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoAgregarMascota().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}