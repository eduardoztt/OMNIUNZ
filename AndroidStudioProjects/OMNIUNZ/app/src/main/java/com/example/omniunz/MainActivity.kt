package com.example.omniunz

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.angellira.app_1_eduardo.preferences.PreferencesManager
import com.example.omniunz.databinding.ActivityMainBinding
import com.example.omniunz.grafic.CalorieProgressView
import com.example.omniunz.grafic.CustomMarkerView
import com.github.lzyzsd.circleprogress.ArcProgress
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        preferencesManager = PreferencesManager(this)
        auth = FirebaseAuth.getInstance()

        binding.username.text = preferencesManager.userName
        binding.userimage.load(preferencesManager.userImage)

        val storage = Firebase.storage
        val pickMedia =
            FindImage(storage)





        binding.userimage.setOnClickListener {
            Log.i("perfil", "Clicado")
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        binding.iconButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.buttonUser.setOnClickListener {
            Log.i("perfil", "Clicado")
            startActivity(Intent(this, PerfilActivity::class.java))
        }


        val calorieProgressView = findViewById<CalorieProgressView>(R.id.calorieProgressView)
        // Defina o progresso atual (percentual de 0 a 100)
        calorieProgressView.setProgress(56f)

        grafico()

    }



    private fun grafico() {
        val lineChart = findViewById<LineChart>(R.id.lineChart)

        // Configuração de dados do gráfico
        val entries = listOf(
            Entry(0f, 1000f),
            Entry(1f, 1200f),
            Entry(2f, 1500f),
            Entry(3f, 2000f),
            Entry(4f, 1800f),
            Entry(5f, 2200f),
            Entry(6f, 2500f)
        )

        val dataSet = LineDataSet(entries, "Consumo Diário")
        dataSet.color = Color.parseColor("#5C6BC0") // Cor da linha
        dataSet.valueTextColor = Color.BLACK // Cor dos textos dos valores
        dataSet.setCircleColor(Color.WHITE) // Cor dos círculos
        dataSet.circleHoleColor = Color.parseColor("#5C6BC0") // Cor do centro dos círculos
        dataSet.lineWidth = 2f // Espessura da linha
        dataSet.circleRadius = 5f // Tamanho dos círculos
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // Linha suavizada
        dataSet.setDrawFilled(true) // Habilitar preenchimento
        dataSet.fillColor = Color.parseColor("#E3F2FD") // Cor mais clara para o preenchimento
        dataSet.fillAlpha = 200 // Transparência do preenchimento

        // Remover os valores exibidos no meio da linha
        dataSet.setDrawValues(false) // Esta configuração desativa os valores no gráfico

        // Configuração do eixo X
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false) // Desabilitar linhas de grade
        xAxis.granularity = 1f // Intervalo entre os valores
        xAxis.textSize = 20f // Aumentar o tamanho do texto
        xAxis.textColor = Color.BLACK // Cor do texto
        xAxis.valueFormatter = object : ValueFormatter() {
            private val days = listOf("D", "S", "T", "Q", "Q", "S", "S")
            override fun getFormattedValue(value: Float): String {
                return days.getOrNull(value.toInt()) ?: value.toString()
            }
        }

        // Configuração do eixo Y (lado esquerdo)
        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.textColor = Color.BLACK // Cor dos números no eixo Y
        yAxisLeft.textSize = 20f // Aumentar o tamanho do texto
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.isEnabled = true// Mostrar linhas de grade
        lineChart.axisRight.isEnabled = false
        yAxisLeft.axisMinimum = 0f // Valor mínimo do eixo Y
        yAxisLeft.axisMaximum = 2500f // Valor máximo do eixo Y
        yAxisLeft.granularity = 500f


        val markerView = CustomMarkerView(this, R.layout.custom_marker)
        markerView.chartView = lineChart // Vincula o MarkerView ao gráfico
        lineChart.marker = markerView

        // Outras configurações
        lineChart.description.isEnabled = false // Remover a descrição do gráfico
        lineChart.legend.isEnabled = false // Desabilitar a legenda

        // Setando os dados no gráfico
        lineChart.data = LineData(dataSet)
        lineChart.invalidate() // Atualizar o gráfico
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


    private suspend fun downloadImage(downloadUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val inputStream = URL(downloadUrl).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun FindImage(
        storage: FirebaseStorage,
    ): ActivityResultLauncher<PickVisualMediaRequest> {

        val email = preferencesManager.userEmail
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")

                    val storageRef = storage.reference
                    val riversRef = storageRef.child("$email/${uri.lastPathSegment}")


                    val uploadTask = riversRef.putFile(uri)

                    try {
                        uploadTask.addOnFailureListener {
                            Log.e("PhotoPicker", "Upload falhou: ${it.message}")

                        }.addOnSuccessListener { _ ->
                            riversRef.downloadUrl.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val downloadUri = task.result.toString()

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val bitmap = downloadImage(downloadUri)
                                        if (bitmap != null) {
                                            val base64String = convertBitmapToBase64(bitmap)
                                            preferencesManager.image = base64String // Salva no SharedPreferences
                                            Log.d("PhotoPicker", "Imagem salva em Base64 no SharedPreferences ${preferencesManager.image}")
                                            startActivity(Intent(this@MainActivity,NutricaoClassesActivity::class.java))
                                        } else {
                                            Log.e("PhotoPicker", "Falha ao baixar ou processar a imagem")
                                        }
                                    }
                                    Log.d("PhotoPicker", "Download URL: $downloadUri")

                                } else {
                                    Log.e(
                                        "PhotoPicker",
                                        "Falha ao obter a URL de download: ${task.exception?.message}"
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            "É necessário esta conectado a Internet para atualizar seus dados",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        return pickMedia
    }

    private fun setupView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}