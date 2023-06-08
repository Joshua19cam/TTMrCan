package com.example.ttmrcan

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.ttmrcan.databinding.FragmentFragmentoEditarMascotaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
        this.binding = FragmentFragmentoEditarMascotaBinding.inflate(inflater, container, false)

        return binding.root
    }

    var mascota = Mascota(-1,"","","","",
         "","","",0,"",-1)
    var isEditando = false
    var imageMascota64 = ""
    lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val idMascota = arguments?.getInt("id_mascota")
        mascota.id_mascota = idMascota!!
        val nombreMascota = arguments?.getString("nombre_mascota")
        mascota.nombre_mascota = nombreMascota!!
        val colorMascota = arguments?.getString("color_mascota")
        val razaMascota = arguments?.getString("raza_mascota")
        val fechaMascota = arguments?.getString("fecha_nacimiento_mascota")

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(fechaMascota)
        val fechaFormateada = sdf.format(date)

        val fotoMascota = arguments?.getString("foto_mascota")
        mascota.foto_mascota = fotoMascota.toString()

        binding.editNombreMascotaE.setText(nombreMascota)
        binding.editColorMascotaE.setText(colorMascota)
        binding.editRazaMascotaE.setText(razaMascota)
        binding.editFechaMascotaE.setText(fechaFormateada)

        if(fotoMascota!=""){
            val uniqueId = System.currentTimeMillis().toString()
            Glide.with(this).load(fotoMascota).signature(ObjectKey(uniqueId)).into(binding.imageView2)
        }
        binding.editFechaMascotaE.isFocusable = false
        binding.editFechaMascotaE.isFocusableInTouchMode = false
        binding.editFechaMascotaE.inputType = InputType.TYPE_NULL
        binding.editFechaMascotaE.setOnClickListener{
            showDatePickerDialog()
        }

        binding.buttonEditarImagen.setOnClickListener {
            openGallery()
        }

        binding.buttonGuardarEditar.setOnClickListener {
            val isValido = validarCampos()
            if(isValido){
                if(imageMascota64!=""){
                    mandarimagen(fotoMascota.toString())
                }
                actualizarMascota()
            }else{
                Toast.makeText(activity,"Alguno de los campos está vacío", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCancelarEditar.setOnClickListener {
            requireActivity().onBackPressed() //Esto se ocupa para regresar entre fragments ES UTIL
        }

    }

    fun mandarimagen(img : String){

        if (img!=""){
            val pattern = Regex("""/([A-Za-z0-9]+)\.png$""")
            val matchResult = pattern.find(mascota.foto_mascota)

            val nombre = matchResult?.groupValues?.get(1)

            val imagen = ImageModel(System.currentTimeMillis().toString(),nombre.toString(),imageMascota64)
            viewModel.enviarFoto(imagen)

        }else{
            val dateFormat = SimpleDateFormat("HHmmss")
            val dateFormatD = SimpleDateFormat("ddMMyyyy")
            val fechaActual = Date()
            val fechaFormateada = dateFormatD.format(fechaActual)
            val horaActual = dateFormat.format(Date()).substring(0,6)

            val nombreC = mascota.nombre_mascota.trim()+fechaFormateada+horaActual
            this.mascota.foto_mascota = "http://192.168.100.78/upload_image/img/$nombreC.png"
            val imagenC = ImageModel(System.currentTimeMillis().toString(),nombreC.trim(),imageMascota64)
            viewModel.enviarFoto(imagenC)
        }

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
            binding.editFechaMascotaE.setText(selectedDate)
        }, year, month, dayOfMonth)

        datePickerDialog.show()
    }
    fun actualizarMascota() {

        this.mascota.nombre_mascota = binding.editNombreMascotaE.text.toString()
        this.mascota.color_mascota = binding.editColorMascotaE.text.toString()
        this.mascota.raza_mascota = binding.editRazaMascotaE.text.toString()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(binding.editFechaMascotaE.text.toString())
        val fechaFormateada = sdf.format(date)

        this.mascota.fecha_nacimiento_mascota = fechaFormateada

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.actualizarMascota(mascota.id_mascota, mascota)
            activity?.runOnUiThread{
                if (call.isSuccessful){
                    //Toast.makeText(activity,call.body().toString(),Toast.LENGTH_SHORT).show()
                    mostrarDialogo()
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

    private fun mostrarDialogo() {
        val dialogo = activity?.let { Dialog(it, R.style.CustomDialogStyle) }
        dialogo?.setContentView(R.layout.dialogo_cambio_exitoso)
        val titulo = dialogo?.findViewById<TextView>(R.id.dialogo_correcto)
        titulo?.text = "Los datos de su mascota se actualizaron correctamente"
        dialogo?.setCancelable(true)
        dialogo?.show()
        Handler().postDelayed({
            dialogo?.dismiss()
        }, 4000)
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