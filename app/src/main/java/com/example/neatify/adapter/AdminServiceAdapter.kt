package com.example.neatify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neatify.databinding.ItemAdminServiceBinding
import com.example.neatify.model.Service

class AdminServiceAdapter(
    private val list: List<Service>,
    private val onEdit: (Service) -> Unit,
    private val onDelete: (Service) -> Unit
) : RecyclerView.Adapter<AdminServiceAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAdminServiceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminServiceBinding.inflate(
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
        holder.binding.tvDeskripsi.text = item.deskripsi ?: "-"
        holder.binding.tvHarga.text = "Rp${formatRupiah(item.harga)}/${item.satuan}"

        holder.binding.btnEdit.setOnClickListener {
            onEdit(item)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDelete(item)
        }
    }

    private fun formatRupiah(value: Int): String {
        return "%,d".format(value).replace(",", ".")
    }
}