package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.R
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityRegisterBinding
import com.example.neatify.model.LoginResponse
import com.example.neatify.model.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvLogin.setOnClickListener {
            finish()
        }

        binding.ivRegisterPasswordToggle.setOnClickListener {
            togglePassword()
        }

        binding.ivRegisterConfirmPasswordToggle.setOnClickListener {
            toggleConfirmPassword()
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun togglePassword() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivRegisterPasswordToggle.setImageResource(R.drawable.ic_eye)
        } else {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivRegisterPasswordToggle.setImageResource(R.drawable.ic_eye_off)
        }

        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    private fun toggleConfirmPassword() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible

        if (isConfirmPasswordVisible) {
            binding.etConfirmPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivRegisterConfirmPasswordToggle.setImageResource(R.drawable.ic_eye)
        } else {
            binding.etConfirmPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivRegisterConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_off)
        }

        binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text.length)
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val emailText = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        val email = if (emailText.isEmpty()) null else emailText

        if (name.isEmpty()) {
            binding.etName.error = "Nama wajib diisi"
            return
        }

        if (phone.isEmpty()) {
            binding.etPhone.error = "Nomor telepon wajib diisi"
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password wajib diisi"
            return
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password minimal 6 karakter"
            return
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Konfirmasi password wajib diisi"
            return
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Password tidak sama"
            return
        }

        if (!binding.cbTerms.isChecked) {
            Toast.makeText(
                this,
                "Kamu harus menyetujui syarat dan ketentuan dulu.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val request = RegisterRequest(
            name = name,
            phone = phone,
            email = email,
            password = password
        )

        RetrofitClient.instance.register(request)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registrasi berhasil. Silakan login.",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registrasi gagal. Cek data kamu.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}