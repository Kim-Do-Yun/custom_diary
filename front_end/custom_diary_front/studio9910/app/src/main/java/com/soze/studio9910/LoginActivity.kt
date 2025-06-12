package com.soze.studio9910

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private var isAutoLoginChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val checkIcon = findViewById<ImageView>(R.id.autoLoginCheckIcon)
        val autoLoginText = findViewById<TextView>(R.id.autoLoginText)
        val signupButton = findViewById<TextView>(R.id.signupButton)
        val primaryColor = Color.parseColor("#FF6F61")
        val grayColor = Color.parseColor("#868686")

        val toggle = {
            isAutoLoginChecked = !isAutoLoginChecked
            checkIcon.setColorFilter(if (isAutoLoginChecked) primaryColor else grayColor)
        }

        checkIcon.setOnClickListener { toggle() }
        autoLoginText.setOnClickListener { toggle() }
        // 초기 상태: 회색
        checkIcon.setColorFilter(grayColor)

        // 회원가입 버튼 클릭 시 약관 동의 페이지로 이동
        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupTermsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
} 