package com.example.neatify.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neatify.R
import com.example.neatify.databinding.ItemOrderBinding
import com.example.neatify.model.Order

class AdminOrderAdapter(
    private val list: List<Order>,
    private val onClick: (Order) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvKodeOrder.text = "#${item.kode_order ?: "ORD-${item.id}"}"
        holder.binding.tvLayanan.text = "${item.layanan ?: "-"} · ${item.berat}kg"
        holder.binding.tvTotal.text = "Rp${formatRupiah(item.total_harga)}"
        holder.binding.tvEstimasi.text = "Alamat\n${item.alamat ?: "-"}"
        holder.binding.tvStatus.text = formatStatus(item.status)

        when (item.status) {
            "selesai" -> {
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_green)
                holder.binding.tvStatus.setTextColor(Color.parseColor("#2E7D32"))
            }

            "dibatalkan" -> {
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_red)
                holder.binding.tvStatus.setTextColor(Color.parseColor("#C62828"))
            }

            else -> {
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_blue)
                holder.binding.tvStatus.setTextColor(Color.parseColor("#064C9B"))
            }
        }

        holder.itemView.setOnClickListener {
            onClick(item)
        }
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