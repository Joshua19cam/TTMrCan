package com.example.ttmrcan

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isEmpty
import com.example.ttmrcan.databinding.ActivityRegistroClienteBinding
import com.example.ttmrcan.databinding.FragmentFragmentoAgregarMascotaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.LocalTime

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

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImage: Bitmap? = null

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
        "","","","",0,"",-1)
    //var usuarioID = 6
    var isEditando = false
    var imageMascota64 = ""
    lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val sharedPreferencesUsuario = requireContext().getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        val valorUsuarioId = sharedPreferencesUsuario.getInt("id",2)

        binding.buttonCancelar.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.buttonGuardar.setOnClickListener {
            camposVacios(valorUsuarioId)
        }

        //Spinner de sexo para la mascota

        val genderOptions = arrayOf("Macho", "Hembra")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.sexoSpinner.adapter = adapter

        binding.sexoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mascota.sexo_mascota = genderOptions[position]
                // Aquí puedes guardar la selección en una variable o hacer lo que desees con ella
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Opcional: si deseas realizar alguna acción cuando no se selecciona nada
                mascota.sexo_mascota = genderOptions[0]
            }
        }
        binding.editFechaMascotaA.isFocusable = false
        binding.editFechaMascotaA.isFocusableInTouchMode = false
        binding.editFechaMascotaA.inputType = InputType.TYPE_NULL
        binding.editFechaMascotaA.setOnClickListener{
            showDatePickerDialog()
        }

        binding.imagenMascota.setOnClickListener {
            openGallery()
        }

    }

    fun camposVacios(idUsuario: Int){
        if(binding.editNombreMascotaA.text.isNullOrEmpty()){
            showToast("El campo 'Nombre de la mascota' está vacío")
            return
        }
        if(binding.editColorMascotaA.text.isNullOrEmpty()){
            showToast("El campo 'Color' está vacío")
            return
        }
        if(binding.editRazaMascotaA.text.isNullOrEmpty()){
            showToast("El campo 'Raza' está vacío")
            return
        }
        if(binding.editFechaMascotaA.text.isNullOrEmpty()){
            showToast("El campo 'Fecha de nacimiento' está vacío")
            return
        }

        //accion
        if(imageMascota64!=""){
            mandarimagen()
        }
        agregarMascota(idUsuario)

        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_rigth_to_left, // entrada para el fragmento que se está agregando
            R.anim.exit_left, // salida para el fragmento actual
            R.anim.enter_left_to_rigth, // entrada para el fragmento actualizado
            R.anim.exit_rigth // salida para el fragmento actualizado
        )
        fragmentTransaction.replace(R.id.fragment_container, FragmentoListaMascotas())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun mandarimagen(){

        val dateFormat = SimpleDateFormat("HHmmss")
        val dateFormatD = SimpleDateFormat("ddMMyyyy")
        val fechaActual = Date()
        val fechaFormateada = dateFormatD.format(fechaActual)
        val horaActual = dateFormat.format(Date()).substring(0,6)

        val nombre = mascota.nombre_mascota.trim()+fechaFormateada+horaActual
        mascota.foto_mascota = "http://192.168.100.78/upload_image/img/$nombre.png"
        val imagen = ImageModel(System.currentTimeMillis().toString(),nombre.trim(),imageMascota64)
        viewModel.enviarFoto(imagen)

    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            try {
                val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
                selectedImage = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                imageMascota64 = encodeImageToBase64(selectedImage)
                Toast.makeText(activity,"Tu imagen ya está seleccionada",Toast.LENGTH_SHORT).show()
                // Aquí tienes tu imagen en base64 en la variable base64Image
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun encodeImageToBase64(image: Bitmap?): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        if (imageBytes != null) {
            return Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } else {
            return ""
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
            binding.editFechaMascotaA.setText(selectedDate)
        }, year, month, dayOfMonth)

        datePickerDialog.show()
    }

    fun agregarMascota(idUsuario: Int){
        this.mascota.nombre_mascota = binding.editNombreMascotaA.text.toString()
        this.mascota.color_mascota = binding.editColorMascotaA.text.toString()
        this.mascota.raza_mascota = binding.editRazaMascotaA.text.toString()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(binding.editFechaMascotaA.text.toString())
        val fechaFormateada = sdf.format(date)

        this.mascota.fecha_nacimiento_mascota = fechaFormateada
        this.mascota.id_usuario = idUsuario

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.agregarMascota(mascota)
            activity?.runOnUiThread{
                if (call.isSuccessful){
                    //Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
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
        titulo?.text = "Su mascota se registró correctamente"
        dialogo?.setCancelable(true)
        dialogo?.show()
        Handler().postDelayed({
            dialogo?.dismiss()
        }, 3000)
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