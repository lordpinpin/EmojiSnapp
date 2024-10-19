package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

class SemiCircleTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var text: String = "Ongoing: All Emojis in this location with odd-costed power lose -1 power, while cards with even-costed powers lose -2 power."
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.black) // Default text color
        textSize = 30f // Adjust text size as needed
        textAlign = Paint.Align.CENTER
        typeface = ResourcesCompat.getFont(context, R.font.allerta)
        letterSpacing = -0.05f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Define semicircle parameters
        val width = width.toFloat()
        val radius = width / 2

        // Split text into multiple lines based on the decreasing max width
        val lines = splitTextToLines(text, radius * 2)

        // Draw each line, adjusting the position to create a wrapped effect
        for (i in lines.indices) {
            val line = lines[i]
            val yOffset = textPaint.textSize * (i + 1) + 5 * i // Adjust line spacing

            // Calculate decreasing line width based on the semicircle curvature
            val maxWidthForLine = (radius * 2) - (i * 30) // Adjust shrinkage rate as needed
            drawCenteredText(canvas, line, width / 2, yOffset, maxWidthForLine)
        }
    }

    // Method to split text into multiple lines based on a maxWidth
    private fun splitTextToLines(text: String, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val potentialLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (textPaint.measureText(potentialLine) <= maxWidth) {
                currentLine = potentialLine
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        return lines
    }

    // Draw text centered within the given maximum width
    private fun drawCenteredText(canvas: Canvas, text: String, x: Float, y: Float, maxWidth: Float) {
        // Clip the width if needed to make it look like it fits the semicircle
        val clippedText = if (textPaint.measureText(text) > maxWidth) {
            ellipsizeText(text, maxWidth)
        } else {
            text
        }
        canvas.drawText(clippedText, x, y, textPaint)
    }

    // Function to ellipsize text if it exceeds the maximum width
    private fun ellipsizeText(text: String, maxWidth: Float): String {
        var ellipsized = text
        while (textPaint.measureText(ellipsized + "...") > maxWidth && ellipsized.isNotEmpty()) {
            ellipsized = ellipsized.dropLast(1)
        }
        return if (ellipsized.isNotEmpty()) "$ellipsized..." else ""
    }

    // Public method to set text dynamically
    fun setText(newText: String) {
        text = newText
        invalidate() // Redraw the view with new text
    }
}