package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.R
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityRegisterBinding
import okhttp3.ResponseBody
import org.json.JSONObject
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

        binding.btnRegister.isEnabled = true
        binding.btnRegister.isClickable = true
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

        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Memproses..."

        val request = mutableMapOf<String, String>(
            "name" to name,
            "phone" to phone,
            "nomor_telepon" to phone,
            "no_telepon" to phone,
            "password" to password,
            "password_confirmation" to confirmPassword,
            "confirm_password" to confirmPassword
        )

        if (!email.isNullOrEmpty()) {
            request["email"] = email
        }

        RetrofitClient.instance.register(request)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Daftar Sekarang"

                    val raw = response.body()?.string()
                        ?: response.errorBody()?.string()
                        ?: ""

                    if (!response.isSuccessful) {
                        showError("Register gagal (${response.code()})", extractMessage(raw, "Cek data kamu"))
                        return
                    }

                    try {
                        val json = JSONObject(raw)
                        val status = json.optBoolean("status", true)
                        val message = json.optString("message", "Registrasi berhasil. Silakan login.")

                        if (status) {
                            Toast.makeText(
                                this@RegisterActivity,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            showError("Register gagal", message)
                        }

                    } catch (e: Exception) {
                        // Kalau Laravel sukses tapi balasannya bukan JSON, tetap arahkan ke login.
                        // Ini buat nyelametin demo dari backend yang suka kirim response ajaib.
                        Toast.makeText(
                            this@RegisterActivity,
                            "Register diproses. Silakan coba login.",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Daftar Sekarang"
                    showError("Register gagal konek", t.message ?: "Tidak ada detail error")
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
