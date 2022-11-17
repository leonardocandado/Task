package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.helper.BiometricHelper
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PersonRepository
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.SecurityPreferences
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val personRepository = PersonRepository(application.applicationContext)
    private val secutiryPreferences = SecurityPreferences(application.applicationContext)
    private val priorityRepository = PriorityRepository(application.applicationContext)


    private val _login = MutableLiveData<ValidationModel>()
    val login: LiveData<ValidationModel> = _login

    private val _biometrics = MutableLiveData<Boolean>()
    val biometrics: LiveData<Boolean> = _biometrics

    //Faz login usando API
    fun doLogin(email: String, password: String) {
        personRepository.login(email, password, object : APIListener<PersonModel> {
            override fun onSucess(result: PersonModel) {
                savePreferences(result.token, result.personKey, result.name)
                RetrofitClient.addHeaders(result.token, result.personKey)
                _login.value = ValidationModel()
            }

            override fun onFailure(message: String) {
                _login.value = ValidationModel(message)
            }
        })
    }

    //Verifica se usuário está logado
    fun verifyAuthentication() {
        val token = secutiryPreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val person = secutiryPreferences.get(TaskConstants.SHARED.PERSON_KEY)

        RetrofitClient.addHeaders(token, person)

        //se o token e personKey forem diferentes de vazio, usuário está logado
        val logged = (token != "" && person != "")
        //_loggedUser.value = logged


        //Se usuario não estiver logado a aplicação vai atualizar os dados
        if (!logged) {
            priorityRepository.list(object : APIListener<List<PriorityModel>> {
                override fun onSucess(result: List<PriorityModel>) {
                    priorityRepository.save(result)
                }

                override fun onFailure(message: String) {

                }
            })
        }

        // Usuário está logado E possui autenticação biométrica
        _biometrics.value = logged && BiometricHelper.isBiometricAvailable(getApplication())

    }

    fun savePreferences(token: String, personKey: String, name: String) {
        secutiryPreferences.store(TaskConstants.SHARED.TOKEN_KEY, token)
        secutiryPreferences.store(TaskConstants.SHARED.PERSON_KEY, personKey)
        secutiryPreferences.store(TaskConstants.SHARED.PERSON_NAME, name)

    }

}