# Brackeys IDE

**Brackeys IDE** is a fast and free multi-language code editor for Android.

![Android CI](https://github.com/massivemadness/Brackeys-IDE/workflows/Android%20CI/badge.svg) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

<img src="https://raw.githubusercontent.com/massivemadness/Brackeys-IDE/master/.github/images/carbon.png" width="700" />

---

# Table of Contents

## EditorKit

1. [Gradle Dependency](#gradle-dependency)
2. [The Basics](#the-basics)
3. [More Options](#more-options)
   1. [Config](#config)
   2. [Text Scroller](#text-scroller)
4. [Code Suggestions](#code-suggestions)
5. [Undo Redo](#undo-redo)
6. [Navigation](#navigation)
   1. [Text Navigation](#text-navigation)
   2. [Find Replace](#find-replace)
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

[ ![jCenter](https://api.bintray.com/packages/massivemadness/Brackeys-IDE/editorkit/images/download.svg) ](https://bintray.com/massivemadness/Brackeys-IDE/editorkit/_latestVersion)

## Gradle Dependency

Add this to your module's `build.gradle` file:

```gradle
dependencies {

    implementation 'com.brackeys.ui:editorkit:1.0.0'
}
```

The `editorkit` module **does not** provide support for syntax highlighting, you need to add specific language dependency.
You can see list of available languages [here](#languages-1).

---

## The Basics

**First,** you need to add `TextProcessor` in your layout:

```xml
<com.brackeys.ui.editorkit.widget.TextProcessor
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
import com.brackeys.ui.editorkit.utils.UndoStack

editor.undoStack = UndoStack()
editor.redoStack = UndoStack()
```

Now you can begin using the code editor.

---

## More Options

### Config

You can change the default code editor's behavior by passing the `Config` object to it:

```kotlin
import com.brackeys.ui.editorkit.model.Config

editor.config = Config(
    fontSize = 14f, // text size, including the line numbers
    fontType = Typeface.MONOSPACE, // typeface, including the line numbers

    wordWrap = true, // whether the word wrap enabled
    codeCompletion = true, // whether the code suggestions will shown
    pinchZoom = true, // whether the zoom gesture enabled
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

To attach the fast scroller you need to add `TextScroller` in your layout:

```xml
<com.brackeys.ui.editorkit.widget.TextScroller
    android:layout_width="30dp"
    android:layout_height="match_parent"
    android:id="@+id/scroller"
    app:thumbNormal="@drawable/fastscroll_normal"
    app:thumbDragging="@drawable/fastscroll_pressed"
    app:thumbTint="@color/blue"/>
```

Now you need to pass a reference to a view inside `link` method:

```kotlin
val editor = findViewById<TextProcessor>(R.id.editor)
val scroller = findViewById<TextScroller>(R.id.scroller)

scroller.link(editor)
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
import com.brackeys.ui.editorkit.adapter.SuggestionAdapter
import com.brackeys.ui.language.base.model.Suggestion

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

You can enable/disable suggestions dynamically by changing the `codeCompletion` parameter in editor's [Config](#config).

---

## Undo Redo

The `TextProcessor` supports undo/redo operations by calling following methods:

```kotlin
// You should always check the ability to undo before undoing
if (editor.canUndo()) {
    editor.undo()
}

// You should always check the ability to redo before redoing
if (editor.canRedo()) {
    editor.redo()
}
```

Also you may have a use-case when you want to update the undo/redo buttons visibility or other UI, this can be achieved by adding the `OnUndoRedoChangedListener` to your code editor:

```kotlin
import com.brackeys.ui.editorkit.listener.OnUndoRedoChangedListener

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

TODO: add more info

### Text Navigation

TODO: add more info

### Find Replace

TODO: add more info

### Shortcuts

TODO: add more info

---

## Theming

The `editorkit` module provides you some predefined color schemes in `EditorTheme` class:

```kotlin
import com.brackeys.ui.editorkit.theme.EditorTheme

editor.colorScheme = EditorTheme.DARCULA // default

// or you can use one of these:
EditorTheme.MONOKAI
EditorTheme.OBSIDIAN
EditorTheme.LADIES_NIGHT
EditorTheme.TOMORROW_NIGHT
EditorTheme.VISUAL_STUDIO_2013

```

You can also create custom color scheme by changing the `ColorScheme` properties:

```kotlin
import com.brackeys.ui.editorkit.model.ColorScheme
import com.brackeys.ui.language.base.model.SyntaxScheme

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

[ ![jCenter](https://api.bintray.com/packages/massivemadness/Brackeys-IDE/language-base/images/download.svg) ](https://bintray.com/massivemadness/Brackeys-IDE/language-base/_latestVersion)

## Gradle Dependency

Select your language and add it's dependency to your module's `build.gradle` file:

```gradle
dependencies {

    implementation 'com.brackeys.ui:language-actionscript:1.0.0'
    implementation 'com.brackeys.ui:language-base:1.0.0' // for custom language
    implementation 'com.brackeys.ui:language-c:1.0.0'
    implementation 'com.brackeys.ui:language-cpp:1.0.0'
    implementation 'com.brackeys.ui:language-csharp:1.0.0'
    implementation 'com.brackeys.ui:language-html:1.0.0'
    implementation 'com.brackeys.ui:language-java:1.0.0'
    implementation 'com.brackeys.ui:language-javascript:1.0.0'
    implementation 'com.brackeys.ui:language-json:1.0.0'
    implementation 'com.brackeys.ui:language-kotlin:1.0.0'
    implementation 'com.brackeys.ui:language-lisp:1.0.0'
    implementation 'com.brackeys.ui:language-lua:1.0.0'
    implementation 'com.brackeys.ui:language-markdown:1.0.0'
    implementation 'com.brackeys.ui:language-plaintext:1.0.0'
    implementation 'com.brackeys.ui:language-python:1.0.0'
    implementation 'com.brackeys.ui:language-sql:1.0.0'
    implementation 'com.brackeys.ui:language-visualbasic:1.0.0'
    implementation 'com.brackeys.ui:language-xml:1.0.0'
}
```

---

## Custom Language

**First,** add this to your module's `build.gradle` file:

```gradle
dependencies {

    implementation 'com.brackeys.ui:language-base:1.0.0'
}
```

**Second,** implement the `Language` interface:

```kotlin
import com.brackeys.ui.language.base.Language
import com.brackeys.ui.language.base.parser.LanguageParser
import com.brackeys.ui.language.base.provider.SuggestionProvider
import com.brackeys.ui.language.base.styler.LanguageStyler

class CustomLanguage : Language {

    override fun getName(): String {
        return "custom language"
    }

    override fun getParser(): LanguageParser {
        return ...
    }

    override fun getProvider(): SuggestionProvider {
        return ...
    }

    override fun getStyler(): LanguageStyler {
        return ...
    }
}
```

Every language consist of 3 key components:
1. **LanguageParser** is responsible for analyzing the source code. The code editor does not use this component directly.
2. **SuggestionProvider** is responsible for collecting the names of functions, fields, and keywords within your file scope. The code editor use this component to display the list of code suggestions.
3. **LanguageStyler** is responsible for syntax highlighting. The code editor use this component to display all spans on the screen.

### LanguageParser

TODO: add more info

### SuggestionProvider

`SuggestionProvider` is an interface which provides code suggestions to display them in a `TextProcessor`.

TODO: add more info

### LanguageStyler

TODO: add more info