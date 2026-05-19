package com.example.neatify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neatify.databinding.ItemAdminUserBinding
import com.example.neatify.model.User

class AdminUserAdapter(
    private val list: List<User>,
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAdminUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvName.text = item.name
        holder.binding.tvPhone.text = item.phone ?: "-"
        holder.binding.tvEmail.text = item.email ?: "-"
        holder.binding.tvAlamat.text = "Alamat: ${item.alamat ?: "-"}"

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }
}