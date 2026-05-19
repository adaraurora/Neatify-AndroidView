package com.example.neatify.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityFormServiceBinding
import com.example.neatify.model.ServiceRequest
import com.example.neatify.model.ServiceSingleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormServiceBinding

    private var serviceId: Int = 0
    private var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceId = intent.getIntExtra("SERVICE_ID", 0)
        isEditMode = serviceId != 0

        binding.tvBack.setOnClickListener {
            finish()
        }

        if (isEditMode) {
            binding.tvTitle.text = "Edit Layanan"
            binding.btnSave.text = "Update"

            binding.etNama.setText(intent.getStringExtra("NAMA") ?: "")
            binding.etDeskripsi.setText(intent.getStringExtra("DESKRIPSI") ?: "")
            binding.etHarga.setText(intent.getIntExtra("HARGA", 0).toString())
            binding.etSatuan.setText(intent.getStringExtra("SATUAN") ?: "")
            binding.etImage.setText(intent.getStringExtra("IMAGE") ?: "")
        } else {
            binding.tvTitle.text = "Tambah Layanan"
            binding.btnSave.text = "Simpan"
        }

        binding.btnSave.setOnClickListener {
            saveService()
        }
    }

    private fun saveService() {
        val nama = binding.etNama.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()
        val hargaText = binding.etHarga.text.toString().trim()
        val satuan = binding.etSatuan.text.toString().trim()
        val image = binding.etImage.text.toString().trim()

        if (nama.isEmpty()) {
            binding.etNama.error = "Nama layanan wajib diisi"
            return
        }

        if (deskripsi.isEmpty()) {
            binding.etDeskripsi.error = "Deskripsi wajib diisi"
            return
        }

        if (hargaText.isEmpty()) {
            binding.etHarga.error = "Harga wajib diisi"
            return
        }

        if (satuan.isEmpty()) {
            binding.etSatuan.error = "Satuan wajib diisi"
            return
        }

        val request = ServiceRequest(
            nama_layanan = nama,
            deskripsi = deskripsi,
            harga = hargaText.toInt(),
            satuan = satuan,
            image = if (image.isEmpty()) "default.png" else image
        )

        if (isEditMode) {
            updateService(request)
        } else {
            createService(request)
        }
    }

    private fun createService(request: ServiceRequest) {
        RetrofitClient.instance.createService(request)
            .enqueue(object : Callback<ServiceSingleResponse> {
                override fun onResponse(
                    call: Call<ServiceSingleResponse>,
                    response: Response<ServiceSingleResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@FormServiceActivity,
                            "Layanan berhasil ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@FormServiceActivity,
                            "Gagal menambah layanan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ServiceSingleResponse>, t: Throwable) {
                    Toast.makeText(
                        this@FormServiceActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun updateService(request: ServiceRequest) {
        RetrofitClient.instance.updateService(serviceId, request)
            .enqueue(object : Callback<ServiceSingleResponse> {
                override fun onResponse(
                    call: Call<ServiceSingleResponse>,
                    response: Response<ServiceSingleResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@FormServiceActivity,
                            "Layanan berhasil diupdate",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@FormServiceActivity,
                            "Gagal update layanan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ServiceSingleResponse>, t: Throwable) {
                    Toast.makeText(
                        this@FormServiceActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}