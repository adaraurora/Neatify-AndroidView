package com.example.neatify.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.neatify.activity.LoginActivity
import com.example.neatify.activity.WalletActivity
import com.example.neatify.api.RetrofitClient
import com.example.neatify.databinding.FragmentProfileBinding
import com.example.neatify.model.LoginResponse
import com.example.neatify.model.User
import com.example.neatify.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager

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

        loadProfile()

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
                            showProfile(user)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal mengambil profil",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal konek profil: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun showProfile(user: User) {
        binding.tvProfileName.text = user.name
        binding.tvProfilePhone.text = user.phone ?: "-"
        binding.tvProfileEmail.text = user.email ?: "-"
        binding.tvProfileAlamat.text = "Alamat: ${user.alamat ?: "-"}"
        binding.tvProfileSaldo.text = "Rp${formatRupiah(user.saldo ?: 0)}"
        binding.tvProfilePoin.text = "${user.poin ?: 0}"
    }

    private fun formatRupiah(value: Int): String {
        return "%,d".format(value).replace(",", ".")
    }

    override fun onResume() {
        super.onResume()

        if (_binding != null) {
            loadProfile()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}