package com.example.neatify.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.widget.TextView
import com.example.neatify.activity.DetailOrderActivity
import com.example.neatify.model.Order
import com.example.neatify.model.OrderListResponse
import androidx.recyclerview.widget.GridLayoutManager
import com.example.neatify.adapter.ServiceAdapter
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.FragmentHomeBinding
import com.example.neatify.model.ServiceResponse
import com.example.neatify.utils.SessionManager
import com.example.neatify.activity.NotificationActivity
import com.example.neatify.activity.WalletActivity
import com.example.neatify.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        session = SessionManager(requireContext())

        binding.tvGreeting.text = "Selamat Pagi, ${session.getName()}"
        binding.tvHeadline.text = "Siap tampil rapi hari ini?"

        setupRecyclerView()
        loadServices()
        loadProfile()
        loadActiveOrder()

        binding.ivNotif.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        binding.tvTopUp.setOnClickListener {
            startActivity(Intent(requireContext(), WalletActivity::class.java))
        }

        binding.cardActiveOrder.visibility = View.VISIBLE
        binding.tvNoActiveOrder.visibility = View.GONE

        binding.tvActiveKodeOrder.text = "#TEST-123"
        binding.tvActiveOrderStatus.text = "TEST STATUS"
        binding.tvActiveOrderDesc.text = "TEST Layanan · 1.0kg"
        binding.tvActiveOrderTotal.text = "Rp99.000"

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvServices.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvServices.setHasFixedSize(false)
    }

    private fun loadServices() {
        RetrofitClient.instance.getServices()
            .enqueue(object : Callback<ServiceResponse> {

                override fun onResponse(
                    call: Call<ServiceResponse>,
                    response: Response<ServiceResponse>
                ) {
                    Log.d("SERVICE_API", "Code: ${response.code()}")
                    Log.d("SERVICE_API", "Body: ${response.body()}")
                    Log.d("SERVICE_API", "Error: ${response.errorBody()?.string()}")

                    if (response.isSuccessful) {
                        val services = response.body()?.data ?: emptyList()

                        Toast.makeText(
                            requireContext(),
                            "Jumlah layanan: ${services.size}",
                            Toast.LENGTH_SHORT
                        ).show()

                        binding.rvServices.adapter = ServiceAdapter(services) { service ->
                            Toast.makeText(
                                requireContext(),
                                "Pilih: ${service.nama_layanan}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal ambil layanan. Code: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ServiceResponse>, t: Throwable) {
                    Log.e("SERVICE_API", "Failure: ${t.message}", t)

                    Toast.makeText(
                        requireContext(),
                        "Gagal konek layanan: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadActiveOrder() {
        val userId = session.getUserId()

        RetrofitClient.instance.getUserOrders(userId)
            .enqueue(object : Callback<OrderListResponse> {

                override fun onResponse(
                    call: Call<OrderListResponse>,
                    response: Response<OrderListResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val orders = response.body()?.data ?: emptyList()

                        val activeOrder = orders.firstOrNull {
                            val status = it.status?.lowercase()?.trim()
                            status != "selesai" && status != "dibatalkan"
                        }

                        if (activeOrder != null) {
                            showActiveOrder(activeOrder)
                        } else {
                            hideActiveOrder()
                        }
                    } else {
                        hideActiveOrder()
                    }
                }

                override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                    hideActiveOrder()
                }
            })
    }

    private fun showActiveOrder(order: Order) {
        binding.cardActiveOrder.visibility = View.VISIBLE
        binding.tvNoActiveOrder.visibility = View.GONE

        binding.tvActiveKodeOrder.text = "#${order.kode_order ?: "ORD-${order.id}"}"
        binding.tvActiveOrderStatus.text = formatStatus(order.status)
        binding.tvActiveOrderDesc.text = "${order.layanan ?: "-"} · ${order.berat}kg"
        binding.tvActiveOrderTotal.text = "Rp${formatRupiah(order.total_harga)}"

        updateHomeProgress(order.status)

        binding.cardActiveOrder.setOnClickListener {
            val intent = Intent(requireContext(), DetailOrderActivity::class.java)
            intent.putExtra("ORDER_ID", order.id)
            startActivity(intent)
        }
    }

    private fun hideActiveOrder() {
        binding.cardActiveOrder.visibility = View.GONE
        binding.tvNoActiveOrder.visibility = View.VISIBLE
    }

    private fun updateHomeProgress(status: String?) {
        resetStep(binding.homeStepDijemput)
        resetStep(binding.homeStepDicuci)
        resetStep(binding.homeStepSetrika)
        resetStep(binding.homeStepDikirim)

        when (status) {
            "dijemput" -> {
                activeStep(binding.homeStepDijemput)
            }

            "dicuci" -> {
                activeStep(binding.homeStepDijemput)
                activeStep(binding.homeStepDicuci)
            }

            "setrika" -> {
                activeStep(binding.homeStepDijemput)
                activeStep(binding.homeStepDicuci)
                activeStep(binding.homeStepSetrika)
            }

            "dikirim", "selesai" -> {
                activeStep(binding.homeStepDijemput)
                activeStep(binding.homeStepDicuci)
                activeStep(binding.homeStepSetrika)
                activeStep(binding.homeStepDikirim)
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

    override fun onResume() {
        super.onResume()

        if (_binding != null) {
            loadProfile()
            loadActiveOrder()
        }
    }

    private fun loadProfile() {
        RetrofitClient.instance.getProfile(session.getUserId())
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val user = response.body()?.data

                        if (user != null) {
                            binding.tvSaldo.text = "Rp${formatRupiah(user.saldo ?: 0)}"
                            binding.tvPoin.text = "${user.poin ?: 0}"
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // diam aja, jangan semua error harus drama Toast
                }
            })
    }
}