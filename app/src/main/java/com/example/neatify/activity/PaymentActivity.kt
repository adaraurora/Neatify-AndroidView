package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.R
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.ActivityPaymentBinding
import com.example.neatify.model.OrderRequest
import com.example.neatify.model.OrderResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    private var userId: Int = 0
    private var serviceId: Int = 0
    private var serviceName: String = ""
    private var servicePrice: Int = 0
    private var berat: Double = 1.0
    private var alamat: String = ""
    private var catatan: String? = null

    private var subtotal: Int = 0
    private var ongkir: Int = 10000
    private var diskon: Int = 0
    private var total: Int = 0

    private var selectedPayment: String = "wallet"
    private var isCreatingOrder: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        validateIntentData()
        showData()
        setupPaymentMethod()
        updatePaymentStyle()

        binding.tvBack.setOnClickListener {
            finish()
        }

        binding.btnPay.setOnClickListener {
            createOrder()
        }
    }

    private fun getIntentData() {
        userId = intent.getIntExtra("USER_ID", 0)
        serviceId = intent.getIntExtra("SERVICE_ID", 0)
        serviceName = intent.getStringExtra("SERVICE_NAME") ?: "-"
        servicePrice = intent.getIntExtra("SERVICE_PRICE", 0)
        berat = intent.getDoubleExtra("BERAT", 1.0)
        alamat = intent.getStringExtra("ALAMAT") ?: ""
        catatan = intent.getStringExtra("CATATAN")

        subtotal = (servicePrice * berat).toInt()
        total = subtotal + ongkir - diskon
    }

    private fun validateIntentData() {
        if (userId == 0 || serviceId == 0 || alamat.isEmpty() || subtotal <= 0) {
            Toast.makeText(
                this,
                "Data pembayaran tidak valid",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun showData() {
        binding.tvServiceName.text = serviceName
        binding.tvBerat.text = "Berat: ${String.format("%.1f", berat)} kg"
        binding.tvAlamat.text = "Alamat: $alamat"

        binding.tvSubtotal.text = "Subtotal: Rp${formatRupiah(subtotal)}"
        binding.tvOngkir.text = "Ongkir: Rp${formatRupiah(ongkir)}"
        binding.tvDiskon.text = "Diskon: Rp${formatRupiah(diskon)}"
        binding.tvTotal.text = "Total: Rp${formatRupiah(total)}"
    }

    private fun setupPaymentMethod() {
        binding.paySaldo.setOnClickListener {
            selectedPayment = "wallet"
            updatePaymentStyle()
        }

        binding.payTransfer.setOnClickListener {
            selectedPayment = "transfer"
            updatePaymentStyle()
        }

        binding.payEwallet.setOnClickListener {
            selectedPayment = "ewallet"
            updatePaymentStyle()
        }
    }

    private fun updatePaymentStyle() {
        binding.paySaldo.setBackgroundResource(R.drawable.bg_card)
        binding.payTransfer.setBackgroundResource(R.drawable.bg_card)
        binding.payEwallet.setBackgroundResource(R.drawable.bg_card)

        binding.paySaldo.setTextColor(resources.getColor(R.color.text_dark, null))
        binding.payTransfer.setTextColor(resources.getColor(R.color.text_dark, null))
        binding.payEwallet.setTextColor(resources.getColor(R.color.text_dark, null))

        binding.paySaldo.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.payTransfer.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.payEwallet.setTypeface(null, android.graphics.Typeface.NORMAL)

        when (selectedPayment) {
            "wallet" -> {
                binding.paySaldo.setBackgroundResource(R.drawable.bg_blue_light_card)
                binding.paySaldo.setTextColor(resources.getColor(R.color.blue_primary, null))
                binding.paySaldo.setTypeface(null, android.graphics.Typeface.BOLD)
            }

            "transfer" -> {
                binding.payTransfer.setBackgroundResource(R.drawable.bg_blue_light_card)
                binding.payTransfer.setTextColor(resources.getColor(R.color.blue_primary, null))
                binding.payTransfer.setTypeface(null, android.graphics.Typeface.BOLD)
            }

            "ewallet" -> {
                binding.payEwallet.setBackgroundResource(R.drawable.bg_blue_light_card)
                binding.payEwallet.setTextColor(resources.getColor(R.color.blue_primary, null))
                binding.payEwallet.setTypeface(null, android.graphics.Typeface.BOLD)
            }
        }
    }

    private fun createOrder() {
        if (isCreatingOrder) return

        if (userId == 0 || serviceId == 0) {
            Toast.makeText(this, "Data pesanan tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        isCreatingOrder = true
        binding.btnPay.isEnabled = false
        binding.btnPay.text = "Memproses..."

        val request = OrderRequest(
            user_id = userId,
            service_id = serviceId,
            berat = berat,
            alamat = alamat,
            catatan = catatan,
            metode_pembayaran = selectedPayment
        )

        RetrofitClient.instance.createOrder(request)
            .enqueue(object : Callback<OrderResponse> {

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    isCreatingOrder = false
                    binding.btnPay.isEnabled = true
                    binding.btnPay.text = "Bayar dan Buat Pesanan"

                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(
                            this@PaymentActivity,
                            "Pesanan berhasil dibuat",
                            Toast.LENGTH_SHORT
                        ).show()

                        val orderId = response.body()?.data?.id ?: 0

                        if (orderId != 0) {
                            val intent = Intent(this@PaymentActivity, DetailOrderActivity::class.java)
                            intent.putExtra("ORDER_ID", orderId)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this@PaymentActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val errorText = response.errorBody()?.string()
                        val message = response.body()?.message ?: errorText ?: "Gagal membuat pesanan"

                        Toast.makeText(
                            this@PaymentActivity,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    isCreatingOrder = false
                    binding.btnPay.isEnabled = true
                    binding.btnPay.text = "Bayar dan Buat Pesanan"

                    Toast.makeText(
                        this@PaymentActivity,
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