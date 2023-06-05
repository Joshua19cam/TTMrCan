package com.example.ttmrcan

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.Date

object AppConstantes{
    const val  BASE_URL = "http://192.168.100.78:4000"
    //pala : http://192.168.0.123:3000
    //joshua :192.168.100.78
    //omar: 192.168.0.120
    // 192.168.0.112
}

interface WebServ {

    @GET("/usuario/{email_usuario}")
    suspend fun obtenerIdUsuario(
        @Path("email_usuario") email_usuario: String?
    ): Response<Usuario>

    @GET("/usuario/consultas/{id_usuario}")
    suspend fun obtenerConsultas(
        @Path("id_usuario") id_usuario: Int
    ): Response<TotalConsultas>

    @GET("/mascotas/{id_usuario}")
    suspend fun obtenerMascotasUsuario(
        @Path("id_usuario") id_usuario: Int
    ): Response<MascotasResponse>

    @GET("/citas/ocupadas/{fecha_cita}")
    suspend fun obtenerHorasOcupadas(
        @Path("fecha_cita") fecha_cita: String
    ): Response<HorasOcupadasResponse>

    @GET("/mascota/historialM/{id_mascota}/{tipo_consulta}")
    suspend fun obtenerHistorial(
        @Path("id_mascota") id_mascota: Int,
        @Path("tipo_consulta") tipo_consulta: String
    ): Response<HistorialResponse>

    @GET("/usuarioLogin/{email_usuario}/{password_usuario}")
    suspend fun checarLogin(
        @Path("email_usuario") email_usuario: String,
        @Path("password_usuario") password_usuario: String
    ): Response<Token>

    @POST("/usuario/add")
    suspend fun agregarUsuario(
        @Body usuario: Usuario
    ): Response<String>


    @POST("/mascota/add")
    suspend fun agregarMascota(
        @Body mascota: Mascota
    ): Response<String>

    @POST("/enviarCorreo")
    suspend fun enviarCorreo(
        @Body email_usuario: CorreoDestino
    ): Response<String>

    @PUT("/usuario/update/{id_usuario}")
    suspend fun actualizarUsuario(
        @Path("id_usuario") id_usuario: Int,
        @Body usuario: Usuario
    ): Response<String>

    @PUT("/mascota/update/{id_mascota}")
    suspend fun actualizarMascota(
        @Path("id_mascota") id_mascota: Int,
        @Body mascota: Mascota
    ): Response<String>

    @PUT("/mascota/darBaja/{id_mascota}")
    suspend fun darBaja(
        @Path("id_mascota") id_mascota: Int,
        @Body baja_mascota: Mascota
    ): Response<String>

    @GET("/citas/{id_usuario}")
    suspend fun obtenerCitas(
        @Path("id_usuario") id_usuario: Int
    ): Response<CitasResponse>
    @POST("/cita/add")
    suspend fun agregarCita(
        @Body cita: Cita
    ): Response<String>

    //eliminar cita/cancelar
    @DELETE("/cita/delete/{id_cita}")
    suspend fun cancelarCita(
        @Path("id_cita") id_cita: Int
    ): Response<String>



}
object RetrofitClient{
    val webServ: WebServ by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(WebServ::class.java)
    }
}