package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neatify.adapter.OrderAdapter
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityAdminUserDetailBinding
import com.example.neatify.model.OrderListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminUserDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUserDetailBinding
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("USER_ID", 0)

        binding.tvName.text = intent.getStringExtra("USER_NAME") ?: "-"
        binding.tvPhone.text = "Nomor: ${intent.getStringExtra("USER_PHONE") ?: "-"}"
        binding.tvEmail.text = "Email: ${intent.getStringExtra("USER_EMAIL") ?: "-"}"
        binding.tvAlamat.text = "Alamat: ${intent.getStringExtra("USER_ALAMAT") ?: "-"}"

        binding.rvUserOrders.layoutManager = LinearLayoutManager(this)

        binding.tvBack.setOnClickListener {
            finish()
        }

        loadUserOrders()
    }

    private fun loadUserOrders() {
        RetrofitClient.instance.getUserOrders(userId)
            .enqueue(object : Callback<OrderListResponse> {

                override fun onResponse(
                    call: Call<OrderListResponse>,
                    response: Response<OrderListResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val orders = response.body()?.data ?: emptyList()

                        binding.rvUserOrders.adapter = OrderAdapter(orders) { order ->
                            val intent = Intent(this@AdminUserDetailActivity, AdminOrderDetailActivity::class.java)
                            intent.putExtra("ORDER_ID", order.id)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(
                            this@AdminUserDetailActivity,
                            "Gagal mengambil riwayat pesanan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminUserDetailActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}