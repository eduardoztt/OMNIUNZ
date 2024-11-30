package com.example.omniunz.grafic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CalorieProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, android.R.color.darker_gray)
        style = Paint.Style.STROKE
        strokeWidth = 80f //largura do circulo
    }

    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#FF7B5CFF")
        style = Paint.Style.STROKE
        strokeWidth = 80f //largura do circulo
        strokeCap = Paint.Cap.ROUND
    }

    private val rectF = RectF()
    private var progress = 0f // Progresso inicial (em %)

    fun setProgress(value: Float) {
        progress = value
        invalidate() // Redesenha a View
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Define o retângulo para o arco
        val size = Math.min(width, height).toFloat()
        val padding = paintProgress.strokeWidth / 2
        rectF.set(padding, padding, size - padding, size - padding)

        // Desenha o fundo do arco
        canvas.drawArc(rectF, 184f, 180f, false, paintBackground)

        // Desenha o progresso
        canvas.drawArc(rectF, 184f, progress * 1.8f, false, paintProgress)
    }
}