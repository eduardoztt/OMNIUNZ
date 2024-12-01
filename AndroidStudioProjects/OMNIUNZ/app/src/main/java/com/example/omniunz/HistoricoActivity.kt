package com.example.omniunz

import DataAdapter
import Historico
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.angellira.app_1_eduardo.preferences.PreferencesManager
import com.angellira.app_1_eduardo.recyclerview.adapter.RecyclerViewHistorico
import com.example.omniunz.databinding.ActivityHistoricoBinding
import com.example.omniunz.model.MyDataItem
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

class HistoricoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var preferencesManager: PreferencesManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()

        preferencesManager = PreferencesManager(this)
        database = FirebaseDatabase.getInstance()



        binding.iconButtonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        filtroDatas()
        recyclerViewDatas()

    }

    private fun filtrarHistoricoPorSemana(dataInicial: String) {
        // Formato de data esperado
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        // Converte a data inicial recebida para LocalDate
        val dataInicioParsed = LocalDate.parse(dataInicial, dateFormatter)

        // Calcula a data final como 7 dias após a data inicial
        val dataFimParsed = dataInicioParsed.plusDays(6)

        val userId = preferencesManager.userUid
        if (userId != null) {
            val userRef = database.reference.child("users").child(userId).child("alimentos")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val alimentosFiltrados = mutableListOf<Historico>()

                    // Formato de data no Firebase
                    val firebaseDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                    for (childSnapshot in snapshot.children) {
                        val alimento = childSnapshot.getValue(Historico::class.java)
                        alimento?.let {
                            var dataAlimento: LocalDate
                            try {
                                // Tenta fazer o parse da data no formato "dd-MM-yyyy"
                                dataAlimento = LocalDate.parse(it.data, dateFormatter)
                            } catch (e: DateTimeParseException) {
                                // Se o formato "dd-MM-yyyy" falhar, tenta o formato "yyyy-MM-dd"
                                dataAlimento = LocalDate.parse(it.data, firebaseDateFormatter)
                            }

                            // Verifica se a data do alimento está dentro do intervalo da semana
                            if ((dataAlimento.isEqual(dataInicioParsed) || dataAlimento.isAfter(
                                    dataInicioParsed
                                )) &&
                                (dataAlimento.isEqual(dataFimParsed) || dataAlimento.isBefore(
                                    dataFimParsed
                                ))
                            ) {
                                alimentosFiltrados.add(it)
                            }
                        }
                    }

                    // Atualizar o RecyclerView com os alimentos filtrados
                    recyclerView(alimentosFiltrados)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@HistoricoActivity,
                        "Erro ao carregar alimentos por semana.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }


    private fun obterIntervaloDaSemana(numeroDaSemana: Int, ano: Int): Pair<String, String> {
        val primeiroDiaDoAno = LocalDate.of(ano, 1, 1)
        val inicioDaSemana = primeiroDiaDoAno.with(ChronoField.DAY_OF_WEEK, 1)
            .plusWeeks((numeroDaSemana - 1).toLong()) // Segunda-feira
        val fimDaSemana = inicioDaSemana.plusDays(6) // Domingo

        return Pair(
            inicioDaSemana.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            fimDaSemana.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        )
    }

    private fun filtrarHistoricoPorMes(mesAno: String) {
        val intervalo = obterIntervaloDoMes(mesAno)
        val (dataInicio, dataFim) = intervalo

        val userId = preferencesManager.userUid
        if (userId != null) {
            val userRef = database.reference.child("users").child(userId).child("alimentos")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val alimentosFiltrados = mutableListOf<Historico>()

                    // Formatos de data possíveis
                    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    val firebaseDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                    for (childSnapshot in snapshot.children) {
                        val alimento = childSnapshot.getValue(Historico::class.java)
                        alimento?.let {
                            var dataAlimento: LocalDate
                            try {
                                // Tenta fazer o parse da data no formato "dd-MM-yyyy"
                                dataAlimento = LocalDate.parse(it.data, dateFormatter)
                            } catch (e: DateTimeParseException) {
                                // Se o formato "dd-MM-yyyy" falhar, tenta o formato "yyyy-MM-dd"
                                dataAlimento = LocalDate.parse(it.data, firebaseDateFormatter)
                            }

                            // Verifica se a data do alimento está dentro do intervalo
                            val dataInicioParsed = LocalDate.parse(dataInicio, dateFormatter)
                            val dataFimParsed = LocalDate.parse(dataFim, dateFormatter)

                            if ((dataAlimento.isEqual(dataInicioParsed) || dataAlimento.isAfter(
                                    dataInicioParsed
                                )) &&
                                (dataAlimento.isEqual(dataFimParsed) || dataAlimento.isBefore(
                                    dataFimParsed
                                ))
                            ) {
                                alimentosFiltrados.add(it)
                            }
                        }
                    }

                    // Atualizar o RecyclerView com os alimentos filtrados
                    recyclerView(alimentosFiltrados)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@HistoricoActivity,
                        "Erro ao carregar alimentos por mês.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }


    private fun obterIntervaloDoMes(mesAno: String): Pair<String, String> {
        // Use o formato correto para a entrada '01-11-2024'
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())

        // Monta a data de início com o primeiro dia do mês
        val dataInicio = LocalDate.parse("01-$mesAno", formatter)

        // A data final será o último dia do mês
        val dataFim = dataInicio.withDayOfMonth(dataInicio.lengthOfMonth())

        // Formatando as datas para o formato dd-MM-yyyy
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return Pair(
            dataInicio.format(dateFormatter),
            dataFim.format(dateFormatter)
        )
    }


    private fun filtrarHistoricoPorData(dataSelecionada: String) {
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

                        // Atualizar o RecyclerView com os itens filtrados
                        recyclerView(alimentosFiltrados)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@HistoricoActivity,
                            "Erro ao carregar alimentos filtrados.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }


    private fun filtroDatas() {
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            val context = this@HistoricoActivity // Usando o contexto da Activity diretamente

            when (checkedId) {
                R.id.btnDiario -> {
                    if (isChecked) {
                        binding.btnDiario.setStrokeColorResource(R.color.black)
                        binding.btnDiario.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                        binding.btnSemanal.setStrokeColorResource(android.R.color.transparent)
                        binding.btnSemanal.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                        binding.btnMensal.setStrokeColorResource(android.R.color.transparent)
                        binding.btnMensal.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    }
                }

                R.id.btnSemanal -> {
                    if (isChecked) {
                        // Define as bordas e a cor do fundo para o botão selecionado
                        binding.btnSemanal.setStrokeColorResource(R.color.black)
                        binding.btnSemanal.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        ) // Cor do fundo
                        binding.btnDiario.setStrokeColorResource(android.R.color.transparent)
                        binding.btnDiario.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                        binding.btnMensal.setStrokeColorResource(android.R.color.transparent)
                        binding.btnMensal.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    }
                }

                R.id.btnMensal -> {
                    if (isChecked) {
                        // Define as bordas e a cor do fundo para o botão selecionado
                        binding.btnMensal.setStrokeColorResource(R.color.black)
                        binding.btnMensal.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        ) // Cor do fundo
                        binding.btnDiario.setStrokeColorResource(android.R.color.transparent)
                        binding.btnDiario.setBackgroundColor(
                            ContextCompat.getColor
                                (
                                context,
                                R.color.white
                            )
                        )
                        binding.btnSemanal.setStrokeColorResource(android.R.color.transparent)
                        binding.btnSemanal.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    }
                }
            }
        }
    }

    private fun recyclerViewDatas() {
        // Adiciona o listener ao ToggleGroup
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            val currentDate = LocalDate.now()
            val dataList = mutableListOf<MyDataItem>()
            var selectedItemIndex: Int? = null

            when (checkedId) {
                R.id.btnDiario -> {
                    // Preenche a lista com os dias do mês
                    val daysOfMonth = (1..currentDate.lengthOfMonth()).map { day ->
                        currentDate.withDayOfMonth(day)
                            .format(DateTimeFormatter.ofPattern("MMM \ndd"))
                    }
                    daysOfMonth.forEachIndexed { index, dayFormatted ->

                        val dataNoFormatoDDMMYYYY = currentDate.withDayOfMonth(index + 1)
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

                        dataList.add(
                            MyDataItem(
                                name = dayFormatted,  // Exibe como "Nov \n23"
                                data = dataNoFormatoDDMMYYYY,  // Salva como "23-11-2024"
                                isDaily = true,
                                isSemana = false
                            )
                        )

                        // Verifica se é o dia atual e marca o item
                        if (currentDate.dayOfMonth == index + 1) {
                            selectedItemIndex = index // Marca o índice do item como selecionado
                        }
                    }
                }

                R.id.btnSemanal -> {
                    val weeksOfMonth = mutableListOf<String>()

                    val currentDate = LocalDate.now()
                    val firstDayOfMonth = currentDate.withDayOfMonth(1)
                    val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth())

                    val totalWeeksInMonth = ChronoUnit.WEEKS.between(
                        firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)),
                        lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                    ).toInt() + 1

                    for (week in 0 until totalWeeksInMonth) {
                        val startOfWeek = firstDayOfMonth.plusWeeks(week.toLong()).with(
                            TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)
                        )

                        val endOfWeek = startOfWeek.plusDays(6)

                        val weekFormatted = "Semana ${week + 1} - ${
                            startOfWeek.format(
                                DateTimeFormatter.ofPattern("MMM dd")
                            )
                        } a ${endOfWeek.format(DateTimeFormatter.ofPattern("MMM dd"))}"

                        // Salvando a data de início da semana no formato "dd-MM-yyyy"
                        val weekStartDate =
                            startOfWeek.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

                        dataList.add(
                            MyDataItem(
                                name = weekFormatted,
                                data = weekStartDate, // Aqui será salva a data de início da semana
                                isDaily = false,
                                isSemana = true
                            )
                        )

                        if (currentDate.isBefore(endOfWeek.plusDays(1)) && currentDate.isAfter(
                                startOfWeek.minusDays(1)
                            )
                        ) {
                            selectedItemIndex = week
                        }
                    }
                }


                R.id.btnMensal -> {
                    // Preenche a lista com os meses do ano atual em pt-BR
                    val monthsOfYear = (0 until 12).map { month ->
                        // Usa o ano atual e configura o Locale para pt-BR para os nomes dos meses
                        currentDate.withMonth(month + 1)
                            .format(DateTimeFormatter.ofPattern("MMMM", Locale("pt", "BR")))
                    }

                    monthsOfYear.forEachIndexed { index, monthFormatted ->
                        // Cria a data correspondente a cada mês no formato "MM-yyyy"
                        val monthDate = currentDate.withMonth(index + 1)
                            .format(DateTimeFormatter.ofPattern("MM-yyyy"))
                        val dateWithDay = "$monthDate"  // Adiciona o dia 01

                        dataList.add(
                            MyDataItem(
                                name = monthFormatted,  // Exibe como "novembro", "dezembro", etc.
                                data = dateWithDay,      // Salva como "01-MM-yyyy"
                                isDaily = false,
                                isSemana = false
                            )
                        )

                        // Verifica se é o mês atual e marca o item
                        if (currentDate.monthValue == index + 1) {
                            selectedItemIndex = index // Marca o índice do item como selecionado
                        }
                    }
                }


            }


            adapterDatas(dataList)

            // Após configurar o RecyclerView, marca o item selecionado
            selectedItemIndex?.let {
                // Simula o clique no item, ao forçar a rolagem até ele e chamando a função de seleção
                (binding.recyclerView2.layoutManager as? LinearLayoutManager)?.apply {
                    scrollToPositionWithOffset(it, 0)
                }
            }
        }

        // Simula a seleção do botão "Diário" ao iniciar
        binding.toggleGroup.check(R.id.btnDiario)
    }

    private fun adapterDatas(dataList: MutableList<MyDataItem>) {
        val adapter = DataAdapter(
            posts = dataList,
            onItemClickListener = { dataItem ->
                if (dataItem.isDaily) {
                    filtrarHistoricoPorData(dataItem.data)
                } else {
                    if (dataItem.isSemana) {
                        filtrarHistoricoPorSemana(dataItem.data)
                    } else {
                        filtrarHistoricoPorMes(dataItem.data) // Passa o valor correto de mês (MM-yyyy)
                    }
                }
            }
        )
        Log.i("datas", dataList.toString())

        binding.recyclerView2.adapter = adapter
    }


    private fun recyclerView(listAnalise: List<Historico>) {
        Log.d("listResult", "listResult  $listAnalise")

        val adapter = RecyclerViewHistorico(
            posts = listAnalise,
        )
        binding.recyclerview.adapter = adapter

    }

    private fun setupView() {
        binding = ActivityHistoricoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}