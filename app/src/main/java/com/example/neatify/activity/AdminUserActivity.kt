package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neatify.adapter.AdminUserAdapter
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityAdminUserBinding
import com.example.neatify.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsers.layoutManager = LinearLayoutManager(this)

        binding.tvBack.setOnClickListener {
            finish()
        }

        loadUsers()
    }

    private fun loadUsers() {
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

                        binding.tvTotalUsers.text = "Total pelanggan: ${users.size}"

                        binding.rvUsers.adapter = AdminUserAdapter(users) { user ->
                            val intent = Intent(this@AdminUserActivity, AdminUserDetailActivity::class.java)
                            intent.putExtra("USER_ID", user.id)
                            intent.putExtra("USER_NAME", user.name)
                            intent.putExtra("USER_PHONE", user.phone)
                            intent.putExtra("USER_EMAIL", user.email)
                            intent.putExtra("USER_ALAMAT", user.alamat)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(
                            this@AdminUserActivity,
                            "Gagal mengambil data pelanggan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    Toast.makeText(
                        this@AdminUserActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}