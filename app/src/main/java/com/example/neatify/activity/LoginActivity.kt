package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.R
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityLoginBinding
import com.example.neatify.utils.SessionManager
import okhttp3.ResponseBody
import org.json.JSONObject
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

        binding.btnLogin.isEnabled = true
        binding.btnLogin.isClickable = true
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

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Memproses..."

        val request = mutableMapOf(
            "login" to login,
            "email" to login,
            "phone" to login,
            "nomor_telepon" to login,
            "no_telepon" to login,
            "password" to password
        )

        RetrofitClient.instance.login(request)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Masuk"

                    val raw = response.body()?.string()
                        ?: response.errorBody()?.string()
                        ?: ""

                    if (!response.isSuccessful) {
                        showError("Login gagal (${response.code()})", extractMessage(raw, "Cek akun kamu"))
                        return
                    }

                    try {
                        val json = JSONObject(raw)
                        val status = json.optBoolean("status", false)

                        if (!status) {
                            showError("Login gagal", json.optString("message", "Cek akun kamu"))
                            return
                        }

                        val data = json.optJSONObject("data")
                        val token = json.optString("token", "")

                        if (data == null || token.isEmpty()) {
                            showError("Login gagal", "Data user/token kosong dari server")
                            return
                        }

                        val userId = data.optInt("id", 0)
                        val name = data.optString("name", login)
                        val role = data.optString("role", "user")

                        session.saveLogin(
                            userId = userId,
                            name = name,
                            token = token,
                            role = role
                        )

                        Toast.makeText(
                            this@LoginActivity,
                            "Login berhasil",
                            Toast.LENGTH_SHORT
                        ).show()

                        val nextIntent = if (role == "admin") {
                            Intent(this@LoginActivity, AdminMainActivity::class.java)
                        } else {
                            Intent(this@LoginActivity, MainActivity::class.java)
                        }

                        startActivity(nextIntent)
                        finish()

                    } catch (e: Exception) {
                        showError(
                            "Login gagal baca response",
                            "Server tidak mengirim JSON valid. Isi response: ${raw.take(300)}"
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Masuk"
                    showError("Login gagal konek", t.message ?: "Tidak ada detail error")
                }
            })
    }

    private fun extractMessage(raw: String, fallback: String): String {
        return try {
            val json = JSONObject(raw)
            json.optString("message", raw.ifEmpty { fallback })
        } catch (e: Exception) {
            raw.ifEmpty { fallback }.take(500)
        }
    }

    private fun showError(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Oke", null)
            .show()
    }
}
