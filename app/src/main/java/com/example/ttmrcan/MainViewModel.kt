package com.example.ttmrcan

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel: ViewModel() {

    val mensaje = MutableLiveData<String>()

    fun enviarFoto(image: ImageModel){
        viewModelScope.launch(Dispatchers.IO){
            val response = RetrofitCliente.webService.enviarImage(image.idImage,image.nomImage,image.image)
            withContext(Dispatchers.Main){
                if (response.isSuccessful){
                    //val message: String? = response.body()
                }

            }
        }
    }

}