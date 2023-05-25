package com.example.ttmrcan

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ttmrcan.databinding.FragmentFragmentoHistorialBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoHistorial.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoHistorial : Fragment(), HistorialAdapter.OnItemClicked{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoHistorialBinding
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
        this.binding = FragmentFragmentoHistorialBinding.inflate(inflater, container, false)

        return binding.root
    }

    lateinit var adaptador: HistorialAdapter
    var listaHistorial = arrayListOf<Historial>()
    var historialNuevo = Historial(1,"","",
        1,"","","","",1,1,1)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idMascotaH = arguments?.getInt("id_mascota_HM")
        val tipoMascotaH = arguments?.getString("tipo_mascota_HM")

        binding.recyclerViewHistorial.layoutManager = LinearLayoutManager(activity)
        setupRecyclerView()

        obtenerHistorial(idMascotaH!!,tipoMascotaH!!)

    }

    fun obtenerHistorial(id: Int,tipo: String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerHistorial(id,tipo)
            activity?.runOnUiThread{
                if(call.isSuccessful){
                    listaHistorial = call.body()!!.listaHistorialM
                    setupRecyclerView()
                }else{
                    Toast.makeText(activity,"No hay un historial aún", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setupRecyclerView() {
        adaptador = HistorialAdapter(listaHistorial)
        adaptador.setOnClick(this@FragmentoHistorial)
        binding.recyclerViewHistorial.adapter = adaptador
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoHistorial.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoHistorial().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun masInfo(historial: Historial) {

        val dialogo = activity?.let { Dialog(it, R.style.CustomDialogStyle) }
        dialogo?.setContentView(R.layout.dialogo_detalles_historial)
        val peso = dialogo?.findViewById<TextView>(R.id.dialogo_peso_text)
        val descripcion = dialogo?.findViewById<TextView>(R.id.dialogo_descipcion_text)
        val observaciones = dialogo?.findViewById<TextView>(R.id.dialogo_observaciones_text)
        peso?.text = historial.peso_consulta.toString()
        descripcion?.text = historial.descripcion_consulta
        observaciones?.text = historial.observaciones_consulta
        dialogo?.setCancelable(true)
        dialogo?.show()
    }
}