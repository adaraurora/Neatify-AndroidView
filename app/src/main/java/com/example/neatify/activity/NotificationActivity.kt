package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neatify.adapter.NotificationAdapter
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityNotificationBinding
import com.example.neatify.model.OrderListResponse
import com.example.neatify.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.rvNotifications.layoutManager = LinearLayoutManager(this)

        binding.tvBack.setOnClickListener {
            finish()
        }

        loadNotifications()
    }

    private fun loadNotifications() {
        RetrofitClient.instance.getUserOrders(session.getUserId())
            .enqueue(object : Callback<OrderListResponse> {

                override fun onResponse(
                    call: Call<OrderListResponse>,
                    response: Response<OrderListResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val orders = response.body()?.data ?: emptyList()

                        if (orders.isEmpty()) {
                            binding.rvNotifications.visibility = View.GONE
                            binding.tvEmpty.visibility = View.VISIBLE
                        } else {
                            binding.rvNotifications.visibility = View.VISIBLE
                            binding.tvEmpty.visibility = View.GONE

                            binding.rvNotifications.adapter = NotificationAdapter(orders) { order ->
                                val intent = Intent(this@NotificationActivity, DetailOrderActivity::class.java)
                                intent.putExtra("ORDER_ID", order.id)
                                startActivity(intent)
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@NotificationActivity,
                            "Gagal mengambil notifikasi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                    Toast.makeText(
                        this@NotificationActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}