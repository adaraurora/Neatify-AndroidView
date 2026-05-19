package com.example.neatify.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityTopUpBinding
import com.example.neatify.model.LoginResponse
import com.example.neatify.model.TopUpRequest
import com.example.neatify.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopUpBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.tvBack.setOnClickListener {
            finish()
        }

        binding.btnTopUp.setOnClickListener {
            topUp()
        }
    }

    private fun topUp() {
        val amountText = binding.etAmount.text.toString().trim()

        if (amountText.isEmpty()) {
            binding.etAmount.error = "Nominal wajib diisi"
            return
        }

        val amount = amountText.toInt()

        if (amount < 1000) {
            binding.etAmount.error = "Minimal top up Rp1.000"
            return
        }

        val request = TopUpRequest(amount)

        RetrofitClient.instance.topUpSaldo(session.getUserId(), request)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(
                            this@TopUpActivity,
                            "Top up berhasil",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    } else {
                        Toast.makeText(
                            this@TopUpActivity,
                            "Top up gagal",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@TopUpActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}