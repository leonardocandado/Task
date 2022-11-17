package com.devmasterteam.tasks.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.databinding.ActivityLoginBinding
import com.devmasterteam.tasks.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Variáveis da classe
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        // Layout
        setContentView(binding.root)

        // Eventos
        binding.buttonLogin.setOnClickListener(this)
        binding.textRegister.setOnClickListener(this)


        viewModel.verifyAuthentication()

        // Observadores
        observe()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.button_login) {
            handleLogin()
        } else if(v.id == R.id.text_register){
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun handleLogin() {
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()

        viewModel.doLogin(email, password)
    }

    private fun observe() {
        viewModel.login.observe(this) {
            if (it.status()) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(applicationContext, it.message(), Toast.LENGTH_SHORT).show()
                binding.editPassword.text.clear()
            }

        }

        viewModel.biometrics.observe(this) {
            if (it) {
                biometric()
            }
        }
    }

    private fun biometric(){
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt  = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        })
        // Informações apresentadas no momento da autenticação
        val info: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticar Biometria")
            // .setSubtitle("Subtítulo")
            // .setDescription("Descrição")
            .setNegativeButtonText(getString(R.string.cancelar))
            .build()

        // Exibe para o usuário
        biometricPrompt.authenticate(info)
    }
}