package com.example.ttmrcan

import com.google.gson.annotations.SerializedName

data class CitasResponse(
    @SerializedName("listaCitas") var listaCitas: ArrayList<Cita>
)
