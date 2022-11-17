package com.devmasterteam.tasks.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.databinding.ActivityLoginBinding
import com.devmasterteam.tasks.databinding.ActivityRegisterBinding
import com.devmasterteam.tasks.viewmodel.LoginViewModel
import com.devmasterteam.tasks.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Variáveis da classe
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        // Eventos
        binding.buttonSave.setOnClickListener(this)

        // Layout
        setContentView(binding.root)

        observe()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.button_save) {
            handleSave()
        }
    }

    private fun observe() {
        viewModel.user.observe(this) {
            if (it.status()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this,it.message(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSave() {
        viewModel.create(
            binding.editName.text.toString(),
            binding.editEmail.text.toString(),
            binding.editPassword.text.toString()
        )
    }
}