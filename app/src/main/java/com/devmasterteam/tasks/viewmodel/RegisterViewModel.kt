package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PersonRepository
import com.devmasterteam.tasks.service.repository.SecurityPreferences
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val personRepository = PersonRepository(application.applicationContext)
    private val secutiryPreferences = SecurityPreferences(application.applicationContext)

    private val _user = MutableLiveData<ValidationModel>()
    val user : LiveData<ValidationModel> = _user

    fun create(name: String, email: String, password: String) {
        personRepository.create(name, email, password,
            object : APIListener<PersonModel>{
            override fun onSucess(result: PersonModel) {
                savePreferences(result.token, result.personKey, result.name)
                RetrofitClient.addHeaders(result.token, result.personKey)
                _user.value = ValidationModel()
            }

            override fun onFailure(message: String) {
                _user.value = ValidationModel(message)
            }

        })
    }

    fun savePreferences(token: String, personKey: String, name: String) {
        secutiryPreferences.store(TaskConstants.SHARED.TOKEN_KEY, token)
        secutiryPreferences.store(TaskConstants.SHARED.PERSON_KEY, personKey)
        secutiryPreferences.store(TaskConstants.SHARED.PERSON_NAME, name)

    }

}