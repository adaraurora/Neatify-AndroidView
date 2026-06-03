package com.example.neatify.fragment

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.neatify.R
import com.example.neatify.activity.LoginActivity
import com.example.neatify.activity.NotificationActivity
import com.example.neatify.activity.WalletActivity
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.FragmentProfileBinding
import com.example.neatify.model.LoginResponse
import com.example.neatify.model.User
import com.example.neatify.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        session = SessionManager(requireContext())

        binding.btnLogout.setOnClickListener {
            session.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.tvWalletMenu.setOnClickListener {
            startActivity(Intent(requireContext(), WalletActivity::class.java))
        }

        binding.tvNotifMenu.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        binding.tvSecurityMenu.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.tvHelpMenu.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
                ?.selectedItemId = R.id.nav_help
        }

        binding.tvProfileAlamat.setOnClickListener {
            showAddressDialog()
        }

        loadProfile()
        refreshSavedAddress()

        return binding.root
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
                            currentUser = user
                            showProfile(user)
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // sengaja diam, biar profil tidak cerewet kayak printer rusak
                }
            })
    }

    private fun showProfile(user: User) {
        binding.tvProfileName.text = user.name
        binding.tvProfilePhone.text = user.phone ?: "-"
        binding.tvProfileEmail.text = user.email ?: "-"

        if (session.getAddress().isEmpty() && !user.alamat.isNullOrBlank()) {
            session.saveAddress(user.alamat)
        }

        refreshSavedAddress()
        binding.tvProfileSaldo.text = "Rp${formatRupiah(user.saldo ?: 0)}"
        binding.tvProfilePoin.text = "${user.poin ?: 0}"
    }

    private fun refreshSavedAddress() {
        val address = session.getAddress()
        binding.tvProfileAlamat.text = if (address.isNotBlank()) {
            "📍  Alamat: $address  ›"
        } else {
            "📍  Tambah Alamat  ›"
        }
    }

    private fun showAddressDialog() {
        val input = EditText(requireContext())
        input.hint = "Contoh: Jl. Mawar No. 10, Purwokerto"
        input.setText(session.getAddress())
        input.minLines = 2
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        input.setPadding(24, 12, 24, 12)

        AlertDialog.Builder(requireContext())
            .setTitle("Alamat Saya")
            .setMessage("Alamat ini otomatis dipakai saat kamu membuat pesanan baru.")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val address = input.text.toString().trim()
                if (address.isNotEmpty()) {
                    session.saveAddress(address)
                    refreshSavedAddress()
                    showInfo("Alamat Tersimpan", "Alamat berhasil disimpan dan akan otomatis muncul di form pesanan.")
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        val wrapper = LinearLayout(requireContext())
        wrapper.orientation = LinearLayout.VERTICAL
        wrapper.setPadding(36, 8, 36, 0)

        val oldPass = EditText(requireContext())
        oldPass.hint = "Password lama"
        oldPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        val newPass = EditText(requireContext())
        newPass.hint = "Password baru"
        newPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        val confirmPass = EditText(requireContext())
        confirmPass.hint = "Konfirmasi password baru"
        confirmPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        wrapper.addView(oldPass)
        wrapper.addView(newPass)
        wrapper.addView(confirmPass)

        AlertDialog.Builder(requireContext())
            .setTitle("Ubah Kata Sandi")
            .setMessage("Untuk demo Android, password baru disimpan lokal di aplikasi.")
            .setView(wrapper)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val oldValue = oldPass.text.toString().trim()
                        val newValue = newPass.text.toString().trim()
                        val confirmValue = confirmPass.text.toString().trim()

                        when {
                            oldValue.isEmpty() -> oldPass.error = "Password lama wajib diisi"
                            newValue.length < 6 -> newPass.error = "Minimal 6 karakter"
                            newValue != confirmValue -> confirmPass.error = "Konfirmasi tidak sama"
                            else -> {
                                session.saveDemoPassword(newValue)
                                dismiss()
                                showInfo("Kata Sandi Tersimpan", "Password baru berhasil disimpan untuk demo aplikasi.")
                            }
                        }
                    }
                }
                show()
            }
    }

    private fun showInfo(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Oke", null)
            .show()
    }

    private fun formatRupiah(value: Int): String {
        return "%,d".format(value).replace(",", ".")
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadProfile()
            refreshSavedAddress()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
