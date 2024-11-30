package com.example.omniunz

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.omniunz.databinding.ActivityNutricaoBinding
import com.example.omniunz.model.Alimento

class NutricaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNutricaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        val alimento = intent.getSerializableExtra("alimento") as Alimento

        binding.carboidrato.text = alimento.carboidrato
        binding.proteina.text = alimento.proteina
        binding.gordura.text = alimento.gordura




        binding.columnProteina.post {
            updateNutritionalBars(
                alimento.proteina.toFloat(),
                alimento.carboidrato.toFloat(),
                alimento.gordura.toFloat()
            )
        }



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


    private fun updateNutritionalBars(proteina: Float, carboidrato: Float, gordura: Float) {
        val total = proteina + carboidrato + gordura

        // Calcula as porcentagens
        val percProteina = (proteina / total) * 100
        val percCarboidrato = (carboidrato / total) * 100
        val percGordura = (gordura / total) * 100

        // Ajusta as larguras proporcionalmente
        val layoutParamsProteina = binding.columnProteina.layoutParams
        layoutParamsProteina.width = (binding.columnProteina.parent as View).width * percProteina.toInt() / 100
        binding.columnProteina.layoutParams = layoutParamsProteina
        binding.columnProteina.requestLayout()


        val layoutParamsCarboidrato = binding.columnCarboidrato.layoutParams
        layoutParamsCarboidrato.width = (binding.columnCarboidrato.parent as View).width * percCarboidrato.toInt() / 100
        binding.columnCarboidrato.layoutParams = layoutParamsCarboidrato
        binding.columnCarboidrato.requestLayout()

        val layoutParamsGordura = binding.columnGordura.layoutParams
        layoutParamsGordura.width = (binding.columnGordura.parent as View).width * percGordura.toInt() / 100
        binding.columnGordura.layoutParams = layoutParamsGordura
        binding.columnGordura.requestLayout()
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