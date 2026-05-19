package com.example.neatify.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neatify.R
import com.example.neatify.databinding.ItemNotificationBinding
import com.example.neatify.model.Order

class NotificationAdapter(
    private val list: List<Order>,
    private val onClick: (Order) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        val kode = item.kode_order ?: "ORD-${item.id}"
        val statusText = formatStatus(item.status)

        holder.binding.tvTitle.text = "Update Pesanan"
        holder.binding.tvMessage.text = "Pesanan #$kode sekarang berstatus $statusText"
        holder.binding.tvStatus.text = statusText

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
}