package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.neatify.adapter.SelectServiceAdapter
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityCreateOrderBinding
import com.example.neatify.model.Service
import com.example.neatify.model.ServiceResponse
import com.example.neatify.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateOrderBinding
    private lateinit var session: SessionManager

    private var selectedService: Service? = null
    private var berat: Double = 1.0
    private var estimasi: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.tvBack.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        setupBeratButton()
        prefillSavedAddress()
        loadServices()

        binding.btnBuatPesanan.setOnClickListener {
            goToPayment()
        }
    }

    private fun setupRecyclerView() {
        binding.rvSelectServices.layoutManager = GridLayoutManager(this, 2)
        binding.rvSelectServices.setHasFixedSize(false)
    }

    private fun prefillSavedAddress() {
        val savedAddress = session.getAddress()
        if (savedAddress.isNotBlank()) {
            binding.etAlamat.setText(savedAddress)
        }
    }

    private fun setupBeratButton() {
        updateBeratText()

        binding.btnPlus.setOnClickListener {
            if (berat < 30.0) {
                berat += 0.5
                updateBeratText()
                updateEstimasi()
            }
        }

        binding.btnMinus.setOnClickListener {
            if (berat > 1.0) {
                berat -= 0.5
                updateBeratText()
                updateEstimasi()
            }
        }
    }

    private fun updateBeratText() {
        binding.tvBerat.text = String.format("%.1f", berat)
    }

    private fun updateEstimasi() {
        val service = selectedService

        estimasi = if (service != null) {
            (service.harga * berat).toInt()
        } else {
            0
        }

        binding.tvEstimasi.text = "Rp${formatRupiah(estimasi)}"
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

                        if (services.isEmpty()) {
                            Toast.makeText(
                                this@CreateOrderActivity,
                                "Data layanan masih kosong",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }

                        binding.rvSelectServices.adapter = SelectServiceAdapter(services) { service ->
                            selectedService = service
                            updateEstimasi()

                            Toast.makeText(
                                this@CreateOrderActivity,
                                "${service.nama_layanan} dipilih",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@CreateOrderActivity,
                            "Gagal mengambil layanan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ServiceResponse>, t: Throwable) {
                    Toast.makeText(
                        this@CreateOrderActivity,
                        "Gagal konek layanan: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun goToPayment() {
        val service = selectedService
        val alamat = binding.etAlamat.text.toString().trim()
        val catatan = binding.etCatatan.text.toString().trim()

        if (service == null) {
            Toast.makeText(this, "Pilih layanan dulu, jangan asal nekat.", Toast.LENGTH_SHORT).show()
            return
        }

        if (alamat.isEmpty()) {
            binding.etAlamat.error = "Alamat wajib diisi"
            return
        }

        if (estimasi <= 0) {
            Toast.makeText(this, "Estimasi biaya belum valid", Toast.LENGTH_SHORT).show()
            return
        }

        session.saveAddress(alamat)

        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("USER_ID", session.getUserId())
        intent.putExtra("SERVICE_ID", service.id)
        intent.putExtra("SERVICE_NAME", service.nama_layanan)
        intent.putExtra("SERVICE_PRICE", service.harga)
        intent.putExtra("BERAT", berat)
        intent.putExtra("ALAMAT", alamat)
        intent.putExtra("CATATAN", catatan)
        intent.putExtra("ESTIMASI", estimasi)

        startActivity(intent)
    }

    private fun formatRupiah(value: Int): String {
        return "%,d".format(value).replace(",", ".")
    }
}