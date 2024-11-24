package com.example.omniunz

import DataAdapter
import Historico
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.angellira.app_1_eduardo.recyclerview.adapter.RecyclerViewHistorico
import com.example.omniunz.databinding.ActivityHistoricoBinding
import com.example.omniunz.model.MyDataItem
import com.google.android.material.carousel.CarouselLayoutManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoricoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()


        binding.iconButtonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val historicos = listOf(
            Historico(
                name = "Frango Grelhado",
                proteina = "30g",
                carboidrato = "5g",
                gordura = "10g",
                caloria = "250",
                data = "24/11/2024",
                image = "https://example.com/images/frango_grelhado.png"
            ),
            Historico(
                name = "Arroz Integral",
                proteina = "3g",
                carboidrato = "38g",
                gordura = "1g",
                caloria = "170",
                data = "23/11/2024",
                image = "https://example.com/images/arroz_integral.png"
            ),
            Historico(
                name = "Abacate",
                proteina = "2g",
                carboidrato = "9g",
                gordura = "15g",
                caloria = "160",
                data = "22/11/2024",
                image = "https://example.com/images/abacate.png"
            ),
            Historico(
                name = "Batata Doce",
                proteina = "2g",
                carboidrato = "20g",
                gordura = "0g",
                caloria = "80",
                data = "21/11/2024",
                image = "https://example.com/images/batata_doce.png"
            ),
            Historico(
                name = "Peito de Peru",
                proteina = "25g",
                carboidrato = "0g",
                gordura = "3g",
                caloria = "120",
                data = "20/11/2024",
                image = "https://example.com/images/peito_peru.png"
            )
        )


        recyclerView(historicos)

        filtroDatas()
        recyclerViewDatas()

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
                        dataList.add(
                            MyDataItem(
                                dayFormatted,
                                isDaily = true
                            )
                        )

                        // Verifica se é o dia atual e marca o item
                        if (currentDate.dayOfMonth == index + 1) {
                            selectedItemIndex = index // Marca o índice do item como selecionado
                        }
                    }
                }

                R.id.btnSemanal -> {
                    // Preenche a lista com as semanas do mês
                    val weeksOfMonth = mutableListOf<String>()
                    val firstDayOfMonth = currentDate.withDayOfMonth(1)
                    for (week in 0..3) {
                        val startOfWeek = firstDayOfMonth.plusWeeks(week.toLong())
                        weeksOfMonth.add(
                            "Semana \n${week + 1} - ${
                                startOfWeek.format(
                                    DateTimeFormatter.ofPattern("MMM dd")
                                )
                            }"
                        )
                    }
                    weeksOfMonth.forEachIndexed { index, weekFormatted ->
                        dataList.add(
                            MyDataItem(
                                weekFormatted,
                                isDaily = false
                            )
                        )

                        // Verifica se é a semana atual e marca o item
                        if (currentDate.isBefore(firstDayOfMonth.plusWeeks((index + 1).toLong()))) {
                            selectedItemIndex = index // Marca o índice do item como selecionado
                        }
                    }
                }

                R.id.btnMensal -> {
                    // Preenche a lista com os meses do ano
                    val monthsOfYear = (0 until 12).map { month ->
                        currentDate.plusMonths(month.toLong())
                            .format(DateTimeFormatter.ofPattern("MMMM"))
                    }
                    monthsOfYear.forEachIndexed { index, monthFormatted ->
                        dataList.add(
                            MyDataItem(
                                monthFormatted,
                                isDaily = false
                            )
                        )

                        // Verifica se é o mês atual e marca o item
                        if (currentDate.monthValue == index + 1) {
                            selectedItemIndex = index // Marca o índice do item como selecionado
                        }
                    }
                }
            }

            // Cria o adapter e configura no RecyclerView
            val adapter = DataAdapter(
                posts = dataList,
                onItemClickListener = { data ->
                    // Lógica para o clique nos itens
                }
            )
            Log.i("datas", dataList.toString())

            binding.recyclerView2.adapter = adapter

            // Após configurar o RecyclerView, marca o item selecionado
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