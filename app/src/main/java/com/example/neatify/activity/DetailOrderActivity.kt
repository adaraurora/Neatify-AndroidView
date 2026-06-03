package com.example.neatify.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.R
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityDetailOrderBinding
import com.example.neatify.model.Order
import com.example.neatify.model.OrderResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailOrderBinding
    private var orderId: Int = 0

    // Ganti nomor ini ke nomor admin/driver asli. Format wajib 62, bukan 08.
    private val adminWhatsappNumber = "6285329111850"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getIntExtra("ORDER_ID", 0)

        binding.tvBack.setOnClickListener {
            finish()
        }

        binding.btnHubungiDriver.setOnClickListener {
            openWhatsappAdmin()
        }

        if (orderId == 0) {
            Toast.makeText(this, "ID pesanan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadOrderDetail()
    }


    private fun openWhatsappAdmin() {
        val kodeOrder = runCatching { binding.tvKodeOrder.text.toString() }.getOrDefault("pesanan saya")
        val message = Uri.encode("Halo Admin Neatify, saya ingin menanyakan status $kodeOrder.")
        val url = "https://wa.me/$adminWhatsappNumber?text=$message"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            AlertDialog.Builder(this)
                .setTitle("WhatsApp tidak bisa dibuka")
                .setMessage("Tidak ada aplikasi yang bisa membuka WhatsApp/link browser di HP ini. Nomor admin: +$adminWhatsappNumber")
                .setPositiveButton("Oke", null)
                .show()
        } catch (e: Exception) {
            AlertDialog.Builder(this)
                .setTitle("Gagal membuka WhatsApp")
                .setMessage("Coba cek nomor admin atau aplikasi WhatsApp. Nomor admin: +$adminWhatsappNumber")
                .setPositiveButton("Oke", null)
                .show()
        }
    }

    private fun loadOrderDetail() {
        RetrofitClient.instance.getOrderDetail(orderId)
            .enqueue(object : Callback<OrderResponse> {

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val order = response.body()?.data

                        if (order != null) {
                            showOrder(order)
                        } else {
                            Toast.makeText(
                                this@DetailOrderActivity,
                                "Data pesanan kosong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@DetailOrderActivity,
                            "Gagal mengambil detail pesanan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    Toast.makeText(
                        this@DetailOrderActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun showOrder(order: Order) {
        val status = normalizeStatus(order.status)

        binding.tvKodeOrder.text = "#${order.kode_order ?: "ORD-${order.id}"}"
        binding.tvStatus.text = formatStatus(status)
        binding.tvEstimasi.text = "Estimasi selesai: ${order.estimasi_selesai ?: "-"}"

        binding.tvLayanan.text = "Layanan: ${order.layanan ?: "-"}"
        binding.tvBerat.text = "Berat: ${order.berat} kg"
        binding.tvAlamat.text = order.alamat ?: "-"
        binding.tvCatatan.text = "Catatan: ${order.catatan ?: "-"}"

        binding.tvOngkir.text = "Ongkir: Rp${formatRupiah(order.ongkir ?: 0)}"
        binding.tvDiskon.text = "Diskon: Rp${formatRupiah(order.diskon ?: 0)}"
        binding.tvMetodePembayaran.text = "Metode: ${formatPaymentMethod(order.metode_pembayaran)}"
        binding.tvStatusPembayaran.text =
            "Status Pembayaran: ${formatPaymentStatus(order.status_pembayaran)}"
        binding.tvTotal.text = "Rp${formatRupiah(order.total_harga)}"

        updateStatusStyle(status)
        updateProgress(status)
    }

    private fun updateStatusStyle(status: String?) {
        when (status) {
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

    private fun updateProgress(status: String?) {
        resetStep(binding.stepDijemput)
        resetStep(binding.stepDicuci)
        resetStep(binding.stepSetrika)
        resetStep(binding.stepDikirim)

        when (status) {
            "dijemput" -> {
                activeStep(binding.stepDijemput)
            }

            "dicuci" -> {
                activeStep(binding.stepDijemput)
                activeStep(binding.stepDicuci)
            }

            "setrika" -> {
                activeStep(binding.stepDijemput)
                activeStep(binding.stepDicuci)
                activeStep(binding.stepSetrika)
            }

            "dikirim", "selesai" -> {
                activeStep(binding.stepDijemput)
                activeStep(binding.stepDicuci)
                activeStep(binding.stepSetrika)
                activeStep(binding.stepDikirim)
            }

            else -> {
                activeStep(binding.stepDijemput)
            }
        }
    }

    private fun activeStep(textView: TextView) {
        textView.setTextColor(Color.parseColor("#064C9B"))
        textView.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun resetStep(textView: TextView) {
        textView.setTextColor(Color.parseColor("#C7D3E0"))
        textView.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    private fun normalizeStatus(status: String?): String {
        return status
            ?.lowercase()
            ?.trim()
            ?.replace("sedang dicuci", "dicuci")
            ?.replace("disetrika", "setrika")
            ?.replace("diantar", "dikirim")
            ?: ""
    }

    private fun formatStatus(status: String?): String {
        return when (status) {
            "dijemput" -> "🧺 Dijemput"
            "dicuci" -> "🫧 Sedang Dicuci"
            "setrika" -> "👕 Disetrika"
            "dikirim" -> "🚚 Dikirim"
            "selesai" -> "✅ Selesai"
            "dibatalkan" -> "❌ Dibatalkan"
            else -> "✨ Diproses"
        }
    }

    private fun formatPaymentStatus(status: String?): String {
        return when (status?.lowercase()?.trim()) {
            "belum_bayar" -> "Belum Bayar"
            "sudah_bayar" -> "Sudah Bayar"
            "paid" -> "Sudah Bayar"
            "unpaid" -> "Belum Bayar"
            else -> status ?: "-"
        }
    }

    private fun formatPaymentMethod(method: String?): String {
        return when (method?.lowercase()?.trim()) {
            "wallet" -> "Saldo Dompet"
            "transfer" -> "Transfer Bank"
            "cash" -> "Cash"
            "ewallet" -> "E-Wallet"
            else -> method ?: "-"
        }
    }

    private fun formatRupiah(value: Int): String {
        return "%,d".format(value).replace(",", ".")
    }
}