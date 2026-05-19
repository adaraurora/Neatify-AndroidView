package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neatify.adapter.WalletTransactionAdapter
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityWalletBinding
import com.example.neatify.model.LoginResponse
import com.example.neatify.model.WalletTransactionResponse
import com.example.neatify.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.rvTransactions.layoutManager = LinearLayoutManager(this)

        binding.tvBack.setOnClickListener {
            finish()
        }

        binding.btnTopUp.setOnClickListener {
            startActivity(Intent(this, TopUpActivity::class.java))
        }

        loadProfile()
        loadTransactions()
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
        loadTransactions()
    }

    private fun loadProfile() {
        RetrofitClient.instance.getProfile(session.getUserId())
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val user = response.body()?.data
                        binding.tvSaldo.text = "Rp${formatRupiah(user?.saldo ?: 0)}"
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // diam aja
                }
            })
    }

    private fun loadTransactions() {
        RetrofitClient.instance.getWalletTransactions(session.getUserId())
            .enqueue(object : Callback<WalletTransactionResponse> {
                override fun onResponse(
                    call: Call<WalletTransactionResponse>,
                    response: Response<WalletTransactionResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val transactions = response.body()?.data ?: emptyList()

                        if (transactions.isEmpty()) {
                            binding.rvTransactions.visibility = View.GONE
                            binding.tvEmpty.visibility = View.VISIBLE
                        } else {
                            binding.rvTransactions.visibility = View.VISIBLE
                            binding.tvEmpty.visibility = View.GONE

                            binding.rvTransactions.adapter =
                                WalletTransactionAdapter(transactions)
                        }
                    } else {
                        Toast.makeText(
                            this@WalletActivity,
                            "Gagal mengambil riwayat dompet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<WalletTransactionResponse>, t: Throwable) {
                    Toast.makeText(
                        this@WalletActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun formatRupiah(value: Int): String {
        return "%,d".format(value).replace(",", ".")
    }
}