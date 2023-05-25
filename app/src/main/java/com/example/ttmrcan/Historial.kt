package com.example.ttmrcan

data class Historial(
    var id_consulta : Int,
    var tipo_consulta : String,
    var proxima_cita_consulta : String,
    var peso_consulta : Int,
    var producto_consulta: String,
    var aplico_consulta: String,
    var descripcion_consulta: String,
    var observaciones_consulta: String,
    var id_mascota : Int,
    var id_veterinario : Int,
    var id_cita : Int
)
