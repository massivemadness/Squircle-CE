# Squircle CE

**Squircle CE** is a fast and free multi-language code editor for Android.

![Android CI](https://github.com/massivemadness/Squircle-CE/workflows/Android%20CI/badge.svg) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

<img src="https://raw.githubusercontent.com/massivemadness/Squircle-CE/master/.github/images/carbon.png" width="700" />

---

# Table of Contents

## EditorKit

1. [Gradle Dependency](#gradle-dependency)
2. [The Basics](#the-basics)
3. [More Options](#more-options)
   1. [Configuration](#configuration)
   2. [Text Scroller](#text-scroller)
4. [Code Suggestions](#code-suggestions)
5. [Undo Redo](#undo-redo)
6. [Navigation](#navigation)
   1. [Text Navigation](#text-navigation)
   2. [Find and Replace](#find-and-replace)
   3. [Shortcuts](#shortcuts)
7. [Theming](#theming)
8. [Custom Plugin](#custom-plugin)

## Languages

1. [Gradle Dependency](#gradle-dependency-1)
2. [Custom Language](#custom-language)
   1. [LanguageParser](#languageparser)
   2. [SuggestionProvider](#suggestionprovider)
   3. [LanguageStyler](#languagestyler)

---

# EditorKit

The `editorkit` module provides code editor without any support for
programming languages.  
***If you are upgrading from any older version, please have a look at
the [migration guide](MIGRATION-GUIDE.md).***  
Please note that this library only supports Kotlin.

[![MavenCentral](https://img.shields.io/maven-central/v/com.blacksquircle.ui/editorkit?label=Download)](https://repo1.maven.org/maven2/com/blacksquircle/ui/editorkit/)

## Gradle Dependency

Add this to your module's `build.gradle` file:

```gradle
dependencies {
  ...
  implementation 'com.blacksquircle.ui:editorkit:2.4.0'
}
```

The `editorkit` module **does not** provide support for syntax
highlighting, you need to add specific language dependency. You can see
list of available languages [here](#languages-1).

---

## The Basics

**First,** you need to add `TextProcessor` in your layout:

```xml
<com.blacksquircle.ui.editorkit.widget.TextProcessor
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="top|start"
    android:id="@+id/editor" />
```

**Second,** you need to provide a `Language` object to support syntax
highlighting by using following code:

```kotlin
val editor = findViewById<TextProcessor>(R.id.editor)

editor.language = JavaScriptLanguage() // or any other language you want
```

**Third**, you need to call `setTextContent` to set the text. **Don't
use the default `setText` method.**

```kotlin
editor.setTextContent("your code here")
```

Also you might want to use `setTextContent(PrecomputedTextCompat)` if
you're working with large text files.

**Finally**, after you set the text you need to clear undo/redo history
because you don't want to keep the change history of previous file:

```kotlin
import com.blacksquircle.ui.editorkit.model.UndoStack

editor.undoStack = UndoStack()
editor.redoStack = UndoStack()
```

Now you can begin using the code editor.

---

## More Options

### Configuration

You can change the default code editor behavior by using Plugin DSL as
shown below:

```kotlin
val pluginSupplier = PluginSupplier.create {
    pinchZoom { // whether the zoom gesture enabled
        minTextSize = 10f
        maxTextSize = 20f 
    }
    lineNumbers {
        lineNumbers = true // line numbers visibility
        highlightCurrentLine = true // whether the current line will be highlighted
    }
    highlightDelimiters() // highlight open/closed brackets beside the cursor
    autoIndentation {
        autoIndentLines = true // whether the auto indentation enabled
        autoCloseBrackets = true // automatically close open parenthesis/bracket/brace
        autoCloseQuotes = true // automatically close single/double quote when typing
    }
}
editor.plugins(pluginSupplier)
```

To enable/disable plugins in runtime, surround necessary methods with
`if (enabled) { ... }` operator:

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

**Remember:** everytime you call `editor.plugins(pluginSupplier)` it
compares current plugin list with the new one, and then detaches plugins
that doesn't exists in the `PluginSupplier`.

### Text Scroller

To attach the text scroller you need to add `TextScroller` in layout:

```xml
<com.blacksquircle.ui.editorkit.widget.TextScroller
    android:layout_width="30dp"
    android:layout_height="match_parent"
    android:id="@+id/scroller"
    app:thumbNormal="@drawable/fastscroll_normal"
    app:thumbDragging="@drawable/fastscroll_pressed"
    app:thumbTint="@color/blue" />
```

Now you need to pass a reference to a view inside `attachTo` method:

```kotlin
val editor = findViewById<TextProcessor>(R.id.editor)
val scroller = findViewById<TextScroller>(R.id.scroller)

scroller.attachTo(editor)

// or using Plugin DSL:

val pluginSupplier = PluginSupplier.create {
    ...
    textScroller {
        scroller = findViewById<TextScroller>(R.id.scroller)
    }
}
```

---

## Code Suggestions

When you working with a code editor you want to see the list of code
suggestion. *(Note that you have to provide a `Language` object before
start using it.)*

**First**, you need to create a layout file that will represent the
suggestion item inside dropdown menu:

```xml
<!-- item_suggestion.xml -->
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:singleLine="true"
    android:padding="6dp"
    android:textSize="12sp"
    android:typeface="monospace"
    android:id="@+id/title" />
```

**Second**, you need to create custom `SuggestionAdapter`:

```kotlin
class AutoCompleteAdapter(context: Context) : SuggestionAdapter(context, R.layout.item_suggestion) {

    override fun createViewHolder(parent: ViewGroup): SuggestionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_suggestion, parent, false)
        return AutoCompleteViewHolder(view)
    }
    
    class AutoCompleteViewHolder(itemView: View) : SuggestionViewHolder(itemView) {
    
        private val title: TextView = itemView.findViewById(R.id.title)
        
        override fun bind(suggestion: Suggestion?, query: String) {
            title.text = suggestion?.text
        }
    }
}
```

**Third**, enable the code completion plugin and set
`SuggestionAdapter`:

```kotlin
val pluginSupplier = PluginSupplier.create {
    ...
    codeCompletion {
        suggestionAdapter = AutoCompleteAdapter(this)
    }
}
```

**UPD:** If you having an issues with the popup position (e.g vertical
offset), this might be solved by explicitly setting
[android:dropDownAnchor](https://developer.android.com/reference/android/widget/AutoCompleteTextView#attr_android:dropDownAnchor)
in XML.

---

## Undo Redo

The `TextProcessor` supports undo/redo operations, but remember that you
**must** check the ability to undo/redo before calling actual methods:

```kotlin
// Undo
if (editor.canUndo()) {
    editor.undo()
}

// Redo
if (editor.canRedo()) {
    editor.redo()
}
```

Also you may have a use case when you want to update undo/redo buttons
visibility or other UI after the text replacements is done, this can be
achieved by adding `OnUndoRedoChangedListener`:

```kotlin
editor.onUndoRedoChangedListener = object : OnUndoRedoChangedListener {
    override fun onUndoRedoChanged() {
        val canUndo = editor.canUndo()
        val canRedo = editor.canRedo()
        
        // ...
    }
}
```

---

## Navigation

### Text Navigation

You can use these extension methods to navigate in text:

```kotlin
editor.moveCaretToStartOfLine()
editor.moveCaretToEndOfLine()
editor.moveCaretToPrevWord()
editor.moveCaretToNextWord()
```

...or use «Go to Line» feature to place the caret at the specific line:

```kotlin
import com.blacksquircle.ui.editorkit.exception.LineException

try {
    editor.gotoLine(lineNumber)
} catch (e: LineException) {
    Toast.makeText(this, "Line does not exists", Toast.LENGTH_SHORT).show()
}
```

### Find and Replace

The `TextProcessor` has built-in support for search and replace
operations, including:
- Search forward or backward
- Regular Expressions
- Match Case
- Words Only

The class itself contains self-explanatory methods for all your
searching needs:
- `find(params)` - Find all possible results in text with provided options.
- `replaceFindResult(replaceText)` - Finds current match and replaces it with new text.
- `replaceAllFindResults(replaceText)` - Finds all matches and replaces them with the new text.
- `findNext()` - Finds the next match and scrolls to it.
- `findPrevious()` - Finds the previous match and scrolls to it.
- `clearFindResultSpans()` - Clears all find spans on the screen. Call this method when you're done searching.

```kotlin
import com.blacksquircle.ui.editorkit.model.FindParams

val params = FindParams(
    query = "function", // text to find
    regex = false, // regular expressions
    matchCase = true, // case sensitive
    wordsOnly = true // words only
)

editor.find(params)

// To navigate between results use findNext() and findPrevious()
```

### Shortcuts

If you're using bluetooth keyboard you probably want to use keyboard
shortcuts to write your code faster. To support the keyboard shortcuts
you need to enable the shortcuts plugin and set `OnShortcutListener`:

```kotlin
val pluginSupplier = PluginSupplier.create {
    ...
    shortcuts {
        onShortcutListener = object : OnShortcutListener {
            override fun onShortcut(shortcut: Shortcut): Boolean {
                val (ctrl, shift, alt, keyCode) = shortcut
                return when {
                    ctrl && keyCode == KeyEvent.KEYCODE_DPAD_LEFT -> editor.moveCaretToStartOfLine()
                    ctrl && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT -> editor.moveCaretToEndOfLine()
                    alt && keyCode == KeyEvent.KEYCODE_DPAD_LEFT -> editor.moveCaretToPrevWord()
                    alt && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT -> editor.moveCaretToNextWord()
                    // ...
                    else -> false
                }
            }
        }
    }
}
```

The `onShortcut` method will be invoked only if at least one of
following keys is pressed: <kbd>ctrl</kbd>, <kbd>shift</kbd>,
<kbd>alt</kbd>.  
You might already noticed that you have to return a `Boolean` value as
the result of `onShortcut` method. Return `true` if the listener has
consumed the shortcut event, `false` otherwise.

---

## Theming

The `editorkit` module includes some default themes in the `EditorTheme`
class:

```kotlin
editor.colorScheme = EditorTheme.DARCULA // default

// or you can use one of these:
EditorTheme.MONOKAI
EditorTheme.OBSIDIAN
EditorTheme.LADIES_NIGHT
EditorTheme.TOMORROW_NIGHT
EditorTheme.VISUAL_STUDIO_2013

```

You can also write your own theme by changing the `ColorScheme`
properties. The example below shows how you can programmatically load
the color scheme:

```kotlin
editor.colorScheme = ColorScheme(
    textColor = Color.parseColor("#C8C8C8"),
    cursorColor = Color.parseColor("#BBBBBB"),
    backgroundColor = Color.parseColor("#232323"),
    gutterColor = Color.parseColor("#2C2C2C"),
    gutterDividerColor = Color.parseColor("#555555"),
    gutterCurrentLineNumberColor = Color.parseColor("#FFFFFF"),
    gutterTextColor = Color.parseColor("#C6C8C6"),
    selectedLineColor = Color.parseColor("#141414"),
    selectionColor = Color.parseColor("#454464"),
    suggestionQueryColor = Color.parseColor("#4F98F7"),
    findResultBackgroundColor = Color.parseColor("#1C3D6B"),
    delimiterBackgroundColor = Color.parseColor("#616161"),
    numberColor = Color.parseColor("#BACDAB"),
    operatorColor = Color.parseColor("#DCDCDC"),
    keywordColor = Color.parseColor("#669BD1"),
    typeColor = Color.parseColor("#669BD1"),
    langConstColor = Color.parseColor("#669BD1"),
    preprocessorColor = Color.parseColor("#C49594"),
    variableColor = Color.parseColor("#9DDDFF"),
    methodColor = Color.parseColor("#71C6B1"),
    stringColor = Color.parseColor("#CE9F89"),
    commentColor = Color.parseColor("#6BA455"),
    tagColor = Color.parseColor("#DCDCDC"),
    tagNameColor = Color.parseColor("#669BD1"),
    attrNameColor = Color.parseColor("#C8C8C8"),
    attrValueColor = Color.parseColor("#CE9F89"),
    entityRefColor = Color.parseColor("#BACDAB")
)
```

## Custom Plugin

Since v2.1.0 the [EditorKit](#editorkit) library supports writing custom
plugins to extend it's functionality. If you're using the latest version, 
you might be familiar with `PluginSupplier` and know how to use it's DSL. 
See [More Options](#more-options) for info.

**First,** you need to create a class which extends the `EditorPlugin`
and provide it's id in the constructor:

```kotlin
class CustomPlugin : EditorPlugin("custom-plugin-id") {

    var publicProperty = true

    override fun onAttached(editText: TextProcessor) {
        super.onAttached(editText)
        // TODO enable your feature here
    }
    
    override fun onDetached(editText: TextProcessor) {
        super.onDetached(editText)
        // TODO disable your feature here
    }
}
```

**Second,** you can override lifecycle methods, for example `afterDraw`,
which invoked immediately after `onDraw(Canvas)` in code editor:

```kotlin
class CustomPlugin : EditorPlugin("custom-plugin-id") {
    
    var publicProperty = true
    
    private val dividerPaint = Paint().apply {
        color = Color.GRAY
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (publicProperty) {
            var i = editText.topVisibleLine
            while (i <= editText.bottomVisibleLine) {
                val startX = editText.paddingStart + editText.scrollX
                val startY = editText.paddingTop + editText.layout.getLineBottom(i)
                val stopX = editText.paddingLeft + editText.layout.width + editText.paddingRight
                val stopY = editText.paddingTop + editText.layout.getLineBottom(i)
                canvas?.drawLine( // draw divider for each visible line
                    startX.toFloat(), startY.toFloat(),
                    stopX.toFloat(), stopY.toFloat(),
                    dividerPaint
                )
                i++
            }
        }
    }
}
```

**Third,** create an extension function to improve code readability when
adding your plugin to a `PluginSupplier`:

```kotlin
fun PluginSupplier.lineDividers(block: CustomPlugin.() -> Unit = {}) {
    plugin(CustomPlugin(), block)
}
```

**Finally,** you can attach your plugin using DSL:

```kotlin
val pluginSupplier = PluginSupplier.create {
    lineDividers {
        publicProperty = true // whether should draw the dividers
    }
    ...
}
editor.plugins(pluginSupplier)
```

---

# Languages

The language modules provides support for programming languages. This
includes syntax highlighting, code suggestions and source code parser.
*(Note that source code parser currently works only in
`language-javascript` module, but it will be implemented for more
languages in the future)*

[![MavenCentral](https://img.shields.io/maven-central/v/com.blacksquircle.ui/language-base?label=Download)](https://repo1.maven.org/maven2/com/blacksquircle/ui/language-base/)

## Gradle Dependency

Select your language and add it's dependency to your module's
`build.gradle` file:

```gradle
dependencies {
  ...
  implementation 'com.blacksquircle.ui:language-actionscript:2.4.0'
  implementation 'com.blacksquircle.ui:language-base:2.4.0' // for custom language
  implementation 'com.blacksquircle.ui:language-c:2.4.0'
  implementation 'com.blacksquircle.ui:language-cpp:2.4.0'
  implementation 'com.blacksquircle.ui:language-csharp:2.4.0'
  implementation 'com.blacksquircle.ui:language-groovy:2.4.0'
  implementation 'com.blacksquircle.ui:language-html:2.4.0'
  implementation 'com.blacksquircle.ui:language-java:2.4.0'
  implementation 'com.blacksquircle.ui:language-javascript:2.4.0'
  implementation 'com.blacksquircle.ui:language-json:2.4.0'
  implementation 'com.blacksquircle.ui:language-julia:2.4.0'
  implementation 'com.blacksquircle.ui:language-kotlin:2.4.0'
  implementation 'com.blacksquircle.ui:language-lisp:2.4.0'
  implementation 'com.blacksquircle.ui:language-lua:2.4.0'
  implementation 'com.blacksquircle.ui:language-markdown:2.4.0'
  implementation 'com.blacksquircle.ui:language-php:2.4.0'
  implementation 'com.blacksquircle.ui:language-plaintext:2.4.0'
  implementation 'com.blacksquircle.ui:language-python:2.4.0'
  implementation 'com.blacksquircle.ui:language-ruby:2.4.0'
  implementation 'com.blacksquircle.ui:language-shell:2.4.0'
  implementation 'com.blacksquircle.ui:language-smali:2.4.0'
  implementation 'com.blacksquircle.ui:language-sql:2.4.0'
  implementation 'com.blacksquircle.ui:language-toml:2.4.0'
  implementation 'com.blacksquircle.ui:language-typescript:2.4.0'
  implementation 'com.blacksquircle.ui:language-visualbasic:2.4.0'
  implementation 'com.blacksquircle.ui:language-xml:2.4.0'
  implementation 'com.blacksquircle.ui:language-yaml:2.4.0'
}
```

---

## Custom Language

**First,** add this to your module's `build.gradle` file:

```gradle
dependencies {
  ...
  implementation 'com.blacksquircle.ui:language-base:2.4.0'
}
```

**Second,** implement the `Language` interface:

```kotlin
import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.base.parser.LanguageParser
import com.blacksquircle.ui.language.base.provider.SuggestionProvider
import com.blacksquircle.ui.language.base.styler.LanguageStyler

class CustomLanguage : Language {

    override val languageName = "custom language"

    override fun getParser(): LanguageParser {
        return CustomParser()
    }

    override fun getProvider(): SuggestionProvider {
        return CustomProvider()
    }

    override fun getStyler(): LanguageStyler {
        return CustomStyler()
    }
}
```

Every language consist of 3 key components:
1. **LanguageParser** is responsible for analyzing the source code. The
   code editor does not use this component directly.
2. **SuggestionProvider** is responsible for collecting the names of
   functions, fields, and keywords within your file scope. The code
   editor use this component to display the list of code suggestions.
3. **LanguageStyler** is responsible for syntax highlighting. The code
   editor use this component to display syntax highlight spans on the
   screen.

### LanguageParser

`LanguageParser` is an interface which detects syntax errors so you can
display them in the `TextProcessor` later.

To create a custom parser you need to implement `execute` method that
will return a `ParseResult`.  
If `ParseResult` contains an exception it means that the source code
can't compile and contains syntax errors. You can highlight an error
line by calling `editor.setErrorLine(lineNumber)` method.

**Remember** that you **shouldn't** use this method on the main thread.

```kotlin
class CustomParser : LanguageParser {

    override fun execute(structure: TextStructure): ParseResult {
        // TODO Implement parser
        val lineNumber = 0
        val columnNumber = 0
        val parseException = ParseException("describe exception here", lineNumber, columnNumber)
        return ParseResult(parseException)
    }
}
```

### SuggestionProvider

`SuggestionProvider` is an interface which provides code suggestions to
display them in the `TextProcessor`.

The text scanning is done on a per-line basis. When the user edits code
on a single line, that line is re-scanned by the current
`SuggestionsProvider` implementation, so you can keep your suggestions
list up to date. This is done by calling the `processLine` method. This
method is responsible for parsing a line of text and saving the code
suggestions for that line.

After calling `setTextContent` the code editor will call `processAllLines` 
to find all possible code suggestions.

```kotlin
class CustomProvider : SuggestionProvider {

    // You can use WordsManager
    // if you don't want to write the language-specific implementation
    private val wordsManager = WordsManager()

    override fun getAll(): Set<Suggestion> {
        return wordsManager.getWords()
    }

    override fun processAllLines(structure: TextStructure) {
        wordsManager.processAllLines(structure)
    }

    override fun processLine(lineNumber: Int, text: CharSequence) {
        wordsManager.processLine(lineNumber, text)
    }

    override fun deleteLine(lineNumber: Int) {
        wordsManager.deleteLine(lineNumber)
    }

    override fun clearLines() {
        wordsManager.clearLines()
    }
}
```

### LanguageStyler

`LanguageStyler` is an interface which provides syntax highlight spans
to display them in the `TextProcessor`.

The `execute` method will be executed on the background thread every
time the text changes. You can use regex or lexer to find the keywords
in text.

**Remember:** the more spans you add, the more time it takes to render
on the main thread.

```kotlin
class CustomStyler : LanguageStyler {

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val syntaxHighlightResults = mutableListOf<SyntaxHighlightResult>()
        
        // TODO Implement syntax highlighting
        
        return syntaxHighlightResults
    }
}
```