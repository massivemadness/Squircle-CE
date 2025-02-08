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

package com.blacksquircle.ui.feature.themes.data.model

internal enum class CodePreview(
    val extension: String,
    val codeSample: String,
) {
    HTML(".html", HTML_SAMPLE),
    JAVASCRIPT(".js", JAVASCRIPT_SAMPLE);

    companion object {

        fun of(extension: String): CodePreview {
            return checkNotNull(entries.find { it.extension == extension })
        }
    }
}

private val HTML_SAMPLE = """
    <!DOCTYPE html>
    <html class="no-js" lang="">
    <head>
      <meta charset="utf-8">
      <title>Title</title>
      <meta name="description" content="">
      <meta name="viewport" content="width=device-width, initial-scale=1">

      <meta property="og:title" content="">
      <meta property="og:type" content="">
      <meta property="og:url" content="">
      <meta property="og:image" content="">

      <link rel="manifest" href="site.webmanifest">
      <link rel="apple-touch-icon" href="icon.png">
      <!-- Place favicon.ico in the root directory -->

      <link rel="stylesheet" href="css/normalize.css">
      <link rel="stylesheet" href="css/style.css">
    </head>
""".trimIndent()

private val JAVASCRIPT_SAMPLE = """
    function makeIterator(array) {
      var index = 0;
      return {
        next: function() {
          return index < array.length
            ? { value: array[index++], done: false }
            : { done: true }
        }
      };
    }

    var it = makeIterator(["simple", "iterator"]);

    console.log(it.next()); // done: false
    console.log(it.next()); // done: false
    console.log(it.next()); // done: true
""".trimIndent()