package com.example.ttmrcan

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

object AppConstantes{
    const val  BASE_URL = "http://192.168.100.78:4000"
}

interface WebServ {

    @GET("/usuario/{email_usuario}")
    suspend fun obtenerIdUsuario(
        @Path("email_usuario") email_usuario: String?
    ): Response<Usuario>

    @GET("/mascotas/{id_usuario}")
    suspend fun obtenerMascotasUsuario(
        @Path("id_usuario") id_usuario: Int
    ): Response<MascotasResponse>

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

    @PUT("/usuario/update/{id}")
    suspend fun actualizarUsuario(
        @Path("id_usuario") id_usuario: Int,
        @Body usuario: Usuario
    ): Response<String>

    @PUT("/mascota/update/{id_mascota}")
    suspend fun actualizarMascota(
        @Path("id_mascota") id_mascota: Int,
        @Body mascota: Mascota
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