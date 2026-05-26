package com.example.neatify.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neatify.R
import com.example.neatify.activity.DetailOrderActivity
import com.example.neatify.adapter.OrderAdapter
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.FragmentOrdersBinding
import com.example.neatify.model.Order
import com.example.neatify.model.OrderListResponse
import com.example.neatify.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager

    private var allOrders: List<Order> = emptyList()
    private var selectedTab: String = "aktif"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)

        session = SessionManager(requireContext())

        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.setHasFixedSize(false)

        setupTabs()
        updateTabStyle()
        loadOrders()

        return binding.root
    }

    private fun setupTabs() {
        binding.tabAktif.setOnClickListener {
            selectedTab = "aktif"
            updateTabStyle()
            filterOrders()
        }

        binding.tabSelesai.setOnClickListener {
            selectedTab = "selesai"
            updateTabStyle()
            filterOrders()
        }

        binding.tabDibatalkan.setOnClickListener {
            selectedTab = "dibatalkan"
            updateTabStyle()
            filterOrders()
        }
    }

    private fun loadOrders() {
        RetrofitClient.instance.getUserOrders(session.getUserId())
            .enqueue(object : Callback<OrderListResponse> {

                override fun onResponse(
                    call: Call<OrderListResponse>,
                    response: Response<OrderListResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        allOrders = response.body()?.data ?: emptyList()
                        filterOrders()
                    } else {
                        showEmpty("Gagal mengambil pesanan")
                    }
                }

                override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal konek pesanan: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    showEmpty("Belum bisa memuat pesanan.")
                }
            })
    }

    private fun filterOrders() {
        val filtered = when (selectedTab) {
            "aktif" -> allOrders.filter {
                val status = normalizeStatus(it.status)
                status != "selesai" && status != "dibatalkan"
            }

            "selesai" -> allOrders.filter {
                normalizeStatus(it.status) == "selesai"
            }

            "dibatalkan" -> allOrders.filter {
                normalizeStatus(it.status) == "dibatalkan"
            }

            else -> allOrders
        }

        if (filtered.isEmpty()) {
            showEmpty("Belum ada pesanan di kategori ini.")
        } else {
            binding.rvOrders.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE

            binding.rvOrders.adapter = OrderAdapter(filtered) { order ->
                val intent = Intent(requireContext(), DetailOrderActivity::class.java)
                intent.putExtra("ORDER_ID", order.id)
                startActivity(intent)
            }
        }
    }

    private fun showEmpty(message: String) {
        binding.rvOrders.visibility = View.GONE
        binding.tvEmpty.visibility = View.VISIBLE
        binding.tvEmpty.text = message
    }

    private fun updateTabStyle() {
        setTabInactive(binding.tabAktif)
        setTabInactive(binding.tabSelesai)
        setTabInactive(binding.tabDibatalkan)

        when (selectedTab) {
            "aktif" -> setTabActive(binding.tabAktif)
            "selesai" -> setTabActive(binding.tabSelesai)
            "dibatalkan" -> setTabActive(binding.tabDibatalkan)
        }
    }

    private fun setTabActive(tab: TextView) {
        tab.setBackgroundResource(R.drawable.bg_tab_active)
        tab.setTextColor(resources.getColor(R.color.white, null))
        tab.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun setTabInactive(tab: TextView) {
        tab.setBackgroundResource(R.drawable.bg_tab_inactive)
        tab.setTextColor(resources.getColor(R.color.text_gray, null))
        tab.setTypeface(null, android.graphics.Typeface.NORMAL)
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

    override fun onResume() {
        super.onResume()

        if (_binding != null) {
            loadOrders()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}