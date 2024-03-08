package com.alltimeowl.payrit.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.alltimeowl.payrit.databinding.ActivityLoginBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    private var backPressedTime: Long = 0
    private val backPressedInterval = 2000 // 뒤로가기 버튼을 두 번 누르는 간격 (2초)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moveToHome()
    }

    private fun moveToHome() {
        binding.buttonNoLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // LoginActivity 종료 (스택 제거)
            finish()
        }

    }

    // 뒤로가기 버튼 이벤트 처리
    override fun onBackPressed() {
        if (backPressedTime + backPressedInterval > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity() // 앱 종료
        } else {
            Toast.makeText(this, "뒤로 가기를 한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}