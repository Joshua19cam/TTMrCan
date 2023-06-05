package com.example.ttmrcan

data class HorasOcupadas(
    var id_cita: Int,
    var tipo_cita: String,
    var descripcion_cita: String,
    var observaciones_cita: String,
    var fecha_cita: String,
    var hora_cita: String,
    var id_mascota: Int,
    var id_usuario: Int,
    var id_veterinario: Int,
    var estatus_cita: Int
)
