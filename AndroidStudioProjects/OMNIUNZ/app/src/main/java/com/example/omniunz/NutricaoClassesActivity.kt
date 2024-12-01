package com.example.omniunz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.angellira.app_1_eduardo.preferences.PreferencesManager
import com.angellira.app_1_eduardo.recyclerview.adapter.RecyclerViewHistorico
import com.example.omniunz.adapter.RecyclerViewAlimentos
import com.example.omniunz.databinding.ActivityNutricaoClassesBinding
import com.example.omniunz.model.Alimento
import com.google.firebase.database.FirebaseDatabase

class NutricaoClassesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNutricaoClassesBinding
    private lateinit var preferencesManager: PreferencesManager
    private val database = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        preferencesManager = PreferencesManager(this)

        binding.imageAlimento.load(preferencesManager.image) {
            listener(
                onSuccess = { _, _ -> binding.progressBar2.visibility = GONE
                            binding.imageAlimento.visibility = VISIBLE},
                onError = { _, _ -> binding.progressBar2.visibility = GONE }
            )
        }

        binding.iconButtonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        getAlimentos("Meat")

    }

    private fun getAlimentos(classe: String) {
        val ref = database.child("API/API/").child(classe) // Classe é passada dinamicamente

        ref.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val listaDeAlimentos = mutableListOf<Alimento>()

                    for (child in snapshot.children) {
                        val alimento = child.getValue(Alimento::class.java)
                        alimento?.let {
                            listaDeAlimentos.add(it)
                        }
                    }

                    recyclerView(listaDeAlimentos)
                } else {
                    Log.i("ALIMENTOS", "Nenhum dado encontrado para a classe $classe.")
                }
            }
            .addOnFailureListener { exception ->
                Log.i("ERRO", "Erro ao buscar dados da classe $classe: ${exception.message}")
                Toast.makeText(this, "Erro ao buscar os dados.", Toast.LENGTH_SHORT).show()
            }
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