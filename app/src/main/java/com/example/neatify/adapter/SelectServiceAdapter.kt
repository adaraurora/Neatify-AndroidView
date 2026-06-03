package com.example.neatify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neatify.databinding.ItemSelectServiceBinding
import com.example.neatify.model.Service

class SelectServiceAdapter(
    private val list: List<Service>,
    private val onClick: (Service) -> Unit
) : RecyclerView.Adapter<SelectServiceAdapter.ViewHolder>() {

    private var selectedId: Int? = null

    inner class ViewHolder(val binding: ItemSelectServiceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSelectServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvNama.text = item.nama_layanan
        holder.binding.tvHarga.text = "Rp ${formatHarga(item.harga)}/${item.satuan}"
        holder.binding.tvIcon.text = getIcon(item.nama_layanan)

        if (selectedId == item.id) {
            holder.binding.cardService.alpha = 1f
            holder.binding.cardService.setBackgroundResource(com.example.neatify.R.drawable.bg_blue_light_card)
        } else {
            holder.binding.cardService.alpha = 1f
            holder.binding.cardService.setBackgroundResource(com.example.neatify.R.drawable.bg_card)
        }

        holder.itemView.setOnClickListener {
            selectedId = item.id
            notifyDataSetChanged()
            onClick(item)
        }
    }

    private fun formatHarga(harga: Int): String {
        return if (harga >= 1000) "${harga / 1000}k" else harga.toString()
    }

    private fun getIcon(nama: String): String {
        return when {
            nama.contains("Setrika", ignoreCase = true) -> "🧺"
            nama.contains("Sepatu", ignoreCase = true) -> "👟"
            nama.contains("Selimut", ignoreCase = true) -> "▤"
            nama.contains("Bedding", ignoreCase = true) -> "🛏️"
            nama.contains("Cuci", ignoreCase = true) -> "🫧"
            else -> "◎"
        }
    }
}