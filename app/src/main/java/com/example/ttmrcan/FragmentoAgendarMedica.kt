package com.example.ttmrcan

import android.R
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.viewbinding.ViewBindings
import com.example.ttmrcan.databinding.FragmentAgendarMedicaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoAgendarMedica.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoAgendarMedica : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentAgendarMedicaBinding

    private var listaMascotasId: List<String> = emptyList()
    private var listaMascotas: List<String> = emptyList()
    private var listaHorasOcupadas: List<String> = emptyList()
    private var listaHorasTotales = listOf("10:00:00", "10:30:00","11:00:00","11:30:00","12:00:00",
        "12:30:00","13:00:00","13:30:00","14:00:00","14:30:00","15:00:00","15:30:00","16:00:00","16:30:00",
        "17:00:00","17:30:00","18:00:00")
    private var horasDisponibles: List<String> = emptyList()

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
        binding = FragmentAgendarMedicaBinding.inflate(inflater, container, false)
        return binding.root
    }

    var cita = Cita(1,"","","","","",1,1,1,0)
    var horasOcupadas = HorasOcupadas(1,"","","","","",1,1,1,1)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferencesUsuario =
            requireContext().getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        val valorUsuarioId = sharedPreferencesUsuario.getInt("id", 2)
        this.cita.id_usuario = valorUsuarioId

        val tipoCita = arguments?.getString("tipo_cita")
        this.cita.tipo_cita = tipoCita.toString()

        if(cita.tipo_cita=="Medica"){
            val color = ContextCompat.getColorStateList(requireContext(), com.example.ttmrcan.R.color.cita_medica)
            binding.textViewTituloCM.backgroundTintList = color
        }else if(cita.tipo_cita=="Vacunacion"){
            val color = ContextCompat.getColorStateList(requireContext(), com.example.ttmrcan.R.color.cita_vacunacion)
            binding.textViewTituloCM.backgroundTintList = color
            binding.tvDescipcionCM.setText("Descripción de vacuna")
        }else if(cita.tipo_cita=="Estetica"){
            val color = ContextCompat.getColorStateList(requireContext(), com.example.ttmrcan.R.color.cita_estetica)
            binding.textViewTituloCM.backgroundTintList = color
        }


        obtenerMascotasUsuario(valorUsuarioId)

        binding.btnAgendarCitaMedica.setOnClickListener {
            // validar que todos lo campos esten llenos
            val isValido = validarCampos()
            if(isValido){
                agendarCitaMedica()
                val fragmentTransaction = requireFragmentManager().beginTransaction()
                fragmentTransaction.setCustomAnimations(
                    com.example.ttmrcan.R.anim.enter_rigth_to_left, // entrada para el fragmento que se está agregando
                    com.example.ttmrcan.R.anim.exit_left, // salida para el fragmento actual
                    com.example.ttmrcan.R.anim.enter_left_to_rigth, // entrada para el fragmento actualizado
                    com.example.ttmrcan.R.anim.exit_rigth // salida para el fragmento actualizado
                )
                fragmentTransaction.replace(com.example.ttmrcan.R.id.fragment_container, FragmentoCitas())
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }else{
                Toast.makeText(activity,"Alguno de los campos está vacío", Toast.LENGTH_SHORT).show()
            }

        }
        binding.editTextTextFecha.isFocusable = false
        binding.editTextTextFecha.isFocusableInTouchMode = false
        binding.editTextTextFecha.inputType = InputType.TYPE_NULL
        binding.editTextTextFecha.setOnClickListener{
            showDatePickerDialog()
        }

        binding.btnCancelarCitaMedica.setOnClickListener{
            requireActivity().onBackPressed()
        }

        binding.spinnerSMCM.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                cita.id_mascota = listaMascotasId[position].toInt()
                // Aquí puedes hacer algo con la posición seleccionada

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Acción cuando no se selecciona nada
                cita.id_mascota = listaMascotasId[0].toInt()
            }
        }

    }

    fun agendarCitaMedica() {

        if (listaMascotas.isNotEmpty()){
            this.cita.descripcion_cita = binding.etDescripcionCM.text.toString()
            this.cita.observaciones_cita = binding.editTextTextObservacionesCM.text.toString()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(binding.editTextTextFecha.text.toString())
            val fechaFormateada = sdf.format(date)

            this.cita.fecha_cita = fechaFormateada

            CoroutineScope(Dispatchers.IO).launch {
                val call = RetrofitClient.webServ.agregarCita(cita)
                activity?.runOnUiThread{
                    if (call.isSuccessful){
                        Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Toast.makeText(activity, "No puedes agendar si no tienes mascotas", Toast.LENGTH_SHORT).show()
        }

    }


    private fun obtenerHorasDisponibles(fecha : String) {

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha)
        val fechaFormateada = sdf.format(date)

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerHorasOcupadas(fechaFormateada)
            requireActivity().runOnUiThread {
                if (call.isSuccessful) {
                    listaHorasOcupadas = call.body()?.listaHorasOcupadas?.map { it.hora_cita } ?: emptyList()
                    if (listaHorasOcupadas.isNotEmpty()) {
                        //filto de las horas
                        horasDisponibles = listaHorasTotales.filter { !listaHorasOcupadas.contains(it) }

                        val formatoEntrada = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        val formatoSalida = SimpleDateFormat("h:mm a", Locale.getDefault())

                        val listaHorasFormateadas = mutableListOf<String>()
                        for (hora in horasDisponibles) {
                            val horaDate = formatoEntrada.parse(hora)
                            val horaFormateada = formatoSalida.format(horaDate)
                            listaHorasFormateadas.add(horaFormateada)
                        }

                        val horasAdapter = ArrayAdapter(
                            requireContext(),
                            R.layout.simple_spinner_item,
                            listaHorasFormateadas
                        )

                        horasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.hourSpinner.adapter = horasAdapter
                    } else {

                        val formatoEntrada = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        val formatoSalida = SimpleDateFormat("h:mm a", Locale.getDefault())

                        val listaHorasFormateadas = mutableListOf<String>()
                        for (hora in listaHorasTotales) {
                            val horaDate = formatoEntrada.parse(hora)
                            val horaFormateada = formatoSalida.format(horaDate)
                            listaHorasFormateadas.add(horaFormateada)
                        }
                        //En caso de que no haya horas ocupadas
                        val horasAdapter = ArrayAdapter(
                            requireContext(),
                            R.layout.simple_spinner_item,
                            listaHorasFormateadas
                        )

                        horasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.hourSpinner.adapter = horasAdapter
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Error al obtener las mascotas del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.hourSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(horasDisponibles.isNotEmpty()) {
                    cita.hora_cita = horasDisponibles[position]
                }else{
                    cita.hora_cita = listaHorasTotales[position]
                }
                // Aquí puedes guardar la selección en una variable o hacer lo que desees con ella
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Opcional: si deseas realizar alguna acción cuando no se selecciona nada
                if(horasDisponibles.isNotEmpty()) {
                    cita.hora_cita = horasDisponibles[0]
                }else{
                    cita.hora_cita = listaHorasTotales[0]
                }
            }
        }
    }

    fun obtenerMascotasUsuario(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerMascotasUsuario(id)
            requireActivity().runOnUiThread {
                if (call.isSuccessful) {
                    listaMascotas = call.body()?.listaMascotas?.map { it.nombre_mascota } ?: emptyList()
                    listaMascotasId = call.body()?.listaMascotas?.map { it.id_mascota.toString() } ?: emptyList()
                    if (listaMascotas.isNotEmpty()) {
                        val mascotaAdapter = ArrayAdapter(
                            requireContext(),
                            R.layout.simple_spinner_item,
                            listaMascotas
                        )

                        mascotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerSMCM.adapter = mascotaAdapter
                    } else {
                        Toast.makeText(activity, "No tienes mascotas aún", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Error al obtener las mascotas del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val locale = Locale("es")
        Locale.setDefault(locale)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedDate = String.format(locale,"%02d/%02d/%04d", dayOfMonth, month + 1, year)
            binding.editTextTextFecha.setText(selectedDate)
            obtenerHorasDisponibles(selectedDate)
        }, year, month, dayOfMonth)

        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }
    fun validarCampos(): Boolean{
        return !(binding.etDescripcionCM.text.isNullOrEmpty()||binding.editTextTextFecha.text.isEmpty())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAgendarMedica.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoAgendarMedica().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}