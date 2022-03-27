# Migration Guide

This migration guide will help you adapt your existing code to match the
latest version of [EditorKit](README.md#editorkit) library.

1. [v2.1.2 -> v2.2.0](#v212---v220)
2. [v2.0.0 -> v2.1.0](#v200---v210)
3. [v1.3.0 -> v2.0.0](#v130---v200)
4. [v1.2.1 -> v1.3.0](#v121---v130)
5. [v1.2.0 -> v1.2.1](#v120---v121)
6. [v1.1.0 -> v1.2.0](#v110---v120)
7. [v1.0.1 -> v1.1.0](#v101---v110)
8. [v1.0.0 -> v1.0.1](#v100---v101)

---

## v2.1.2 -> v2.2.0

Migration steps:
1. If you're using custom languages, replace 'getName' method with `languageName` property.
   ```kotlin
   // Before
   override fun getName() {
       return "custom language"
   }
   
   // After
   override val languageName = "custom language"
   ```

---

## v2.0.0 -> v2.1.0

Migration steps:
1. Setup of the code editor was completely rewritten from scratch, the
   `EditorConfig` class was removed, you have to apply the settings by
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
       ...
   )
   
   // After
   val pluginSupplier = PluginSupplier.create {
        codeCompletion {
            suggestionAdapter = ...
        }
        pinchZoom { // or simply pinchZoom()
            minTextSize = 10f
            maxTextSize = 20f 
        }
        lineNumbers { // or simply lineNumbers()
            lineNumbers = true
            highlightCurrentLine = true
        }
        highlightDelimiters()
        autoIndentation()
        // ...
   }
   editor.plugins(pluginSupplier)
   ```
   You can enable/disable plugins in runtime by surrounding necessary
   methods with `if (enabled) { ... }` operator:
   ```kotlin
   val pluginSupplier = PluginSupplier.create {
       if (preferences.isLineNumbersEnabled) {
           lineNumbers() 
       }
       if (preferences.isPinchZoomEnabled) { 
           pinchZoom() 
       }
       // ...
   }
   editor.plugins(pluginSupplier)
   ```
   Since v2.1.0 there's no alternative to `fontSize`, `fontType` and
   some other properties in `PluginSupplier`, to configure these
   parameters use following methods:
   ```kotlin
   editor.setTextSize(14f) // previous `fontSize`
   editor.setTypeface(Typeface.MONOSPACE) // previous `fontType`
   editor.setHorizontallyScrolling(false) // previous `wordWrap`
   
   editor.softKeyboard = false // previous `softKeyboard`
   editor.useSpacesInsteadOfTabs = true // previous `useSpacesInsteadOfTabs`
   editor.tabWidth = 4 // previous `tabWidth`
   ```
2. `FindParams` model now has `query` property, which previously was
   passed to a `TextProcessor.find()` method as the first argument:
   ```kotlin
   // Before
   val params = FindParams(
       regex = false,
       matchCase = true,
       wordsOnly = true
   )
   editor.find("function", params)
   
   // After
   val params = FindParams(
       query = "function",
       regex = false,
       matchCase = true,
       wordsOnly = true
   )
   editor.find(params)
   ```
3. If you're using custom themes, the `SyntaxScheme` class was removed
   and all it's properties were moved in `ColorScheme` itself.
4. If you're using custom languages:
   1. Remove `enqueue()` and `cancel()` methods in `LanguageStyler`. The
      `execute()` method now invoked on the background thread, so now
      you don't need to write the asynchronous work by yourself.
   2. The `execute()` method now takes `ColorScheme` as the parameter
      since the `SyntaxScheme` class was removed.
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

## v1.3.0 -> v2.0.0

Migration steps:
1. Change the package name from `com.brackeys.ui` to
   `com.blacksquircle.ui`
2. Everything else remains the same

---

## v1.2.1 -> v1.3.0

Migration steps:
1. If you're using custom themes, add new `variableColor` property to the
   `SyntaxScheme` object

---

## v1.2.0 -> v1.2.1

Migration steps:
1. Change the groupId from `com.brackeys.ui` to `com.blacksquircle.ui`
   as shown below:
   ```groovy
   // Before
   implementation 'com.brackeys.ui:editorkit:1.2.0'
   
   // After
   implementation 'com.blacksquircle.ui:editorkit:1.2.1'
   ```
2. The package name `com.brackeys.ui` remains the same, but it will be
   changed in the future

---

## v1.1.0 -> v1.2.0

Migration steps:
1. If you're using `TextScroller`, rename it's `link()` method to
   `attachTo()` as shown below:
   ```kotlin
   // Before
   scroller.link(editor)
   
   // After
   scroller.attachTo(editor)
   ```

---

## v1.0.1 -> v1.1.0

Migration steps:
1. Rename `Config` to `EditorConfig` as shown below:
   ```kotlin
   // Before
   editor.config = Config(...)
   
   // After
   editor.editorConfig = EditorConfig(...)
   ```

---

## v1.0.0 -> v1.0.1

Migration steps:
1. Rename `ShortcutListener` to `OnShortcutListener` as shown below:
   ```kotlin
   // Before
   editor.shortcutListener = object : ShortcutListener { ... }
   
   // After
   editor.onShortcutListener = object : OnShortcutListener { ... }
   ```