package com.example.omniunz.grafic
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.example.omniunz.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.security.KeyStore

class CustomMarkerView(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    private val markerText: TextView = findViewById(R.id.marker_text)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            // Atualize o texto do MarkerView com os dados do ponto selecionado
            markerText.text = "Valor: ${it.y.toInt()}"
        }
        super.refreshContent(e, highlight)
    }


    override fun getOffset(): MPPointF {
        // Posiciona o MarkerView centralizado acima do ponto
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}


