package com.example.ttmrcan

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ttmrcan.databinding.FragmentFragmentoCitasBinding
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
 * Use the [FragmentoCitas.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoCitas : Fragment(), CitasAdapter.OnItemClicked  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoCitasBinding
    private lateinit var inflater: LayoutInflater

    private lateinit var pendingIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager

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
    var nuevaLista = arrayListOf<String>()
    //var mascotaNueva = Mascota(-1,"","","","","","",0,"",0)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferencesLogin = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE)
        val idGuardado = sharedPreferencesLogin.getInt("ip", 0)

        createChannel()
        obtenerCitasPendientes(idGuardado)

        binding.btnAbrirCitaVacunacion.setOnClickListener {
            // Aquí se abrirá el fragmento de vacunación sin pasar ningún valor específico
            irCitaVacuancion()
        }

        binding.btnAbrirCitaMedica.setOnClickListener {
            // Aquí se abrirá el fragmento de vacunación sin pasar ningún valor específico
            irCitaMedica()
        }

        binding.btnAbrirCitaEstetica.setOnClickListener {
            irCitaEstetica()
        }

        binding.recyclerviewCitasProximas.layoutManager = LinearLayoutManager(activity)
        setupRecyclerView()



        // Configurar el botón Atrás del teléfono para volver al FragmentoA
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Realizar la transacción de fragmento para ir al FragmentoA
            requireActivity().supportFragmentManager.popBackStack()


            binding.recyclerviewCitasProximas.layoutManager = LinearLayoutManager(requireContext())
            setupRecyclerView()
            obtenerCitasPendientes(idGuardado)
        }

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun obtenerCitasPendientes(id: Int) {

        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val citasProcesadas = sharedPreferences.getStringSet("citasProcesadas", mutableSetOf())?.toMutableSet()
            ?: mutableSetOf()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.obtenerCitas(id)
            activity?.runOnUiThread {
                if(call.isSuccessful) {
                    listaCitas = call.body()!!.listaCitas
                    //listaCitas = call.body()?.listaCitas ?: arrayListOf()
                    if (listaCitas.isEmpty()) {
                        // Muestra el mensaje "No tienes citas pendientes"
                        //Toast.makeText(requireContext(),"No tienes citas pendientes",Toast.LENGTH_SHORT).show()
                    } else {
                        setupRecyclerView()
                        generarCitas(listaCitas, citasProcesadas, sharedPreferences)
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
        adaptador = CitasAdapter(this,listaCitas)
        adaptador.setOnClick(this@FragmentoCitas)
        binding.recyclerviewCitasProximas.adapter = adaptador
    }

    companion object {

        const val MY_CHANNEL_ID = "myChannel"
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
    fun irCitaMedica(){
        val fragmentM = FragmentoAgendarMedica()

        val args = Bundle()

        args.putString("tipo_cita", "Medica")

        fragmentM.arguments = args

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_rigth_to_left, // entrada para el fragmento que se está agregando
            R.anim.exit_left, // salida para el fragmento actual
            R.anim.enter_left_to_rigth, // entrada para el fragmento actualizado
            R.anim.exit_rigth // salida para el fragmento actualizado
        )
        fragmentTransaction.replace(R.id.fragment_container, fragmentM) // Reemplaza "fragmentContainer" con el ID del contenedor donde deseas mostrar el fragmento de vacunación
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    fun irCitaVacuancion(){
        val fragmentM = FragmentoAgendarMedica()

        val args = Bundle()

        args.putString("tipo_cita", "Vacunacion")

        fragmentM.arguments = args

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_rigth_to_left, // entrada para el fragmento que se está agregando
            R.anim.exit_left, // salida para el fragmento actual
            R.anim.enter_left_to_rigth, // entrada para el fragmento actualizado
            R.anim.exit_rigth // salida para el fragmento actualizado
        )
        fragmentTransaction.replace(R.id.fragment_container, fragmentM) // Reemplaza "fragmentContainer" con el ID del contenedor donde deseas mostrar el fragmento de vacunación
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    fun irCitaEstetica(){
        val fragmentM = FragmentoAgendarMedica()

        val args = Bundle()

        args.putString("tipo_cita", "Estetica")

        fragmentM.arguments = args

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_rigth_to_left, // entrada para el fragmento que se está agregando
            R.anim.exit_left, // salida para el fragmento actual
            R.anim.enter_left_to_rigth, // entrada para el fragmento actualizado
            R.anim.exit_rigth // salida para el fragmento actualizado
        )
        fragmentTransaction.replace(R.id.fragment_container, fragmentM) // Reemplaza "fragmentContainer" con el ID del contenedor donde deseas mostrar el fragmento de vacunación
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun cancelarCita(id: Int) {
        mostrarDialogoCancelar(id)
        cancelNotification(id)
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

    private fun generarCitas(listaCitas: ArrayList<Cita>, citasProcesadas: MutableSet<String>, sharedPreferences: SharedPreferences) {
        val citasProcesadasCopy = citasProcesadas.toMutableSet()

        for (cita in listaCitas) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(cita.fecha_cita)
            val fechaFormateada = sdf.format(date)

            val fecha = fechaFormateada
            val hora = cita.hora_cita
            val id = cita.id_cita
            val tipo = cita.tipo_cita

            if (!citasProcesadasCopy.contains(id.toString())) {
                scheduleNotification(fecha, hora,tipo, id)
                citasProcesadasCopy.add(id.toString())
            }
        }

        citasProcesadasCopy.retainAll(listaCitas.map { it.id_cita.toString() })

        sharedPreferences.edit().putStringSet("citasProcesadas", citasProcesadasCopy).apply()
    }

    private fun scheduleNotification(date: String,hour: String,tipo: String,notificationId: Int) {

        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetDateTime = dateTimeFormat.parse("$date $hour") // Parsear la fecha y hora especificadas

        val calendar = Calendar.getInstance()
        calendar.time = targetDateTime
        calendar.add(Calendar.HOUR_OF_DAY, -24) // Restar una 24 hora

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
        val fechaFormateada = sdf.format(date)

        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val hora = inputFormat.parse(hour)
        val horaFormateada = outputFormat.format(hora)

        val notificationIntent = Intent(requireContext(), AlarmNotification::class.java)
        notificationIntent.putExtra("titulo", "Recordatorio de tu cita: ${tipo}") // Ejemplo de título
        notificationIntent.putExtra("subtitulo", "Cita el ${fechaFormateada} a las ${horaFormateada}") // Ejemplo de subtítulo
        notificationIntent.putExtra("id", notificationId) // Ejemplo de id

        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationId,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager?.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

    }

    private fun cancelNotification(notificationId: Int) {
        val notificationIntent = Intent(requireContext(), AlarmNotification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationId,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager?.cancel(pendingIntent)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                MY_CHANNEL_ID,
                "MySuperChannel",
                importancia
            ).apply {
                description = "CITA DE LA MASCOTA"
                enableVibration(true) // Opcionalmente, puedes desactivar la vibración de la notificación
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC // Para que se muestre en la pantalla de bloqueo
            }

            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

}