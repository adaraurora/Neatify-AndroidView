package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.R
import com.example.neatify.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private var currentPage = 0

    private val titles = arrayOf(
        "Laundry cepat\ntanpa ribet ✨",
        "Lacak pesanan\nreal-time ✨",
        "Siap tampil rapi\nsetiap hari ✨"
    )

    private val descriptions = arrayOf(
        "Pesan layanan laundry favoritmu dengan mudah dan praktis.",
        "Pantau setiap proses pesananmu, dari dijemput hingga diantar ke lokasi.",
        "Pakaian bersih, wangi, dan rapi. Pembayaran mudah, hidup lebih praktis."
    )

    private val images = intArrayOf(
        R.drawable.bg_splash_laundry,
        R.drawable.img_onboarding_tracking,
        R.drawable.img_onboarding_order
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showPage()

        binding.main.setOnClickListener {
            if (currentPage < titles.lastIndex) {
                currentPage++
                showPage()
            }
        }

        binding.btnNext.setOnClickListener {
            goToLogin()
        }
    }

    private fun showPage() {
        binding.imgOnboarding.setImageResource(images[currentPage])
        binding.tvTitle.text = titles[currentPage]
        binding.tvDesc.text = descriptions[currentPage]

        binding.btnNext.visibility =
            if (currentPage == titles.lastIndex) View.VISIBLE else View.GONE

        binding.dot1.setBackgroundResource(
            if (currentPage == 0) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
        )

        binding.dot2.setBackgroundResource(
            if (currentPage == 1) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
        )

        binding.dot3.setBackgroundResource(
            if (currentPage == 2) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
        )
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}