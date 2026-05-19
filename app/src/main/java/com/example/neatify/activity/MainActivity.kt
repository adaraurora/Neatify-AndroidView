package com.example.neatify.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.neatify.R
import com.example.neatify.databinding.ActivityMainBinding
import com.example.neatify.fragment.HelpFragment
import com.example.neatify.fragment.HomeFragment
import com.example.neatify.fragment.OrdersFragment
import com.example.neatify.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.nav_order -> {
                    loadFragment(OrdersFragment())
                    true
                }

                R.id.nav_help -> {
                    loadFragment(HelpFragment())
                    true
                }

                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }

        binding.fabAddOrder.setOnClickListener {
            startActivity(Intent(this, CreateOrderActivity::class.java))
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}