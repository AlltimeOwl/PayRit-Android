package com.alltimeowl.payrit.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alltimeowl.payrit.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}