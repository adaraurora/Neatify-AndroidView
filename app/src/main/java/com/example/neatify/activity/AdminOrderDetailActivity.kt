package com.example.neatify.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.R
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityAdminOrderDetailBinding
import com.example.neatify.model.Order
import com.example.neatify.model.OrderResponse
import com.example.neatify.model.UpdateStatusRequest
import com.example.neatify.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminOrderDetailBinding
    private lateinit var session: SessionManager

    private var orderId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        orderId = intent.getIntExtra("ORDER_ID", 0)

        binding.tvBack.setOnClickListener {
            finish()
        }

        binding.btnDijemput.setOnClickListener { updateStatus("dijemput") }
        binding.btnDicuci.setOnClickListener { updateStatus("dicuci") }
        binding.btnSetrika.setOnClickListener { updateStatus("setrika") }
        binding.btnDikirim.setOnClickListener { updateStatus("dikirim") }
        binding.btnSelesai.setOnClickListener { updateStatus("selesai") }
        binding.btnDibatalkan.setOnClickListener { updateStatus("dibatalkan") }

        if (orderId == 0) {
            Toast.makeText(this, "ID pesanan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadDetail()
    }

    private fun loadDetail() {
        RetrofitClient.instance.getOrderDetail(orderId)
            .enqueue(object : Callback<OrderResponse> {

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val order = response.body()?.data
                        if (order != null) {
                            showDetail(order)
                        }
                    } else {
                        Toast.makeText(
                            this@AdminOrderDetailActivity,
                            "Gagal mengambil detail",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminOrderDetailActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun showDetail(order: Order) {
        binding.tvKodeOrder.text = "#${order.kode_order ?: "ORD-${order.id}"}"
        binding.tvStatus.text = formatStatus(order.status)

        binding.tvDetail.text =
            "Layanan: ${order.layanan ?: "-"}\n" +
                    "Berat: ${order.berat} kg\n" +
                    "Alamat: ${order.alamat ?: "-"}\n" +
                    "Catatan: ${order.catatan ?: "-"}\n" +
                    "Total: Rp${formatRupiah(order.total_harga)}\n" +
                    "Pembayaran: ${order.metode_pembayaran ?: "-"}"

        when (order.status) {
            "selesai" -> {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_green)
                binding.tvStatus.setTextColor(Color.parseColor("#2E7D32"))
            }

            "dibatalkan" -> {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_red)
                binding.tvStatus.setTextColor(Color.parseColor("#C62828"))
            }

            else -> {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_blue)
                binding.tvStatus.setTextColor(Color.parseColor("#064C9B"))
            }
        }
    }

    private fun updateStatus(status: String) {
        val request = UpdateStatusRequest(
            admin_id = session.getUserId(),
            status = status
        )

        RetrofitClient.instance.updateOrderStatus(orderId, request)
            .enqueue(object : Callback<OrderResponse> {

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(
                            this@AdminOrderDetailActivity,
                            "Status berhasil diubah",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadDetail()
                    } else {
                        Toast.makeText(
                            this@AdminOrderDetailActivity,
                            "Gagal ubah status. Pastikan akun ini admin.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminOrderDetailActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun formatStatus(status: String?): String {
        return when (status) {
            "dijemput" -> "Dijemput"
            "dicuci" -> "Sedang Dicuci"
            "setrika" -> "Disetrika"
            "dikirim" -> "Dikirim"
            "selesai" -> "Selesai"
            "dibatalkan" -> "Dibatalkan"
            else -> "Diproses"
        }
    }

    private fun formatRupiah(value: Int): String {
        return "%,d".format(value).replace(",", ".")
    }
}