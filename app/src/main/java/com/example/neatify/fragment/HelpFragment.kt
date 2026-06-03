package com.example.neatify.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.neatify.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private val adminWhatsappNumber = "6285329111850"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)

        binding.itemHelpOrder.setOnClickListener {
            showPopup(
                "Cara Membuat Pesanan",
                "Tekan tombol + di tengah bawah, pilih layanan, isi berat cucian dan alamat, lalu lanjut ke pembayaran."
            )
        }

        binding.itemHelpPayment.setOnClickListener {
            showPopup(
                "Metode Pembayaran",
                "Kamu bisa memilih metode pembayaran di halaman pembayaran. Jika memakai dompet, pastikan saldo cukup."
            )
        }

        binding.itemHelpDelivery.setOnClickListener {
            showPopup(
                "Pengambilan & Pengiriman",
                "Pesanan akan diproses bertahap: dijemput, dicuci, disetrika, lalu dikirim. Statusnya bisa dilihat di halaman detail pesanan."
            )
        }

        binding.itemHelpCancel.setOnClickListener {
            showPopup(
                "Pembatalan Pesanan",
                "Pesanan dapat dibatalkan sebelum proses berjalan jauh. Jika sudah diproses, hubungi admin dulu."
            )
        }

        binding.itemHelpRefund.setOnClickListener {
            showPopup(
                "Refund & Pengembalian Dana",
                "Jika pembayaran bermasalah atau pesanan dibatalkan, refund dapat diproses ke saldo dompet sesuai kebijakan admin."
            )
        }

        binding.btnChatAdmin.setOnClickListener {
            openWhatsApp(
                "Halo Admin Neatify, saya butuh bantuan terkait aplikasi Neatify."
            )
        }

        return binding.root
    }

    private fun showPopup(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Oke", null)
            .show()
    }

    private fun openWhatsApp(message: String) {
        val encodedMessage = Uri.encode(message)
        val url = "https://wa.me/$adminWhatsappNumber?text=$encodedMessage"

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showPopup(
                "WhatsApp tidak bisa dibuka",
                "Pastikan WhatsApp atau browser tersedia di perangkat ini."
            )
        } catch (e: Exception) {
            showPopup(
                "Gagal membuka WhatsApp",
                "Terjadi kendala saat membuka chat admin."
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
