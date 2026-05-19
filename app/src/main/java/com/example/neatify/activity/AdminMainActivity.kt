package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neatify.adapter.AdminOrderAdapter
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityAdminMainBinding
import com.example.neatify.model.Order
import com.example.neatify.utils.SessionManager
import com.example.neatify.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminMainActivity : AppCompatActivity() {

    private fun formatRupiah(value: Int): String {
        return "%,d".format(value).replace(",", ".")
    }

    private lateinit var binding: ActivityAdminMainBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.rvAdminOrders.layoutManager = LinearLayoutManager(this)

        binding.btnAdminLogout.setOnClickListener {
            session.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnManageServices.setOnClickListener {
            startActivity(Intent(this, AdminServiceActivity::class.java))
        }

        binding.btnManageUsers.setOnClickListener {
            startActivity(Intent(this, AdminUserActivity::class.java))
        }

        loadOrders()
        loadUsersCount()
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
        loadUsersCount()
    }

    private fun loadOrders() {
        RetrofitClient.instance.getAllOrders()
            .enqueue(object : Callback<List<Order>> {

                override fun onResponse(
                    call: Call<List<Order>>,
                    response: Response<List<Order>>
                ) {
                    if (response.isSuccessful) {
                        val orders = response.body() ?: emptyList()

                        val activeCount = orders.count {
                            it.status != "selesai" && it.status != "dibatalkan"
                        }

                        val doneCount = orders.count {
                            it.status == "selesai"
                        }

                        val totalIncome = orders
                            .filter { it.status == "selesai" }
                            .sumOf { it.total_harga }

                        binding.tvTotalOrders.text = orders.size.toString()
                        binding.tvActiveOrders.text = activeCount.toString()
                        binding.tvDoneOrders.text = doneCount.toString()
                        binding.tvTotalIncome.text = "Rp${formatRupiah(totalIncome)}"

                        binding.rvAdminOrders.adapter = AdminOrderAdapter(orders) { order ->
                            val intent = Intent(this@AdminMainActivity, AdminOrderDetailActivity::class.java)
                            intent.putExtra("ORDER_ID", order.id)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(
                            this@AdminMainActivity,
                            "Gagal mengambil pesanan admin",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                    Toast.makeText(
                        this@AdminMainActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun loadUsersCount() {
        RetrofitClient.instance.getUsers()
            .enqueue(object : Callback<List<User>> {

                override fun onResponse(
                    call: Call<List<User>>,
                    response: Response<List<User>>
                ) {
                    if (response.isSuccessful) {
                        val users = response.body()?.filter {
                            it.role != "admin"
                        } ?: emptyList()

                        binding.tvTotalUsers.text = users.size.toString()
                    } else {
                        binding.tvTotalUsers.text = "0"
                    }
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    binding.tvTotalUsers.text = "0"
                }
            })
    }
}