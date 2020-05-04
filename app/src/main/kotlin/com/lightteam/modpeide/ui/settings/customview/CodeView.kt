package com.lightteam.modpeide.ui.settings.customview

import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.toSpannable
import com.lightteam.language.language.Language
import com.lightteam.language.styler.Styleable
import com.lightteam.language.styler.span.SyntaxHighlightSpan
import com.lightteam.modpeide.data.converter.ThemeConverter
import com.lightteam.modpeide.data.feature.scheme.Theme
import com.lightteam.unknown.language.UnknownLanguage

class CodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), Styleable {

    companion object {
        val CODE_PREVIEW = """
            function useItem(x, y, z, itemId, blockId, side) {
                if (itemId == 280) { // Stick
                    Level.explode(x, y, z, 16);
                }
            }
            
            function procCmd(cmd) {
                var command = cmd.split(" ");
                if (command[0] == "kit") {
                    if (command[1] == "start") {
                        // TODO: Implement this method
                    }
                    if (command[1] == "test") {
                        // TODO: Implement this method
                    }
                }
            }
        """.trimIndent()
    }

    var language: Language = UnknownLanguage()
        set(value) {
            field = value
            syntaxHighlight()
        }

    var theme: Theme? = null
        set(value) {
            field = value
            colorize()
        }

    private var syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()

    override fun setSpans(spans: List<SyntaxHighlightSpan>) {
        syntaxHighlightSpans = spans as MutableList<SyntaxHighlightSpan>
        if (layout != null) {
            var topLine = scrollY / lineHeight - 30
            if (topLine >= lineCount) {
                topLine = lineCount - 1
            } else if (topLine < 0) {
                topLine = 0
            }

            var bottomLine = (scrollY + height) / lineHeight + 30
            if (bottomLine >= lineCount) {
                bottomLine = lineCount - 1
            } else if (bottomLine < 0) {
                bottomLine = 0
            }

            val lineStart = layout.getLineStart(topLine)
            val lineEnd = layout.getLineEnd(bottomLine)

            val currentText = text.toSpannable()
            for (span in syntaxHighlightSpans) {
                if (span.start >= 0 && span.end <= text.length && span.start <= span.end
                    && (span.start in lineStart..lineEnd || span.start <= lineEnd && span.end >= lineStart)) {
                    currentText.setSpan(
                        span,
                        if (span.start < lineStart) lineStart else span.start,
                        if (span.end > lineEnd) lineEnd else span.end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            text = currentText
        }
    }

    private fun colorize() {
        theme?.let {
            post {
                setTextColor(it.colorScheme.textColor)
                // setBackgroundColor(it.colorScheme.backgroundColor)
            }
        }
    }

    private fun syntaxHighlight() {
        theme?.let {
            language.runStyler(
                this,
                text.toString(),
                ThemeConverter.toSyntaxScheme(it)
            )
        }
    }
}