package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.repository.local.TaskDatabase
import com.devmasterteam.tasks.service.repository.remote.PriorityService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class PriorityRepository(context: Context): BaseRepository(context) {

    private val remote = RetrofitClient.getService(PriorityService::class.java)
    private val database = TaskDatabase.getDatabase(context).priorityDAO()

    companion object{
        private val cache = mutableMapOf<Int, String>()
        fun getDescription(id: Int): String{
            return cache[id] ?: ""
        }

        fun setDescription(id: Int, str: String){
            cache[id] = str
        }
    }

    fun getDescription(id: Int): String {
        val cache =  PriorityRepository.getDescription(id)

        return if(cache == ""){
            val description = database.getDescription(id)
            setDescription(id, description)
            description
        } else {
            cache
        }

    }

    fun list(listener: APIListener<List<PriorityModel>>) {
        if(!isConnection()){
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        executeCall(remote.list(), listener)
    }

    fun save(list: List<PriorityModel>) {
        database.clear()
        database.save(list)
    }

    fun list(): List<PriorityModel> = database.list()


}