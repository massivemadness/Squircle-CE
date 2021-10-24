# Migration Guide

This migration guide will help you adapt your existing code to match the
latest version of [EditorKit](README.md) library.

---

## v2.0.0 => v2.1.0

Migration steps:
1. Setup of the code editor was completely rewritten:
   1. The `EditorConfig` is deleted, you have to apply the settings
      using `PluginSupplier` as shown below:
      ```kotlin
      // Before
      editor.editorConfig = EditorConfig(
          fontSize = 14f,
          fontType = Typeface.MONOSPACE,
          wordWrap = true,
          codeCompletion = true,
          pinchZoom = true,
          lineNumbers = true,
          highlightCurrentLine = true,
          highlightDelimiters = true,
          ...
      )
      
      // After
      val pluginSupplier = PluginSupplier.create {
           codeCompletion {
               suggestionAdapter = ...
           }
           pinchZoom { // or pinchZoom()
               minTextSize = 10f
               maxTextSize = 20f
           }
           lineNumbers { // or lineNumbers()
               lineNumbers = true
               highlightCurrentLine = true
           }
           highlightDelimiters()
           // ...
      }
      editor.plugins(pluginSupplier) 
      ```
      You might notice that there's no alternative to `fontSize`,
      `fontType` and some other properties in `PluginSupplier`, to set
      these parameters use following methods:
      ```kotlin
      editor.setTextSize(14f)
      editor.setTypeface(Typeface.MONOSPACE)
      editor.setHorizontallyScrolling(false) // previous wordWrap = true
      editor.setSoftKeyboard(false)
      editor.setUseSpacesInsteadOfTabs(true)
      editor.setTabWidth(4)
      ```
2. If you using custom color schemes:
   1. The `SyntaxScheme` class is deleted, all it's properties were
      moved in `ColorScheme` itself.
3. If you using custom languages:
   1. Remove `enqueue()` and `cancel()` methods in `LanguageStyler`. The
      `execute()` method now invoked on the background thread, so now
      you don't need to write the asynchronous work by yourself.
   2. The `execute()` method now takes `ColorScheme` as the parameter
      since the `SyntaxScheme` class is deleted.
      ```kotlin
      class CustomStyler : LanguageStyler {

          override fun execute(source: String, scheme: ColorScheme): List<SyntaxHighlightSpan> {
              val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
              
              // TODO Implement syntax highlighting
              
              return syntaxHighlightSpans
          }
      }
      ```

---

## v1.3.0 => v2.0.0

Migration steps:
1. Change the package name from `com.brackeys.ui` to
   `com.blacksquircle.ui`
2. Everything else remains the same

---

## v1.2.1 => v1.3.0

Migration steps:
1. If you using custom themes, add new `variableColor` property to the
   `SyntaxScheme` object
2. Everything else remains the same

---

## v1.2.0 => v1.2.1

Migration steps:
1. Change the groupId from `com.brackeys.ui` to `com.blacksquircle.ui`
   as shown below:
   ```groovy
   // Before
   implementation "com.brackeys.ui:editorkit:1.2.0"
   
   // After
   implementation "com.blacksquircle.ui:editorkit:1.2.1"
   ```
2. The package name `com.brackeys.ui` remains the same, but it will be
   changed in the future

---

## v1.1.0 => v1.2.0

Migration steps:
1. If you using `TextScroller`, rename it's `link()` method to
   `attachTo()` as shown below:
   ```kotlin
   val scroller = findViewById<TextScroller>(R.id.scroller)
   val editor = findViewById<TextProcessor>(R.id.editor)
   
   // Before
   scroller.link(editor)
   
   // After
   scroller.attachTo(editor)
   ```
2. Everything else remains the same

---

## v1.0.1 => v1.1.0

Migration steps:
1. Rename `Config` to `EditorConfig` as shown below:
   ```kotlin
   // Before
   editor.config = Config(...)
   
   // After
   editor.editorConfig = EditorConfig(...)
   ```
2. Everything else remains the same

---

## v1.0.0 => v1.0.1

Migration steps:
1. Rename `ShortcutListener` to `OnShortcutListener` as shown below:
   ```kotlin
   // Before
   editor.shortcutListener = object : ShortcutListener { ... }
   
   // After
   editor.onShortcutListener = object : OnShortcutListener { ... }
   ```
2. Everything else remains the same
