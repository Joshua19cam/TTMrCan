package com.example.ttmrcan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.ttmrcan.databinding.ActivityRegistroClienteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistroCliente : AppCompatActivity() {
    //Inicializamos declarando los componentes que se estarán utilizando

    lateinit var binding: ActivityRegistroClienteBinding
    lateinit var botonRegistro: Button
    var usuario = Usuario(-1,"", "","","",""
        ,"","",-1,"","","",0,
        0,1)
    var isEditando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Lo asociamos al componente en el archivo .XML
        binding.buttonRegistrar.setOnClickListener {
            var isValido = validarCampos()
            if(isValido){
                if(!isEditando){
                    agregarUsuario()
                    val intent = Intent(this, RegistroClienteV::class.java)
/*                    intent.putExtra("usuarioEmail",usuario.email_usuario)
                    intent.putExtra("usuarioNombre",usuario.nombre_usuario)*/
                    startActivity(intent)
                }
            }
        }
    }

    fun validarCampos(): Boolean{
        return !(binding.editNombre.text.isNullOrEmpty()||binding.editApellido.text.isNullOrEmpty()
                ||binding.editTelefono.text.isNullOrEmpty()||binding.editCorreo.text.isNullOrEmpty()
                ||binding.editEstado.text.isNullOrEmpty()||binding.editCiudad.text.isNullOrEmpty()
                ||binding.editColonia.text.isNullOrEmpty()||binding.editCP.text.isNullOrEmpty()
                ||binding.editCalle.text.isNullOrEmpty()||binding.editNumE.text.isNullOrEmpty()
                ||binding.editContra.text.isNullOrEmpty())
    }
    fun agregarUsuario(){
        this.usuario.nombre_usuario = binding.editNombre.text.toString()
        this.usuario.apellido_usuario = binding.editApellido.text.toString()
        this.usuario.telefono_usuario = binding.editTelefono.text.toString()
        this.usuario.email_usuario = binding.editCorreo.text.toString()
        this.usuario.estado_usuario = binding.editEstado.text.toString()
        this.usuario.ciudad_usuario = binding.editCiudad.text.toString()
        this.usuario.colonia_usuario = binding.editColonia.text.toString()
        this.usuario.cp_usuario = binding.editCP.text.toString().toInt()
        this.usuario.calle_usuario = binding.editCalle.text.toString()
        this.usuario.num_ext_usuario = binding.editNumE.text.toString()
        this.usuario.password_usuario = binding.editContra.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webServ.agregarUsuario(usuario)
            runOnUiThread{
                if (call.isSuccessful){
                    Toast.makeText(this@RegistroCliente,call.body().toString(), Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                    limpiarObjeto()
                }else{
                    Toast.makeText(this@RegistroCliente,call.body().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun limpiarObjeto(){
        this.usuario.id_usuario = -1
        this.usuario.nombre_usuario = ""
        this.usuario.apellido_usuario = ""
        this.usuario.telefono_usuario = ""
        this.usuario.email_usuario = ""
        this.usuario.estado_usuario = ""
        this.usuario.ciudad_usuario = ""
        this.usuario.colonia_usuario = ""
        this.usuario.cp_usuario = -1
        this.usuario.calle_usuario = ""
        this.usuario.num_ext_usuario = ""
        this.usuario.password_usuario = ""
    }

    fun limpiarCampos(){
        binding.editNombre.setText("")
        binding.editApellido.setText("")
        binding.editTelefono.setText("")
        binding.editCorreo.setText("")
        binding.editEstado.setText("")
        binding.editCiudad.setText("")
        binding.editColonia.setText("")
        binding.editCP.setText("")
        binding.editCalle.setText("")
        binding.editNumE.setText("")
        binding.editContra.setText("")
        binding.editConfContra.setText("")
    }
}

