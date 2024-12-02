package com.example.omniunz

import Historico
import ImageRequest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import coil.load
import com.angellira.app_1_eduardo.preferences.PreferencesManager
import com.example.omniunz.databinding.ActivityMainBinding
import com.example.omniunz.grafic.CalorieProgressView
import com.example.omniunz.grafic.CustomMarkerView
import com.example.omniunz.network.ApiAlimento
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.net.URL
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private val dia1 = mutableListOf<Historico>()
    private val dia2 = mutableListOf<Historico>()
    private val dia3 = mutableListOf<Historico>()
    private val dia4 = mutableListOf<Historico>()
    private val dia5 = mutableListOf<Historico>()
    private val dia6 = mutableListOf<Historico>()
    private val dia7 = mutableListOf<Historico>()
    //variavesis global para mostra o grafico com os dados pegos do firebase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        preferencesManager = PreferencesManager(this)
        auth = FirebaseAuth.getInstance()
        val storage = Firebase.storage
        val pickMedia =
            FindImage(storage)

        mostrarDadosUser()


        filtrarHistoricoPorSemanaAtual()


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
        binding.buttonFood.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }

        //alterar meta
        binding.calorieProgressView.setOnClickListener {
            newMeta()
        }
        binding.buttonAdicionarMeta.setOnClickListener {
            newMeta()
        }

    }

    private fun mostrarDadosUser() {
        binding.username.text = preferencesManager.userName
        if (preferencesManager.userImage != null && preferencesManager.userImage.isNotEmpty() && preferencesManager.userImage != "null") {
            binding.userimage.load(preferencesManager.userImage)
        }
        ifElseMeta()
    }

    private fun ifElseMeta() {
        if (preferencesManager.userMeta != 0) {
            binding.metaTotalCalories.text = "${preferencesManager.userMeta.toString()} Kcal"
            binding.buttonAdicionarMeta.visibility = GONE
            binding.relativeLayout.visibility = VISIBLE
        } else {
            binding.buttonAdicionarMeta.visibility = VISIBLE
            binding.relativeLayout.visibility = GONE
        }
    }

    private fun filtrarHistoricoPorSemanaAtual() {
        val formatoData = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val hoje = LocalDate.now()


        val ultimos7Dias = (0..6).map { hoje.minusDays(it.toLong()).format(formatoData) }.reversed()

        var chamadasConcluidas = 0

        for ((index, dia) in ultimos7Dias.withIndex()) {
            filtrarHistoricoPorData(dia) { alimentosFiltrados ->
                when (index) {
                    0 -> dia1.addAll(alimentosFiltrados)
                    1 -> dia2.addAll(alimentosFiltrados)
                    2 -> dia3.addAll(alimentosFiltrados)
                    3 -> dia4.addAll(alimentosFiltrados)
                    4 -> dia5.addAll(alimentosFiltrados)
                    5 -> dia6.addAll(alimentosFiltrados)
                    6 -> dia7.addAll(alimentosFiltrados)
                }

                Log.i("$dia", "$alimentosFiltrados")

                chamadasConcluidas++

                if (chamadasConcluidas == 7) {
                    grafico(
                        dia1,
                        dia2,
                        dia3,
                        dia4,
                        dia5,
                        dia6,
                        dia7
                    )
                }
            }
        }
    }


    private fun filtrarHistoricoPorData(
        dataSelecionada: String,
        callback: (List<Historico>) -> Unit
    ) {
        val userId = preferencesManager.userUid
        if (userId != null) {
            val userRef = database.reference.child("users").child(userId).child("alimentos")

            // Comparando diretamente com a data como String
            userRef.orderByChild("data").equalTo(dataSelecionada)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val alimentosFiltrados = mutableListOf<Historico>()

                        for (childSnapshot in snapshot.children) {
                            val alimento = childSnapshot.getValue(Historico::class.java)
                            alimento?.let {
                                // A comparação é feita diretamente com a data armazenada no Firebase
                                if (it.data == dataSelecionada) {
                                    alimentosFiltrados.add(it)
                                }
                            }
                        }

                        callback(alimentosFiltrados)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@MainActivity,
                            "Erro ao carregar Dados.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }


    private fun newMeta() {

        //chama a função para criar o layout do EditText
        val (textInputLayout, editText) = layoutEditText("")


        //adiciona o EditText ao layout do Material Desing
        textInputLayout.addView(editText)

        //cria o linearLayout
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(142, 60, 142, 0) // adiciona um espaço se necessario
            addView(textInputLayout) // adiciona o layout do Material Desing a um LinearLayout
        }

        //chama a função para criar o dialog
        val dialog = cardNewMeta(layout, editText)

        //configuração do botão de enviar do dialog
        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setTextColor(android.graphics.Color.BLACK)//deixa a cor do botão preta

            //função para atualizar se o botão esta habilitado ou desabilitato
            fun updateButtonVisibility() {
                if (editText.text.toString().trim().isNotEmpty()) {
                    button.isEnabled = true
                } else {
                    button.isEnabled = false
                }
            }
            //chama a função de atualizar o estado do botão ao mudar o texto
            editText.doOnTextChanged { _, _, _, _ ->
                updateButtonVisibility()
            }
            //inicia o botão
            updateButtonVisibility()
        }
        dialog.show()
    }

    private fun layoutEditText(texto: String): Pair<TextInputLayout, TextInputEditText> {
        //Adiciona um layout para o EditText do Material Desingn 3
        val textInputLayout = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            hint = texto
            boxBackgroundMode =
                TextInputLayout.BOX_BACKGROUND_OUTLINE // Adiciona as Bordas ao redor do EditText, sem isso fica como um EditText Normal
            boxStrokeColor =
                ContextCompat.getColor(context, R.color.Button) // seleciona a cor da borda
            setBoxCornerRadii(
                12f, 12f, 12f, 12f
            ) // arredondamento da borda
            boxStrokeWidth = 2 // largura da borda

        }

        //Cria um EditText do Material Desingn 3
        val editText = TextInputEditText(this).apply {

            val heightInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 70f, resources.displayMetrics
            ).toInt()

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, heightInPx
            )
            setTextColor(
                ContextCompat.getColor(
                    context, R.color.black
                )
            ) //cor do texto de email

            textSize = 22f // altera o tamanho das letras dentro do EditText
            isSingleLine = true
            setHorizontallyScrolling(true)
            background =
                null // tira  a borda apenas em baixo que vem normal, para deixar a borda ao redor do EditText inteiro
            setPadding(40, 20, 20, 20) // deixar um espaço entre o texto e borda


            //filtro de caracteres
            filters = arrayOf(InputFilter.LengthFilter(6), InputFilter { source, _, _, _, _, _ ->

                //não deixa quebra linhas e nem dar espaços
                if (source.matches(Regex("[0-9]*"))) {
                    null //aceita
                } else {
                    "" //bloqueia
                }
            })
        }
        return Pair(textInputLayout, editText)
    }


    private fun cardNewMeta(
        layout: LinearLayout, editText: TextInputEditText
    ): AlertDialog {
        // Configurando o título customizado
        val customTitle = TextView(this).apply {
            text = "Digite uma nova meta"
            textSize = 22f // Tamanho do texto
            setTextColor(ContextCompat.getColor(context, R.color.black)) // Cor do texto
            setPadding(20, 100, 20, 20) // Espaçamento interno
            gravity = Gravity.CENTER // Centraliza o título
        }

        lateinit var dialog: AlertDialog


        // Criando o botão "Enviar"
        val positiveButton = Button(this).apply {
            text = "Alterar"
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.white)) // Cor do texto
            setBackgroundColor(ContextCompat.getColor(context, R.color.Button)) // Cor de fundo
            setPadding(80, 20, 80, 20) // Padding do botão

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 90f // Tamanho do raio da borda
                setColor(ContextCompat.getColor(context, R.color.Button)) // Cor de fundo
            }
            background = drawable
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER // Centraliza o botão no layout
                setMargins(0, 70, 0, 70)
            }

            //codigo para o botao alterar do card
            setOnClickListener {
                val text = editText.text.toString().toIntOrNull()
                if (text != null) {
                    try {
                        updateMetaDiariaCalorias(preferencesManager.userUid, text)
                        dialog.dismiss()
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Erro ao atualizar a meta: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(context, "Digite um valor válido", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Adiciona o botão ao layout fornecido
        layout.addView(positiveButton)

        // Criando o diálogo
        dialog = MaterialAlertDialogBuilder(this)
            .setCustomTitle(customTitle) // Define o título customizado
            .setView(layout) // Define o layout customizado com EditText e botão
            .create()

        return dialog
    }


    private fun updateMetaDiariaCalorias(uid: String, novaMeta: Int) {
        database.reference.child("users").child(uid).child("metaDiaria").setValue(novaMeta)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Meta diária de calorias atualizada com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    preferencesManager.userMeta = novaMeta
                    binding.metaTotalCalories.text = preferencesManager.userMeta.toString()
                    ifElseMeta()
                    updateProgressBarCalorias(preferencesManager.CaloriaDiaAtual!!.toInt())
                } else {
                    Toast.makeText(
                        this,
                        "Erro ao atualizar meta: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun parseNumber(value: String?): Float {
        return try {
            val format = NumberFormat.getInstance(Locale("pt", "BR"))
            format.parse(value?.trim() ?: "0")?.toFloat() ?: 0f
        } catch (e: Exception) {
            0f
        }
    }


    private fun grafico(
        dia1: List<Historico>,
        dia2: List<Historico>,
        dia3: List<Historico>,
        dia4: List<Historico>,
        dia5: List<Historico>,
        dia6: List<Historico>,
        dia7: List<Historico>
    ) {
        val lineChart = findViewById<LineChart>(R.id.lineChart)

        val dia1caloria = dia1.sumOf { parseNumber(it.caloria).toInt() }
        val dia2caloria = dia2.sumOf { parseNumber(it.caloria).toInt() }
        val dia3caloria = dia3.sumOf { parseNumber(it.caloria).toInt() }
        val dia4caloria = dia4.sumOf { parseNumber(it.caloria).toInt() }
        val dia5caloria = dia5.sumOf { parseNumber(it.caloria).toInt() }
        val dia6caloria = dia6.sumOf { parseNumber(it.caloria).toInt() }
        val dia7caloria = dia7.sumOf { parseNumber(it.caloria).toInt() }

        preferencesManager.CaloriaDiaAtual = dia7caloria
        updateProgressBarCalorias(dia7caloria)
        updateValoresNutricionaisDiario(dia7)


        val entries = listOf(
            Entry(0f, dia1caloria.toFloat()),
            Entry(1f, dia2caloria.toFloat()),
            Entry(2f, dia3caloria.toFloat()),
            Entry(3f, dia4caloria.toFloat()),
            Entry(4f, dia5caloria.toFloat()),
            Entry(5f, dia6caloria.toFloat()),
            Entry(6f, dia7caloria.toFloat())
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

        // tira valores exibidos no meio da linha
        dataSet.setDrawValues(false) /// desativa os valores no gráfico

        // Configuração do eixo X
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false) // Desabilitar linhas de grade
        xAxis.granularity = 1f // Intervalo entre os valores
        xAxis.textSize = 20f // Aumentar o tamanho do texto
        xAxis.textColor = Color.BLACK // Cor do texto
        val formatoData = DateTimeFormatter.ofPattern("d") // Agora é apenas o dia do mês
        val hoje = LocalDate.now()
        val ultimos7Dias = (0..6).map { hoje.minusDays(it.toLong()).format(formatoData) }.reversed()

        // Configurando o ValueFormatter para exibir apenas o dia
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return ultimos7Dias.getOrNull(value.toInt()) ?: value.toString()
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

        // aqui é onde quando é clicado no grafico mostr os valores
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

    private fun updateValoresNutricionaisDiario(dia7: List<Historico>) {
        val totalProteinas = dia7.sumOf { parseNumber(it.proteina).toInt() }
        val totalGordura = dia7.sumOf { parseNumber(it.gordura).toInt() }
        val totalCarboidratos = dia7.sumOf { parseNumber(it.carboidrato).toInt() }
        val totalMacronutrientes = totalProteinas + totalGordura + totalCarboidratos

        if (totalMacronutrientes > 0) {
            val percProteina = (totalProteinas.toFloat() / totalMacronutrientes) * 100
            val percGordura = (totalGordura.toFloat() / totalMacronutrientes) * 100
            val percCarboidrato = (totalCarboidratos.toFloat() / totalMacronutrientes) * 100


            val parentWidthProteina = (binding.columnProteina.parent as View).width
            val parentWidthCarboidrato = (binding.columnCarboidrato.parent as View).width
            val parentWidthGordura = (binding.columnGordura.parent as View).width

            binding.columnProteina.layoutParams = binding.columnProteina.layoutParams.apply {
                width = (parentWidthProteina * percProteina / 100).toInt()
            }
            binding.columnProteina.requestLayout()

            binding.columnCarboidrato.layoutParams = binding.columnCarboidrato.layoutParams.apply {
                width = (parentWidthCarboidrato * percCarboidrato / 100).toInt()
            }
            binding.columnCarboidrato.requestLayout()

            binding.columnGordura.layoutParams = binding.columnGordura.layoutParams.apply {
                width = (parentWidthGordura * percGordura / 100).toInt()
            }
            binding.columnGordura.requestLayout()

            // Atualiza os textos
            binding.valorProteinas.text = "$totalProteinas g"
            binding.valorGordura.text = "$totalGordura g"
            binding.valorCarboidratos.text = "$totalCarboidratos g"

            Log.i(
                "Macronutrientes",
                "Proteínas: $percProteina%, Gordura: $percGordura%, Carboidratos: $percCarboidrato%"
            )
        } else {
            Log.w("Macronutrientes", "Nenhum macronutriente encontrado.")
        }
    }


    private fun updateProgressBarCalorias(dia7caloria: Int) {
        binding.caloriasdiaria.text = dia7caloria.toString()
        binding.metaDiaria.text = "${dia7caloria} Kcal"
        val meta = preferencesManager.userMeta!!.toFloat()

        if (meta > 0) {
            val porcentagem = (dia7caloria / meta) * 100
            val calorieProgressView = findViewById<CalorieProgressView>(R.id.calorieProgressView)
            calorieProgressView.setProgress(porcentagem)

            // Opcional: Mostre a porcentagem no log ou na tela
            Log.i(
                "CalorieProgress",
                "Progresso: $porcentagem% (Consumidas: $dia7caloria, Meta: $meta)"
            )
        } else {
            Log.e("CalorieProgress", "A meta deve ser maior que 0.")
            val calorieProgressView = findViewById<CalorieProgressView>(R.id.calorieProgressView)
            calorieProgressView.setProgress(0f)
        }
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

    @SuppressLint("SuspiciousIndentation")
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
                                            preferencesManager.image = downloadUri
                                            val request = ImageRequest(imagem = base64String)

                                            try {
                                                lifecycleScope.launch(Main) {
                                                    binding.layoutMain.visibility = GONE
                                                    binding.layoutLoad.visibility = VISIBLE
                                                }
                                                lifecycleScope.launch(IO) {

                                                    val resultado =
                                                        ApiAlimento.retrofitService.image(
                                                            request
                                                        )
                                                    lifecycleScope.launch(Main) {
                                                        Log.i(
                                                            "RESULTADOdaAPIIIIIIIIIIIIII",
                                                            resultado.toString()
                                                        )
                                                        Log.d(
                                                            "PhotoPicker",
                                                            "Imagem salva em Base64 no SharedPreferences ${preferencesManager.image}"
                                                        )

                                                        val intent = Intent(
                                                            this@MainActivity,
                                                            NutricaoClassesActivity::class.java
                                                        )
                                                        intent.putExtra(
                                                            "classe",
                                                            resultado[0].classe
                                                        )
                                                        startActivity(intent)
                                                    }
                                                }
                                            } catch (e: HttpException) {
                                                // Captura o erro 502 ou outros erros HTTP
                                                Log.e(
                                                    "API_ERROR",
                                                    "Erro ao chamar o servidor: ${e.message}"
                                                )
                                                lifecycleScope.launch(Main) {
                                                    // Exibe uma mensagem de erro para o usuário
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Erro ao se conectar ao servidor. Tente novamente.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } catch (e: Exception) {
                                                // Captura outros tipos de exceção
                                                Log.e(
                                                    "GENERAL_ERROR",
                                                    "Erro desconhecido: ${e.message}"
                                                )
                                                lifecycleScope.launch(Main) {
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Erro desconhecido. Tente novamente.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        } else {
                                            Log.e(
                                                "PhotoPicker",
                                                "Falha ao baixar ou processar a imagem"
                                            )
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
        binding.layoutMain.visibility = VISIBLE
        binding.layoutLoad.visibility = GONE
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