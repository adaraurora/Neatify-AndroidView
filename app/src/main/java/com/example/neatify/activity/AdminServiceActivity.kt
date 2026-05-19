package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neatify.adapter.AdminServiceAdapter
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityAdminServiceBinding
import com.example.neatify.model.DeleteResponse
import com.example.neatify.model.Service
import com.example.neatify.model.ServiceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvAdminServices.layoutManager = LinearLayoutManager(this)

        binding.tvBack.setOnClickListener {
            finish()
        }

        binding.btnAddService.setOnClickListener {
            val intent = Intent(this, FormServiceActivity::class.java)
            startActivity(intent)
        }

        loadServices()
    }

    override fun onResume() {
        super.onResume()
        loadServices()
    }

    private fun loadServices() {
        RetrofitClient.instance.getServices()
            .enqueue(object : Callback<ServiceResponse> {
                override fun onResponse(
                    call: Call<ServiceResponse>,
                    response: Response<ServiceResponse>
                ) {
                    if (response.isSuccessful) {
                        val services = response.body()?.data ?: emptyList()

                        binding.rvAdminServices.adapter = AdminServiceAdapter(
                            services,
                            onEdit = { service ->
                                val intent = Intent(this@AdminServiceActivity, FormServiceActivity::class.java)
                                intent.putExtra("SERVICE_ID", service.id)
                                intent.putExtra("NAMA", service.nama_layanan)
                                intent.putExtra("DESKRIPSI", service.deskripsi)
                                intent.putExtra("HARGA", service.harga)
                                intent.putExtra("SATUAN", service.satuan)
                                intent.putExtra("IMAGE", service.image)
                                startActivity(intent)
                            },
                            onDelete = { service ->
                                deleteService(service)
                            }
                        )
                    } else {
                        Toast.makeText(
                            this@AdminServiceActivity,
                            "Gagal mengambil layanan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ServiceResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminServiceActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun deleteService(service: Service) {
        RetrofitClient.instance.deleteService(service.id)
            .enqueue(object : Callback<DeleteResponse> {
                override fun onResponse(
                    call: Call<DeleteResponse>,
                    response: Response<DeleteResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AdminServiceActivity,
                            "Layanan berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadServices()
                    } else {
                        Toast.makeText(
                            this@AdminServiceActivity,
                            "Gagal menghapus layanan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminServiceActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}