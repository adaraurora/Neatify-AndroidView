package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.R
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityLoginBinding
import com.example.neatify.model.LoginRequest
import com.example.neatify.model.LoginResponse
import com.example.neatify.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    private var isLoginPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgot.setOnClickListener {
            Toast.makeText(
                this,
                "Fitur lupa password belum tersedia.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.ivLoginPasswordToggle.setOnClickListener {
            toggleLoginPassword()
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun toggleLoginPassword() {
        isLoginPasswordVisible = !isLoginPasswordVisible

        if (isLoginPasswordVisible) {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivLoginPasswordToggle.setImageResource(R.drawable.ic_eye)
        } else {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivLoginPasswordToggle.setImageResource(R.drawable.ic_eye_off)
        }

        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    private fun loginUser() {
        val login = binding.etLogin.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (login.isEmpty()) {
            binding.etLogin.error = "Nomor telepon atau email wajib diisi"
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password wajib diisi"
            return
        }

        val request = LoginRequest(
            login = login,
            password = password
        )

        RetrofitClient.instance.login(request)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val body = response.body()
                        val user = body?.data
                        val token = body?.token

                        if (user != null && token != null) {

                            // Admin TIDAK BOLEH masuk dari aplikasi Android.
                            // Admin nanti login lewat Laravel Dashboard.
                            if (user.role == "admin") {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Admin login melalui dashboard Laravel.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return
                            }

                            session.saveLogin(
                                userId = user.id,
                                name = user.name,
                                token = token,
                                role = user.role
                            )

                            Toast.makeText(
                                this@LoginActivity,
                                "Login berhasil",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                            )
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login gagal. Cek akun kamu.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}