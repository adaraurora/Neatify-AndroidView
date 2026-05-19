package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.neatify.R
import com.example.neatify.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private var currentPage = 0

    private val titles = arrayOf(
        "Laundry Bersih,\nTanpa Repot",
        "Laundry Cepat\nTanpa Antri",
        "Lacak Pesanan\nSecara Real-time"
    )

    private val descriptions = arrayOf(
        "Layanan laundry rapi dan praktis untuk kebutuhan harianmu.",
        "Pesan layanan laundry kapan saja dan kami yang urus sisanya.",
        "Pantau status cucianmu secara langsung hingga selesai."
    )

    private val images = intArrayOf(
        R.drawable.bg_splash_laundry,
        R.drawable.img_onboarding_order,
        R.drawable.img_onboarding_tracking
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showPage()

        binding.btnNext.setOnClickListener {
            if (currentPage < titles.lastIndex) {
                currentPage++
                showPage()
            } else {
                goToLogin()
            }
        }

        binding.tvSkip.setOnClickListener {
            goToLogin()
        }
    }

    private fun showPage() {
        binding.imgOnboarding.setImageResource(images[currentPage])
        binding.tvTitle.text = titles[currentPage]
        binding.tvDesc.text = descriptions[currentPage]

        binding.btnNext.text =
            if (currentPage == titles.lastIndex) "Mulai Sekarang" else "Lanjut"

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