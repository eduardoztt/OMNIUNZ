package com.example.omniunz

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.omniunz.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()

        binding.iconButtonBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.configuracoes.setOnClickListener {
            startActivity(Intent(this,ContaActivity::class.java))
        }
        binding.notificacao.setOnClickListener {
            startActivity(Intent(this,NotificacoesActivity::class.java))
        }
        binding.suporte.setOnClickListener {
            startActivity(Intent(this,SuporteActivity::class.java))
        }
        binding.planos.setOnClickListener {
            startActivity(Intent(this,PlanosActivity::class.java))
        }




    }

    private fun setupView() {
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}