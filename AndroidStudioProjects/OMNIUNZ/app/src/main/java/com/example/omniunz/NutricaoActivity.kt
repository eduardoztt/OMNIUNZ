package com.example.omniunz

import Historico
import android.content.Intent
import android.os.Bundle
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
import com.example.omniunz.databinding.ActivityNutricaoBinding
import com.example.omniunz.model.Alimento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class NutricaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNutricaoBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var preferencesManager: PreferencesManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        preferencesManager = PreferencesManager(this)
        database = FirebaseDatabase.getInstance()
        val alimento = intent.getSerializableExtra("alimento") as Alimento

        binding.carboidrato.text = alimento.carboidrato
        binding.proteina.text = alimento.proteinas
        binding.gordura.text = alimento.gorduras
        binding.valorNutricionalqtd.text = alimento.calorias
        binding.NameClase.text = alimento.nome
        binding.imageAlimento.load(preferencesManager.image) {
            listener(
                onSuccess = { _, _ ->
                    binding.progressBar2.visibility = GONE
                    binding.imageAlimento.visibility = VISIBLE
                },
                onError = { _, _ -> binding.progressBar2.visibility = GONE }
            )
        }



        saveAlimento(
            alimento.nome,
            alimento.proteinas,
            alimento.carboidrato,
            alimento.gorduras,
            alimento.calorias,
            preferencesManager.image
        )

        try {
            // Tente converter diretamente
            val proteinas = alimento.proteinas.replace(",", ".").toFloatOrNull() ?: 0f
            val carboidrato = alimento.carboidrato.replace(",", ".").toFloatOrNull() ?: 0f
            val gorduras = alimento.gorduras.replace(",", ".").toFloatOrNull() ?: 0f

            // Atualize as barras nutricionais
            binding.columnProteina.post {
                updateNutritionalBars(proteinas, carboidrato, gorduras)
            }
        } catch (e: Exception) {
            // Se ocorrer um erro, defina valores padrão
            val proteinas = alimento.proteinas.replace(",", ".").toFloatOrNull() ?: 0f
            val carboidrato = alimento.carboidrato.replace(",", ".").toFloatOrNull() ?: 0f
            val gorduras = alimento.gorduras.replace(",", ".").toFloatOrNull() ?: 0f

            // Atualize as barras nutricionais
            binding.columnProteina.post {
                updateNutritionalBars(proteinas, carboidrato, gorduras)
            }
        }




        binding.iconButtonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.iconButtonMore.setOnClickListener {
            // startActivity(Intent(this,PerfilActivity::class.java))
        }
        binding.buttonHistorico.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }

    }

    private fun saveAlimento(
        name: String,
        proteina: String,
        carboidrato: String,
        gordura: String,
        caloria: String,
        image: String
    ) {


        if (name.isEmpty() || proteina.isEmpty() || carboidrato.isEmpty() || gordura.isEmpty() || caloria.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        // Obter a data atual
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        val alimento = Historico(
            name = name,
            proteina = proteina,
            carboidrato = carboidrato,
            gordura = gordura,
            caloria = caloria,
            data = currentDate,
            image = image
        )

        val userId = preferencesManager.userUid
        if (userId != null) {
            val userRef = database.reference.child("users").child(userId).child("alimentos")
            val alimentoId = UUID.randomUUID().toString() // Gerando um ID único para o alimento

            // Salvando o alimento no Realtime Database
            userRef.child(alimentoId).setValue(alimento)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Alimento salvo com sucesso!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Erro ao salvar alimento.", Toast.LENGTH_SHORT).show()
                    }
                }
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
            layoutParamsProteina.width =
                (binding.columnProteina.parent as View).width * percProteina.toInt() / 100
            binding.columnProteina.layoutParams = layoutParamsProteina
            binding.columnProteina.requestLayout()


            val layoutParamsCarboidrato = binding.columnCarboidrato.layoutParams
            layoutParamsCarboidrato.width =
                (binding.columnCarboidrato.parent as View).width * percCarboidrato.toInt() / 100
            binding.columnCarboidrato.layoutParams = layoutParamsCarboidrato
            binding.columnCarboidrato.requestLayout()

            val layoutParamsGordura = binding.columnGordura.layoutParams
            layoutParamsGordura.width =
                (binding.columnGordura.parent as View).width * percGordura.toInt() / 100
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