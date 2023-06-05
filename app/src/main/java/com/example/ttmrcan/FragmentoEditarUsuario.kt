package com.example.ttmrcan

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import com.example.ttmrcan.databinding.FragmentFragmentoAgregarMascotaBinding
import com.example.ttmrcan.databinding.FragmentFragmentoEditarUsuarioBinding
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
 * Use the [FragmentoEditarUsuario.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoEditarUsuario : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFragmentoEditarUsuarioBinding
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
        this.binding = FragmentFragmentoEditarUsuarioBinding.inflate(inflater, container, false)

        return binding.root
    }

    var isEditando = false
    var usuario = Usuario(1,"","",
        "","","","","",1,
        "","","",1,1,"",1)
    var imageUsuario64 = ""
    lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val idUsuario = arguments?.getInt("id_usuario")
        usuario.id_usuario = idUsuario!!
        val nombreUsuario = arguments?.getString("nombre_usuario") //Nombre
        usuario.nombre_usuario = nombreUsuario!!
        val apellidosUsuario = arguments?.getString("apellido_usuario") // Apellido
        val telefonoUsuario = arguments?.getString("telefono_usuario") // Telefono
        val emailUsuario = arguments?.getString("email_usuario") // Correo
        val estadoUsuario = arguments?.getString("estado_usuario") // Estado
        val ciudadUsuario = arguments?.getString("ciudad_usuario")// Ciudad
        val coloniaUsuario = arguments?.getString("colonia_usuario") // Colonia
        val cpUsuario = arguments?.getInt("cp_usuario") // CODIGO POSTAL
        val calleUsuario = arguments?.getString("calle_usuario") // Calle
        val numExtUsuario = arguments?.getString("num_ext_usuario") // Num Ext
        val passwordUsuario = arguments?.getString("password_usuario") // Contraseña
        val fotoUsuario = arguments?.getString("foto_usuario") // Foto
        usuario.foto_usuario = fotoUsuario!!

        if(fotoUsuario!=""){
            val uniqueId = System.currentTimeMillis().toString()
            Glide.with(this).load(fotoUsuario).signature(ObjectKey(uniqueId)).into(binding.imageView2)
        }

        binding.etNombreEditarPerfil.setText(nombreUsuario)
        binding.etApellidoEditarPerfil.setText(apellidosUsuario)
        binding.etTelefonoEditarPerfil.setText(telefonoUsuario)
        binding.etCorreoRegistroEditar.setText(emailUsuario)
        binding.etEstadoEditarPerfil.setText(estadoUsuario)
        binding.etCiudadEditarPerfil.setText(ciudadUsuario)
        binding.etColoniaEditarPerfil.setText(coloniaUsuario)
        binding.etCPEditarPerfil.setText(cpUsuario.toString())
        binding.etCalleEditarPerfil.setText(calleUsuario)
        binding.etNumExtEditarPerfil.setText(numExtUsuario)
        binding.etContrasenaEditarPerfil.setText(passwordUsuario)
        binding.etConfirmarContrasenaEditarPerfil.setText(passwordUsuario)

        binding.buttonCancelarEditarPerfilUsuario.setOnClickListener {
            requireActivity().onBackPressed() //Esto se ocupa para regresar entre fragments ES UTIL
        }

        binding.btnEditarPerfilUsuario.setOnClickListener {
            openGallery()
        }

        binding.buttonGuardarEditarPerfilUsuario.setOnClickListener {
            val isValido = validarCampos()
            if(isValido){
                if(!isEditando){
                    if(imageUsuario64!=""){
                        mandarimagen(fotoUsuario.toString())
                    }
                    actualizarUsuario()
                }
            }
        }

    }

    fun actualizarUsuario(){
        this.usuario.nombre_usuario = binding.etNombreEditarPerfil.text.toString()
        this.usuario.apellido_usuario = binding.etApellidoEditarPerfil.text.toString()
        this.usuario.telefono_usuario = binding.etTelefonoEditarPerfil.text.toString()
        this.usuario.email_usuario = binding.etCorreoRegistroEditar.text.toString()
        this.usuario.estado_usuario = binding.etEstadoEditarPerfil.text.toString()
        this.usuario.ciudad_usuario = binding.etCiudadEditarPerfil.text.toString()
        this.usuario.colonia_usuario = binding.etColoniaEditarPerfil.text.toString()
        this.usuario.cp_usuario = binding.etCPEditarPerfil.text.toString().toInt()
        this.usuario.calle_usuario = binding.etCalleEditarPerfil.text.toString()
        this.usuario.num_ext_usuario = binding.etNumExtEditarPerfil.text.toString()
        this.usuario.password_usuario = binding.etContrasenaEditarPerfil.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.actualizarUsuario(usuario.id_usuario,usuario)
            activity?.runOnUiThread{
                if (call.isSuccessful){
                    Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                    mostrarDialogo()
                    requireActivity().onBackPressed()

                }else{
                    Toast.makeText(activity,call.body().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun validarCampos(): Boolean{
        return !(binding.etNombreEditarPerfil.text.isNullOrEmpty()||binding.etApellidoEditarPerfil.text.isNullOrEmpty()
                ||binding.etTelefonoEditarPerfil.text.isNullOrEmpty()||binding.etCorreoRegistroEditar.text.isNullOrEmpty()
                ||binding.etEstadoEditarPerfil.text.isNullOrEmpty()||binding.etCiudadEditarPerfil.text.isNullOrEmpty()
                ||binding.etColoniaEditarPerfil.text.isNullOrEmpty()||binding.etCPEditarPerfil.text.isNullOrEmpty()
                ||binding.etCalleEditarPerfil.text.isNullOrEmpty()||binding.etNumExtEditarPerfil.text.isNullOrEmpty()
                ||binding.etContrasenaEditarPerfil.text.isNullOrEmpty()||binding.etConfirmarContrasenaEditarPerfil.text.isNullOrEmpty())
    }

    private fun mostrarDialogo() {
        val dialogo = activity?.let { Dialog(it, R.style.CustomDialogStyle) }
        dialogo?.setContentView(R.layout.dialogo_cambio_exitoso)
        val titulo = dialogo?.findViewById<TextView>(R.id.dialogo_correcto)
        titulo?.text = "Los datos de su perfil se actualizaron correctamente"
        dialogo?.setCancelable(true)
        dialogo?.show()
        Handler().postDelayed({
            dialogo?.dismiss()
        }, 4000)
    }

    fun mandarimagen(img : String){

        if (img!=""){
            val pattern = Regex("""/(\d+)([A-Za-z0-9]+)\.png$""")
            val matchResult = pattern.find(usuario.foto_usuario)

            val variable = matchResult?.groupValues?.get(1) + matchResult?.groupValues?.get(2)

            val imagen = ImageModel(System.currentTimeMillis().toString(),variable.toString(),imageUsuario64)
            viewModel.enviarFoto(imagen)

        }else{
            val dateFormat = SimpleDateFormat("HHmmss")
            val dateFormatD = SimpleDateFormat("ddMMyyyy")
            val fechaActual = Date()
            val fechaFormateada = dateFormatD.format(fechaActual)
            val horaActual = dateFormat.format(Date()).substring(0,6)

            val nombreC = usuario.id_usuario.toString().trim()+usuario.nombre_usuario.trim()+fechaFormateada+horaActual
            this.usuario.foto_usuario = "http://192.168.100.78/upload_image/img/$nombreC.png"
            val imagenC = ImageModel(System.currentTimeMillis().toString(),nombreC.trim(),imageUsuario64)
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
                imageUsuario64= encodeImageToBase64(selectedImage)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoEditarUsuario.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoEditarUsuario().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}