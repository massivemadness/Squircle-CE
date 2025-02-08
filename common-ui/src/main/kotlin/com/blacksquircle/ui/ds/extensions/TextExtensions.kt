/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.ds.extensions

import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

private const val URL_TAG = "url_tag"

fun AnnotatedString.takeUrl(pos: Int): String? {
    return getStringAnnotations(URL_TAG, pos, pos).firstOrNull()?.item
}

fun String.buildHtmlAnnotatedString(linkColor: Color) = buildAnnotatedString {
    val spanned = HtmlCompat
        .fromHtml(this@buildHtmlAnnotatedString, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val spans = spanned.getSpans(0, spanned.length, Any::class.java)

    append(spanned.toString())

    spans.forEach { span ->
        val start = spanned.getSpanStart(span)
        val end = spanned.getSpanEnd(span)
        applySpanStyle(span, start, end, linkColor)?.let { spanStyle ->
            addStyle(spanStyle, start, end)
        }
    }
}

private fun AnnotatedString.Builder.applySpanStyle(
    span: Any,
    start: Int,
    end: Int,
    linkColor: Color,
): SpanStyle? {
    return when (span) {
        is StyleSpan -> when (span.style) {
            Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
            Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
            Typeface.BOLD_ITALIC -> SpanStyle(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
            else -> null
        }
        is UnderlineSpan -> SpanStyle(textDecoration = TextDecoration.Underline)
        is ForegroundColorSpan -> SpanStyle(color = Color(span.foregroundColor))
        is StrikethroughSpan -> SpanStyle(textDecoration = TextDecoration.LineThrough)
        is URLSpan -> {
            addStringAnnotation(
                tag = URL_TAG,
                annotation = span.url,
                start = start,
                end = end
            )
            SpanStyle(
                color = linkColor,
                textDecoration = TextDecoration.Underline
            )
        }
        else -> null
    }
}