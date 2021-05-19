# Squircle IDE

**Squircle IDE** is a fast and free multi-language code editor for Android.

![Android CI](https://github.com/massivemadness/Squircle-IDE/workflows/Android%20CI/badge.svg) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

<img src="https://raw.githubusercontent.com/massivemadness/Squircle-IDE/master/.github/images/carbon.png" width="700" />

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

## Languages

1. [Gradle Dependency](#gradle-dependency-1)
2. [Custom Language](#custom-language)
   1. [LanguageParser](#languageparser)
   2. [SuggestionProvider](#suggestionprovider)
   3. [LanguageStyler](#languagestyler)

---

# EditorKit

The `editorkit` module provides code editor without any support for programming languages.

[ ![MavenCentral](https://img.shields.io/maven-central/v/com.blacksquircle.ui/editorkit?label=Download) ](https://repo1.maven.org/maven2/com/blacksquircle/ui/editorkit/)

## Gradle Dependency

Add this to your module's `build.gradle` file:

```gradle
dependencies {
  ...
  implementation 'com.blacksquircle.ui:editorkit:2.0.0'
}
```

The `editorkit` module **does not** provide support for syntax highlighting, you need to add specific language dependency.
You can see list of available languages [here](#languages-1).

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

**Second,** you need to provide a `Language` object to support syntax highlighting by using following code:

```kotlin
val editor = findViewById<TextProcessor>(R.id.editor)

editor.language = JavaScriptLanguage() // or any other language you want
```

**Third**, you need to call `setTextContent` to set the text. **Avoid using the default `setText` method.**

```kotlin
editor.setTextContent("your code here")
```

Also you might want to use `setTextContent(PrecomputedTextCompat)` if you're working with large text files.

**Finally**, after you set the text you need to clear undo/redo history because you don't want to keep the change history of other files.

```kotlin
import com.blacksquircle.ui.editorkit.utils.UndoStack

editor.undoStack = UndoStack()
editor.redoStack = UndoStack()
```

Now you can begin using the code editor.

---

## More Options

### Configuration

You can change the default code editor's behavior by passing the `EditorConfig` object to it:

```kotlin
editor.editorConfig = EditorConfig(
    fontSize = 14f, // text size, including the line numbers
    fontType = Typeface.MONOSPACE, // typeface, including the line numbers

    wordWrap = true, // whether the word wrap enabled
    codeCompletion = true, // whether the code suggestions will shown
    pinchZoom = true, // whether the zoom gesture enabled
    lineNumbers = true, // line numbers visibility
    highlightCurrentLine = true, // whether the current line will be highlighted
    highlightDelimiters = true, // highlight open/closed brackets beside the cursor

    softKeyboard = false, // whether the fullscreen editing keyboard will shown

    autoIndentation = true, // whether the auto indentation enabled
    autoCloseBrackets = true, // automatically close open parenthesis/bracket/brace
    autoCloseQuotes = true, // automatically close single/double quote when typing
    useSpacesInsteadOfTabs = true, // insert spaces instead of tabs when using auto-indentation
    tabWidth = 4 // the tab width, works together with `useSpacesInsteadOfTabs`
)
```

### Text Scroller

To attach the text scroller you need to add `TextScroller` in your layout:

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
```

---

## Code Suggestions

When you working with a code editor you want to see the list of code suggestion. *(Note that you have to provide a `Language` object before start using it.)*

**First**, you need to create a layout file that will represent the suggestion item inside dropdown menu:

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

...and pass it to your code editor:

```kotlin
editor.suggestionAdapter = AutoCompleteAdapter(this)
```

You can enable/disable suggestions dynamically by changing the `codeCompletion` parameter in [EditorConfig](#configuration).

**UPD:** If you having an issues with the popup position (e.g vertical offset), this might be solved by explicitly setting [android:dropDownAnchor](https://developer.android.com/reference/android/widget/AutoCompleteTextView#attr_android:dropDownAnchor) in XML.

---

## Undo Redo

The `TextProcessor` supports undo/redo operations, but remember that you **must** check the ability to undo/redo before calling actual methods:

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

Also you may have a use case when you want to update undo/redo buttons visibility or other UI after the text replacements is done, this can be achieved by adding `OnUndoRedoChangedListener`:

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

You can use these helper methods to navigate in text:

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

The `TextProcessor` has built-in support for search and replace operations, including:
- Search forward or backward
- Regular Expressions
- Match Case
- Words Only

The class itself contains self-explanatory methods for all your searching needs:
- `find(searchString, findParams)` - Find all possible results in text with provided options.
- `replaceFindResult(replaceText)` - Finds current match and replaces it with new text.
- `replaceAllFindResults(replaceText)` - Finds all matches and replaces them with the new text.
- `findNext()` - Finds the next match and scrolls to it.
- `findPrevious()` - Finds the previous match and scrolls to it.
- `clearFindResultSpans()` - Clears all find spans on the screen. Call this method when you're done searching.

```kotlin
import com.blacksquircle.ui.editorkit.model.FindParams

val findParams = FindParams(
    regex = false, // whether the regex will be used
    matchCase = true, // case sensitive
    wordsOnly = true // words only
)

editor.find("function", findParams)

// To navigate between results use findNext() and findPrevious()
```

### Shortcuts

If you're using bluetooth keyboard you probably want to use keyboard shortcuts to write your code faster. To support the keyboard shortcuts you need to add `OnShortcutListener`:

```kotlin
editor.onShortcutListener = object : OnShortcutListener {
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
```

The `onShortcut` method will be invoked only if at least one of following keys is pressed: <kbd>ctrl</kbd>, <kbd>shift</kbd>, <kbd>alt</kbd>.  
You might already noticed that you have to return a `Boolean` value as the result of `onShortcut` method.
Return `true` if the listener has consumed the shortcut event, `false` otherwise.

---

## Theming

The `editorkit` module includes some default themes in the `EditorTheme` class:

```kotlin
editor.colorScheme = EditorTheme.DARCULA // default

// or you can use one of these:
EditorTheme.MONOKAI
EditorTheme.OBSIDIAN
EditorTheme.LADIES_NIGHT
EditorTheme.TOMORROW_NIGHT
EditorTheme.VISUAL_STUDIO_2013

```

You can also write your own theme by changing the `ColorScheme` properties. The example below shows how you can programmatically load the color scheme:

```kotlin
editor.colorScheme = ColorScheme(
    textColor = Color.parseColor("#C8C8C8"),
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
    syntaxScheme = SyntaxScheme(
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
)
```

---

# Languages

The language modules provides support for programming languages. This includes syntax highlighting, code suggestions and source code parser.
*(Note that source code parser currently works only in `language-javascript` module, but it will be implemented for more languages soon)*

[ ![MavenCentral](https://img.shields.io/maven-central/v/com.blacksquircle.ui/language-base?label=Download) ](https://repo1.maven.org/maven2/com/blacksquircle/ui/language-base/)

## Gradle Dependency

Select your language and add it's dependency to your module's `build.gradle` file:

```gradle
dependencies {
  ...
  implementation 'com.blacksquircle.ui:language-actionscript:2.0.0'
  implementation 'com.blacksquircle.ui:language-base:2.0.0' // for custom language
  implementation 'com.blacksquircle.ui:language-c:2.0.0'
  implementation 'com.blacksquircle.ui:language-cpp:2.0.0'
  implementation 'com.blacksquircle.ui:language-csharp:2.0.0'
  implementation 'com.blacksquircle.ui:language-groovy:2.0.0'
  implementation 'com.blacksquircle.ui:language-html:2.0.0'
  implementation 'com.blacksquircle.ui:language-java:2.0.0'
  implementation 'com.blacksquircle.ui:language-javascript:2.0.0'
  implementation 'com.blacksquircle.ui:language-json:2.0.0'
  implementation 'com.blacksquircle.ui:language-kotlin:2.0.0'
  implementation 'com.blacksquircle.ui:language-lisp:2.0.0'
  implementation 'com.blacksquircle.ui:language-lua:2.0.0'
  implementation 'com.blacksquircle.ui:language-markdown:2.0.0'
  implementation 'com.blacksquircle.ui:language-php:2.0.0'
  implementation 'com.blacksquircle.ui:language-plaintext:2.0.0'
  implementation 'com.blacksquircle.ui:language-python:2.0.0'
  implementation 'com.blacksquircle.ui:language-shell:2.0.0'
  implementation 'com.blacksquircle.ui:language-sql:2.0.0'
  implementation 'com.blacksquircle.ui:language-typescript:2.0.0'
  implementation 'com.blacksquircle.ui:language-visualbasic:2.0.0'
  implementation 'com.blacksquircle.ui:language-xml:2.0.0'
}
```

---

## Custom Language

**First,** add this to your module's `build.gradle` file:

```gradle
dependencies {
  ...
  implementation 'com.blacksquircle.ui:language-base:2.0.0'
}
```

**Second,** implement the `Language` interface:

```kotlin
import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.base.parser.LanguageParser
import com.blacksquircle.ui.language.base.provider.SuggestionProvider
import com.blacksquircle.ui.language.base.styler.LanguageStyler

class CustomLanguage : Language {

    override fun getName(): String {
        return "custom language"
    }

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
1. **LanguageParser** is responsible for analyzing the source code. The code editor does not use this component directly.
2. **SuggestionProvider** is responsible for collecting the names of functions, fields, and keywords within your file scope. The code editor use this component to display the list of code suggestions.
3. **LanguageStyler** is responsible for syntax highlighting. The code editor use this component to display all spans on the screen.

### LanguageParser

`LanguageParser` is an interface which detects syntax errors so you can display them in the `TextProcessor` later.

To create a custom parser you need to implement `execute` method that will return a `ParseResult`.  
If `ParseResult` contains an exception it means that the source code can't compile and contains syntax errors. You can highlight an error line by calling `editor.setErrorLine(lineNumber)` method.

**Remember** that you **shouldn't** use this method on the main thread.

```kotlin
class CustomParser : LanguageParser {

    override fun execute(name: String, source: String): ParseResult {
        // TODO Implement parser
        val lineNumber = 0
        val columnNumber = 0
        val parseException = ParseException("describe exception here", lineNumber, columnNumber)
        return ParseResult(parseException)
    }
}
```

### SuggestionProvider

`SuggestionProvider` is an interface which provides code suggestions to display them in the `TextProcessor`.

The text scanning is done on a per-line basis. When the user edits code on a single line, that line is re-scanned by the current `SuggestionsProvider` implementation, so you can keep your suggestions list up to date.
This is done by calling the `processLine` method. This method is responsible for parsing a line of text and saving the code suggestions for that line.

After calling `setTextContent` the code editor will call `processLine` for each line to find all possible code suggestions.

```kotlin
class CustomProvider : SuggestionProvider {

    // You can use WordsManager
    // if you don't want to write the language-specific implementation
    private val wordsManager = WordsManager()

    override fun getAll(): Set<Suggestion> {
        return wordsManager.getWords()
    }

    override fun processLine(lineNumber: Int, text: String) {
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

`LanguageStyler` is an interface which provides syntax highlight spans to display them in the `TextProcessor`.

The `execute` method will be executed on the main thread. That means the UI blocks during the execution and no interaction is possible for this period. The code editor never use this method directly.  
The `enqueue` method it's just asynchronous version of `execute` that will be called every time the text changes.  
You can use regex or lexer in the `execute` method to match all the spans in text.

**Remember:** the more spans you add, the more time it takes to render on the main thread.

```kotlin
class CustomStyler : LanguageStyler {

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        
        // TODO Implement syntax highlighting
        
        return syntaxHighlightSpans
    }

    // StylingResult it's just a callback (List<SyntaxHighlightSpan>) -> Unit
    override fun enqueue(sourceCode: String, syntaxScheme: SyntaxScheme, stylingResult: StylingResult) {
        task?.cancelTask()
        task = StylingTask(
            doAsync = { execute(sourceCode, syntaxScheme) },
            onSuccess = stylingResult
        )
        task?.executeTask()
    }

    override fun cancel() {
        task?.cancelTask()
        task = null
    }
}
```