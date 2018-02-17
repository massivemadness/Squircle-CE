/*
 * Copyright (C) 2018 Light Team Software
 *
 * This file is part of ModPE IDE.
 *
 * ModPE IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ModPE IDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.KillerBLS.modpeide.manager.theming.ThemeIdentificator;

/**
 * "Оболочка" приложения. Класс для работы с настройками и другими данными.
 */
public class Wrapper {

    private SharedPreferences mManager;

    public Wrapper(Context context) {
        mManager = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //Preferences

    /**
     * Устанавливаем значение Read Only, необходим
     * для использования Switch-кнопок в {@link android.support.design.widget.NavigationView}.
     * @param readOnly - устанавливаемое значение для Read Only.
     */
    public void setReadOnly(boolean readOnly) {
        mManager.edit().putBoolean("READ_ONLY", readOnly).apply();
    }

    public boolean getReadOnly() {
        return mManager.getBoolean("READ_ONLY", false);
    }

    public void setSyntaxHighlight(boolean syntaxHighlight) {
        mManager.edit().putBoolean("SYNTAX_HIGHLIGHT", syntaxHighlight).apply();
    }

    public boolean getSyntaxHighlight() {
        return mManager.getBoolean("SYNTAX_HIGHLIGHT", true);
    }

    public int getMaxTabsCount() {
        return 5; //mManager.getInt...
    }

    public boolean getFullScreenMode() {
        return mManager.getBoolean("FULLSCREEN_MODE", false);
    }

    public boolean getConfirmExit() {
        return mManager.getBoolean("CONFIRM_EXIT", true);
    }

    public boolean getResumeSession() {
        return mManager.getBoolean("RESUME_SESSION", true);
    }

    public boolean getDisableSwipeGesture() {
        return mManager.getBoolean("DISABLE_SWIPE", false);
    }

    public boolean getImeKeyboard() {
        return mManager.getBoolean("USE_IME_KEYBOARD", false);
    }

    public String getCurrentTypeface() {
        return mManager.getString("FONT_TYPE", "droid_sans_mono");
    }

    public int getFontSize() {
        return mManager.getInt("FONT_SIZE", 14); //default
    }

    public boolean getWrapContent() {
        return mManager.getBoolean("WRAP_CONTENT", true);
    }

    public boolean getShowLineNumbers() {
        return mManager.getBoolean("SHOW_LINE_NUMBERS", true);
    }

    public boolean getBracketMatching() {
        return mManager.getBoolean("BRACKET_MATCHING", true);
    }

    public String getWorkingFolder() {
        return mManager.getString("FEXPLORER_WORKING_FOLDER",
                Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    public void setWorkingFolder(String newWorkingFolder) {
        mManager.edit().putString("FEXPLORER_WORKING_FOLDER", newWorkingFolder).apply();
    }

    public String getSortMode() {
        return mManager.getString("FILE_SORT_MODE", "SORT_BY_NAME");
    }

    public String getCurrentTheme() {
        return mManager.getString("THEME_RESOURCE", ThemeIdentificator.DARCULA);
    }

    public boolean getCreatingFilesAndFolders() {
        return mManager.getBoolean("ALLOW_CREATING_FILES", true);
    }

    public boolean getPushNotifications() {
        return mManager.getBoolean("PUSH_NOTIFICATIONS", true);
    }

    public boolean getHighlightCurrentLine() {
        return mManager.getBoolean("HIGHLIGHT_CURRENT_LINE", true);
    }

    public boolean getCodeCompletion() {
        return mManager.getBoolean("CODE_COMPLETION", true);
    }

    public boolean getShowHiddenFiles() {
        return mManager.getBoolean("SHOW_HIDDEN_FILES", false);
    }

    public boolean getPinchZoom() {
        return mManager.getBoolean("PINCH_ZOOM", true);
    }

    public boolean getIndentLine() {
        return mManager.getBoolean("INDENT_LINE", true);
    }

    public boolean getInsertBracket() {
        return mManager.getBoolean("INSERT_BRACKET", true);
    }

    public boolean getExtendedKeyboard() {
        return mManager.getBoolean("USE_EXTENDED_KEYS", false);
    }
}
