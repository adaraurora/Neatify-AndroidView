package com.example.neatify.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neatify.R
import com.example.neatify.databinding.ItemWalletTransactionBinding
import com.example.neatify.model.WalletTransaction

class WalletTransactionAdapter(
    private val list: List<WalletTransaction>
) : RecyclerView.Adapter<WalletTransactionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemWalletTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWalletTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvDescription.text = item.description
        holder.binding.tvDate.text = item.created_at ?: "-"

        if (item.type == "topup") {
            holder.binding.tvType.text = "Top Up"
            holder.binding.tvAmount.text = "+Rp${formatRupiah(item.amount)}"
            holder.binding.tvAmount.setTextColor(Color.parseColor("#2E7D32"))
            holder.binding.tvType.setBackgroundResource(R.drawable.bg_status_green)
            holder.binding.tvType.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            holder.binding.tvType.text = "Payment"
            holder.binding.tvAmount.text = "-Rp${formatRupiah(item.amount)}"
            holder.binding.tvAmount.setTextColor(Color.parseColor("#C62828"))
            holder.binding.tvType.setBackgroundResource(R.drawable.bg_status_red)
            holder.binding.tvType.setTextColor(Color.parseColor("#C62828"))
        }
    }

    private fun formatRupiah(value: Int): String {
        return "%,d".format(value).replace(",", ".")
    }
}