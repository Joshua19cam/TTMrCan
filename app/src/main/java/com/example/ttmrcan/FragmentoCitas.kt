package com.example.ttmrcan

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ttmrcan.databinding.FragmentFragmentoCitasBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoCitas.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoCitas : Fragment(), CitasAdapter.OnItemClicked  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoCitasBinding
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
        this.binding = FragmentFragmentoCitasBinding.inflate(inflater, container, false)

        return binding.root
    }

    lateinit var adaptador: CitasAdapter
    var listaCitas = arrayListOf<Cita>()
    //var mascotaNueva = Mascota(-1,"","","","","","",0,"",0)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferencesUsuario = requireContext().getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        val valorUsuarioId = sharedPreferencesUsuario.getInt("id",2)

        binding.btnAbrirCitaVacunacion.setOnClickListener {
            // Aquí se abrirá el fragmento de vacunación sin pasar ningún valor específico
            val fragmentV = FragmentoAgendarVacunacion()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragmentV) // Reemplaza "fragmentContainer" con el ID del contenedor donde deseas mostrar el fragmento de vacunación
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.btnAbrirCitaMedica.setOnClickListener {
            // Aquí se abrirá el fragmento de vacunación sin pasar ningún valor específico
            val fragmentM = FragmentoAgendarMedica()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragmentM) // Reemplaza "fragmentContainer" con el ID del contenedor donde deseas mostrar el fragmento de vacunación
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.btnAbrirCitaEstetica.setOnClickListener {
            val fragmentE = FragmentoAgendarEstetica()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragmentE) // Reemplaza "fragmentContainer" con el ID del contenedor donde deseas mostrar el fragmento de vacunación
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.recyclerviewCitasProximas.layoutManager = LinearLayoutManager(activity)
        setupRecyclerView()

        obtenerCitasPendientes(valorUsuarioId)

        // Configurar el botón Atrás del teléfono para volver al FragmentoA
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Realizar la transacción de fragmento para ir al FragmentoA
            requireActivity().supportFragmentManager.popBackStack()


            binding.recyclerviewCitasProximas.layoutManager = LinearLayoutManager(requireContext())
            setupRecyclerView()
            obtenerCitasPendientes(valorUsuarioId)
        }
    }

    private fun obtenerCitasPendientes(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerCitas(id)
            requireActivity().runOnUiThread {
                if (call.isSuccessful) {
                    listaCitas = call.body()!!.listaCitas
                    //listaCitas = call.body()?.listaCitas ?: arrayListOf()
                    if (listaCitas.isEmpty()) {
                        // Muestra el mensaje "No tienes citas pendientes"
                        Toast.makeText(
                            requireContext(),
                            "No tienes citas pendientes",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        setupRecyclerView()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "ERROR CONSULTAR TODOS",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }



    fun setupRecyclerView() {
        adaptador = CitasAdapter(listaCitas)
        adaptador.setOnClick(this@FragmentoCitas)
        binding.recyclerviewCitasProximas.adapter = adaptador
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoCitas.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoCitas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun cancelarCita(id: Int) {
        mostrarDialogoCancelar(id)
    }

    private fun mostrarDialogoCancelar(id: Int){
        val dialogo = activity?.let { Dialog(it, R.style.CustomDialogStyle) }
        dialogo?.setContentView(R.layout.dialogo_cancelar)
        val btnSi = dialogo?.findViewById<Button>(R.id.buttonCSi)
        val btnNo = dialogo?.findViewById<Button>(R.id.buttonCNo)
        //lo del motivo que se la va a hacer
        dialogo?.setCancelable(true)
        dialogo?.show()
        btnSi?.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val call = RetrofitClient.webServ.cancelarCita(id)
                activity?.runOnUiThread{
                    if(call.isSuccessful){
                        Toast.makeText(activity,call.body().toString(),Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(activity,call.body().toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialogo?.cancel()

            val fragmentoCitas = FragmentoCitas()
            val fragmentTransaction = requireFragmentManager().beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragmentoCitas)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        btnNo?.setOnClickListener {
            dialogo?.cancel()
        }
    }
}