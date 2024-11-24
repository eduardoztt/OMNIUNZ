package com.example.omniunz

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.omniunz.databinding.ActivityAnaliseBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class AnaliseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnaliseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()

        binding.iconButtonBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.iconButtonMore.setOnClickListener {
          // startActivity(Intent(this,PerfilActivity::class.java))
        }
        lifecycleScope.launch {
            delay(2.seconds)
            startActivity(Intent(this@AnaliseActivity,NutricaoActivity::class.java))
        }

    }

    private fun setupView() {
        binding = ActivityAnaliseBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}