# Migration Guide

This migration guide will help you adapt your existing code to match the
latest version of EditorKit library.

---

## v2.0.0 => v2.1.0

Migration steps:
1. TODO

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
1. Rename `TextScroller`'s `link()` method to `attachTo()`
2. Everything else remains the same

---

## v1.0.1 => v1.1.0

Migration steps:
1. Rename `Config` to `EditorConfig`
2. Everything else remains the same

---

## v1.0.0 => v1.0.1

Migration steps:
1. Rename `ShortcutListener` to `OnShortcutListener`
2. Everything else remains the same

