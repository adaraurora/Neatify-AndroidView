package com.example.neatify.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.neatify.R
import com.example.neatify.activity.DetailOrderActivity
import com.example.neatify.activity.NotificationActivity
import com.example.neatify.activity.WalletActivity
import com.example.neatify.adapter.ServiceAdapter
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.FragmentHomeBinding
import com.example.neatify.model.LoginResponse
import com.example.neatify.model.Order
import com.example.neatify.model.OrderListResponse
import com.example.neatify.model.ServiceResponse
import com.example.neatify.utils.SessionManager
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

        setupGreeting()
        setupClickListener()
        setupRecyclerView()

        loadProfile()
        loadServices()
        loadActiveOrder()

        return binding.root
    }

    private fun setupGreeting() {
        val name = session.getName().ifEmpty { "Adara" }
        binding.tvGreeting.text = "Selamat Pagi, $name!"
        binding.tvHeadline.text = "Siap tampil rapi hari ini?"
    }

    private fun setupClickListener() {
        binding.ivNotif.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        binding.tvTopUp.setOnClickListener {
            startActivity(Intent(requireContext(), WalletActivity::class.java))
        }

        binding.tvRiwayat.setOnClickListener {
            startActivity(Intent(requireContext(), WalletActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        binding.rvServices.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvServices.setHasFixedSize(false)
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

                        binding.rvServices.adapter = ServiceAdapter(services) { service ->
                            Toast.makeText(
                                requireContext(),
                                "${service.nama_layanan} dipilih",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal mengambil layanan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ServiceResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal konek layanan: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun loadProfile() {
        val userId = session.getUserId()

        RetrofitClient.instance.getProfile(userId)
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
                    // Sengaja tidak pakai Toast biar home tidak cerewet.
                }
            })
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
        binding.tvActiveOrderDesc.text = "${order.layanan ?: "Laundry"} · ${order.berat}kg"
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

        when (status?.lowercase()?.trim()) {
            "dijemput" -> {
                activeStep(binding.homeStepDijemput)
            }

            "dicuci", "sedang dicuci" -> {
                activeStep(binding.homeStepDijemput)
                activeStep(binding.homeStepDicuci)
            }

            "setrika", "disetrika" -> {
                activeStep(binding.homeStepDijemput)
                activeStep(binding.homeStepDicuci)
                activeStep(binding.homeStepSetrika)
            }

            "dikirim", "diantar", "selesai" -> {
                activeStep(binding.homeStepDijemput)
                activeStep(binding.homeStepDicuci)
                activeStep(binding.homeStepSetrika)
                activeStep(binding.homeStepDikirim)
            }

            else -> {
                activeStep(binding.homeStepDijemput)
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
        return when (status?.lowercase()?.trim()) {
            "dijemput" -> "Dijemput"
            "dicuci", "sedang dicuci" -> "Sedang Dicuci"
            "setrika", "disetrika" -> "Disetrika"
            "dikirim", "diantar" -> "Dikirim"
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}