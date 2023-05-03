package com.example.ttmrcan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ttmrcan.databinding.FragmentFragmentoEditarMascotaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoEditarMascota.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoEditarMascota : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoEditarMascotaBinding
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
        this.binding = FragmentFragmentoEditarMascotaBinding.inflate(inflater, container, false)

        return binding.root
    }

    var mascota = Mascota(-1,"","","","",
         "","",0,"",-1)
    var isEditando = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idMascota = arguments?.getInt("id_mascota")
        mascota.id_mascota = idMascota!!
        val nombreMascota = arguments?.getString("nombre_mascota")
        val colorMascota = arguments?.getString("color_mascota")
        val razaMascota = arguments?.getString("raza_mascota")

        val fechaMascota = arguments?.getString("fecha_nacimiento_mascota")
        val fechaCortaMascota = fechaMascota?.substring(0,10)

        binding.editNombreMascotaE.setText(nombreMascota)
        binding.editColorMascotaE.setText(colorMascota)
        binding.editRazaMascotaE.setText(razaMascota)
        binding.editFechaMascotaE.setText(fechaCortaMascota)

        //Toast.makeText(activity,"ID: ${mascota.id_mascota}", Toast.LENGTH_LONG).show()

        binding.buttonGuardarEditar.setOnClickListener {
            val isValido = validarCampos()
            if(isValido){
                if(!isEditando){
                    actualizarMascota()
                }
            }
        }

        binding.buttonCancelarEditar.setOnClickListener {
            requireActivity().onBackPressed() //Esto se ocupa para regresar entre fragments ES UTIL
        }

    }
    fun actualizarMascota() {

        this.mascota.nombre_mascota = binding.editNombreMascotaE.text.toString()
        this.mascota.color_mascota = binding.editColorMascotaE.text.toString()
        this.mascota.raza_mascota = binding.editRazaMascotaE.text.toString()
        this.mascota.fecha_nacimiento_mascota = binding.editFechaMascotaE.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.actualizarMascota(mascota.id_mascota, mascota)
            activity?.runOnUiThread{
                if (call.isSuccessful){
                    Toast.makeText(activity,call.body().toString(),Toast.LENGTH_SHORT).show()

                    requireActivity().onBackPressed()

                }else{
                    Toast.makeText(activity,call.body().toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun validarCampos(): Boolean{
        return !(binding.editNombreMascotaE.text.isNullOrEmpty()||binding.editColorMascotaE.text.isNullOrEmpty()
                ||binding.editRazaMascotaE.text.isNullOrEmpty()||binding.editFechaMascotaE.text.isNullOrEmpty())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoEditarMascota.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoEditarMascota().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}