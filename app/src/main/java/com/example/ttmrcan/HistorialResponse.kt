package com.example.ttmrcan

import com.google.gson.annotations.SerializedName

data class HistorialResponse(
    @SerializedName("listaHistorialM") var listaHistorialM : ArrayList<Historial>
)
