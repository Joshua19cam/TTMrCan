package com.example.ttmrcan

data class Cita(
    var id_cita: Int, //este se autoincrementa
    var tipo_cita: String,
    var descripcion_cita: String,
    var observaciones_cita: String?,
    var fecha_cita: String,
    var hora_cita: String,
    var id_mascota: Int, // se agrega con el spinner
    var id_usuario: Int, // se agrega con el inicio de sesion
    var id_veterinario: Int, // Siempore es 1
    var estatus_cita: Int //este lo cambia el veterianrio
)
