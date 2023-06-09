package com.example.ttmrcan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.ttmrcan.databinding.FragmentFragmentoListaMascotasBinding
import com.example.ttmrcan.databinding.FragmentFragmentoPerfilMascotaBinding
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoPerfilMascota.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoPerfilMascota : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoPerfilMascotaBinding
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
        this.binding = FragmentFragmentoPerfilMascotaBinding.inflate(inflater, container, false)

        return binding.root
    }

    var mascota = Mascota(-1,"","","","",
        "","","",0,"",-1)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO
        val idMascota = arguments?.getInt("id_mascota")
        mascota.id_mascota = idMascota!!
        val nombreMascota = arguments?.getString("nombre_mascota")
        val colorMascota = arguments?.getString("color_mascota")
        val razaMascota = arguments?.getString("raza_mascota")
        val fechaMascota = arguments?.getString("fecha_nacimiento_mascota")

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(fechaMascota)
        val fechaFormateada = sdf.format(date)

        val fotoMascota = arguments?.getString("foto_mascota")
        val sexoMascota = arguments?.getString("sexo_mascota")
        val padecimientosMascota = arguments?.getString("padecimientos_mascota")

        if (fotoMascota!!.isNotEmpty()) {
            val uniqueId = System.currentTimeMillis().toString()
            Glide.with(this).load(fotoMascota).signature(ObjectKey(uniqueId)).into(binding.shapeableImageViewFotoMascota)
        }


        binding.tvNombrePerfil.setText(nombreMascota)
        binding.textViewInfo.setText("Nacimiento: ${fechaFormateada}\n\nRaza: " +
                "${razaMascota}\n\nPadecimientos: ${padecimientosMascota}\n\nSexo: ${sexoMascota}")

        mostrarHistorialMedico()

        binding.btnHistorialMedico.setOnClickListener {
            mostrarHistorialMedico()
        }
        binding.btnHistorialVacunacion.setOnClickListener {
            mostrarHistorialVacunacion()
        }
    }

    fun mostrarHistorialMedico(){
        val fragmentoHistorial = FragmentoHistorial()
        val fragmentTransaction = requireFragmentManager().beginTransaction()

        val args = Bundle()

        args.putInt("id_mascota_HM", mascota.id_mascota)
        args.putString("tipo_mascota_HM", "Medica")

        fragmentoHistorial.arguments = args
        fragmentTransaction.replace(R.id.frameContainerHistorial, fragmentoHistorial)
        fragmentTransaction.commit()
    }
    fun mostrarHistorialVacunacion(){
        val fragmentoHistorial = FragmentoHistorial()
        val fragmentTransaction = requireFragmentManager().beginTransaction()

        val args = Bundle()

        args.putInt("id_mascota_HM", mascota.id_mascota)
        args.putString("tipo_mascota_HM", "Vacunacion")

        fragmentoHistorial.arguments = args
        fragmentTransaction.replace(R.id.frameContainerHistorial, fragmentoHistorial)
        fragmentTransaction.commit()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoPerfilMascota.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoPerfilMascota().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}