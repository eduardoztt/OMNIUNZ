package com.example.omniunz

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.omniunz.databinding.ActivityNutricaoBinding

class NutricaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNutricaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()


        binding.iconButtonBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.iconButtonMore.setOnClickListener {
            // startActivity(Intent(this,PerfilActivity::class.java))
        }
        binding.buttonHistorico.setOnClickListener {
            startActivity(Intent(this,HistoricoActivity::class.java))
        }

    }

    private fun setupView() {
        binding = ActivityNutricaoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}