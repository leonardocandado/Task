package com.devmasterteam.tasks.service.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseRepository(val context: Context) {

    private fun failResponse(str: String): String {
        return Gson().fromJson(str, String::class.java)

    }

    fun <T> executeCall(call: Call<T>, listener: APIListener<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.code() == TaskConstants.HTTP.SUCCESS) {
                    response.body()?.let { listener.onSucess(it) }
                } else {
                    listener.onFailure(failResponse(response.errorBody()!!.string()))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }
        })
    }

    fun isConnection(): Boolean {
        var result = true

        //Gerenciador de conexão
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Verifica versão do sistema rodando a aplicação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica se possui rede
            val activeNet = manager.activeNetwork ?: return false
            //Verifica quais funcionalidades disponives, Wifi, 3G...
            val netWorkCapabilities = manager.getNetworkCapabilities(activeNet) ?: return false

            result = when {
                netWorkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                netWorkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                netWorkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            if (manager.activeNetworkInfo != null) {
                //Deprecated
                result = when (manager.activeNetworkInfo!!.type) {
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }

        return result

    }
}