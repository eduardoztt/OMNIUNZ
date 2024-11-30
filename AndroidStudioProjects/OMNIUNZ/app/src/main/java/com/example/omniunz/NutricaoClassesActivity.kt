package com.example.omniunz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angellira.app_1_eduardo.recyclerview.adapter.RecyclerViewHistorico
import com.example.omniunz.adapter.RecyclerViewAlimentos
import com.example.omniunz.databinding.ActivityNutricaoClassesBinding
import com.example.omniunz.model.Alimento

class NutricaoClassesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNutricaoClassesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()

        val alimentos = listOf(
            Alimento(
                name = "Frango Grelhado",
                proteina = "30",
                carboidrato = "5",
                gordura = "10",
                caloria = "250",
                image = "https://example.com/images/frango_grelhado.png"
            ),
            Alimento(
                name = "Peito de Peru",
                proteina = "25",
                carboidrato = "0",
                gordura = "3",
                caloria = "120",
                image = "https://example.com/images/peito_peru.png"
            )
        )


        recyclerView(alimentos)


    }

    private fun recyclerView(listAnalise: List<Alimento>) {
        Log.d("listResult", "listResult  $listAnalise")

        val adapter = RecyclerViewAlimentos(
            posts = listAnalise,
            onItemClickListener = { alimento ->
                val intent = Intent(this, NutricaoActivity::class.java)
                intent.putExtra("alimento", alimento)
                startActivity(intent)
            }
        )
        binding.recyclerview.adapter = adapter

    }

    private fun setupView() {
        binding = ActivityNutricaoClassesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}