package com.example.ttmrcan

import com.google.gson.annotations.SerializedName

data class HorasOcupadasResponse(
    @SerializedName("listaHorasOcupadas") var listaHorasOcupadas : ArrayList<HorasOcupadas>
)
