package com.example.ttmrcan

data class Usuario(
    var id_usuario: Int,
    var nombre_usuario: String,
    var apellido_usuario: String,
    var telefono_usuario: String,
    var email_usuario: String,
    var estado_usuario: String,
    var ciudad_usuario: String,
    var colonia_usuario: String,
    var cp_usuario: Int,
    var calle_usuario: String,
    var num_ext_usuario: String,
    var password_usuario: String,
    var estatus_usuario: Int,
    var baja_usuario: Int,
    //var foto_usuario: ByteArray, //Preguntar
    var id_veterinario: Int
)
