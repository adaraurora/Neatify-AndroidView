package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.databinding.ActivitySplashBinding
import com.example.neatify.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if (session.isLogin()) {
                if (session.getRole() == "admin") {
                    startActivity(Intent(this, AdminMainActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            } else {
                startActivity(Intent(this, OnboardingActivity::class.java))
            }

            finish()
        }, 2000)
    }
}