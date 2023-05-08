package com.example.ttmrcan

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ttmrcan.databinding.FragmentFragmentoAgregarMascotaBinding
import com.example.ttmrcan.databinding.FragmentFragmentoEditarMascotaBinding
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
 * Use the [FragmentoListaMascotas.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoListaMascotas : Fragment(), MascotaAdapter.OnItemClicked{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoListaMascotasBinding
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
        this.binding = FragmentFragmentoListaMascotasBinding.inflate(inflater, container, false)

        return binding.root
    }

    lateinit var adaptador: MascotaAdapter
    var listaMascotas = arrayListOf<Mascota>()
    lateinit var mascotaNueva : Mascota

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentoAgregarMascota = FragmentoAgregarMascota()

        val idUsuario = arguments?.getString("recivId")

        binding.recyclerViewMascotas.layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewMascotas.layoutManager = GridLayoutManager(activity,2)
        setupRecyclerView()

        obtenerMascotas(idUsuario.toString().toInt())

        binding.buttonAgregarMascota.setOnClickListener {
            val fragmentTransaction = requireFragmentManager().beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.enter_rigth_to_left, // entrada para el fragmento que se está agregando
                R.anim.exit_left, // salida para el fragmento actual
                R.anim.enter_left_to_rigth, // entrada para el fragmento actualizado
                R.anim.exit_rigth // salida para el fragmento actualizado
            )
            fragmentTransaction.replace(R.id.frameContainerMisMascotas, fragmentoAgregarMascota)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    fun obtenerMascotas(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerMascotasUsuario(id) //estoy usando de ejemplo el 6
            activity?.runOnUiThread{
                if(call.isSuccessful && call.body() != null ){
                    listaMascotas = call.body()!!.listaMascotas
                    setupRecyclerView()
                }else{
                    Toast.makeText(activity,"No tienes mascotas aún",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setupRecyclerView() {
        adaptador = MascotaAdapter(listaMascotas)
        adaptador.setOnClick(this@FragmentoListaMascotas)
        binding.recyclerViewMascotas.adapter = adaptador
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoListaMascotas.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoListaMascotas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun editarMascota(mascota: Mascota) {

        val fragmentoEditarMascota = FragmentoEditarMascota()
        val args = Bundle()

        args.putInt("id_mascota", mascota.id_mascota)
        args.putString("nombre_mascota", mascota.nombre_mascota)
        args.putString("color_mascota", mascota.color_mascota)
        args.putString("raza_mascota", mascota.raza_mascota)
        args.putString("fecha_nacimiento_mascota", mascota.fecha_nacimiento_mascota)

        fragmentoEditarMascota.arguments = args

        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.replace(R.id.frameContainerMisMascotas, fragmentoEditarMascota)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun borrarMascota(id: Int) {
        //agregar lo de dar de baja
    }

    override fun verPerfil(mascota: Mascota) {
        val fragmentoVerPerfil = FragmentoPerfilMascota()
        val args = Bundle()

        args.putInt("id_mascota", mascota.id_mascota)
        args.putString("nombre_mascota", mascota.nombre_mascota)
        args.putString("color_mascota", mascota.color_mascota)
        args.putString("raza_mascota", mascota.raza_mascota)
        args.putString("sexo_mascota",mascota.sexo_mascota)
        args.putString("padecimientos_mascota",mascota.padecimientos_mascota)
        args.putString("fecha_nacimiento_mascota", mascota.fecha_nacimiento_mascota)

        fragmentoVerPerfil.arguments = args

        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.replace(R.id.frameContainerMisMascotas, fragmentoVerPerfil)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}