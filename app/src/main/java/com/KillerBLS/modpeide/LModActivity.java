/*
 * Copyright (C) 2017 Light Team Software
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

package com.KillerBLS.modpeide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;

import com.KillerBLS.modpeide.activity.LModDisplayActivity;
import com.KillerBLS.modpeide.activity.LModPreferencesActivity;
import com.KillerBLS.modpeide.task.FileOpenTask;
import com.KillerBLS.modpeide.task.FileSaveTask;
import com.KillerBLS.modpeide.util.LModLogUtils;
import com.KillerBLS.modpeide.manager.LModPreferenceManager;
import com.KillerBLS.modpeide.syntax.SyntaxPatterns;
import com.KillerBLS.modpeide.syntax.SyntaxType;
import com.KillerBLS.modpeide.manager.TypefaceManager;
import com.KillerBLS.modpeide.view.SyntaxEditor;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kobakei.ratethisapp.RateThisApp;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

import it.gmariotti.changelibs.library.view.ChangeLogRecyclerView;

public class LModActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static String FILE_PATH = null;

    private LModPreferenceManager preferenceManager;

    private long back_pressed;
    private Locale locale;
    private String SEARCH_TEXT;
    private boolean IGNORE_CASE;

    @SuppressLint("StaticFieldLeak")
    private static SyntaxEditor editor;

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new LModPreferenceManager(this);

        //region LANGUAGE

        locale = new Locale(preferenceManager.getLanguageParameter());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        //noinspection deprecation
        config.locale = locale;
        //noinspection deprecation
        getBaseContext().getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());

        //endregion LANGUAGE

        setContentView(R.layout.activity_main);
        updateFABVisibility(false);
        initFolder();
        initToolbar();
        initDrawer();
        initEditor();
        initRateSystem();
    }

    public void initRateSystem() {
        // Custom condition: 2 days and 5 launches
        RateThisApp.Config configr = new RateThisApp.Config(2, 5);
        RateThisApp.init(configr);
        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void initFolder() {
        //Create a "ModPE" directory
        File sdPath = Environment.getExternalStorageDirectory();
        String DIR_SD = "ModPE";
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        if(!sdPath.exists()) {
            sdPath.mkdirs();
        }
    }

    public void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void initDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = findViewById(R.id.navigation_view);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open,
                R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                try {
                    closeKeyBoard();
                } catch (NullPointerException e) {
                    showException(e);
                }
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        //noinspection deprecation
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.drawer_background));
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        switch(permsRequestCode){
            case 200:
                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(writeAccepted) {
                    new FileSaveTask(editor.getString(),
                            LModActivity.this).execute();
                } else {
                    FILE_PATH = null;
                    Snackbar.make(editor,
                            R.string.snackbar_access_denied, Snackbar.LENGTH_LONG).show();
                }
                break;
            case 101:
                boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(readAccepted) {
                    new MaterialFilePicker()
                            .withActivity(this)
                            .withRequestCode(1)
                            .withFilter(Pattern.compile(".*\\.js$"))
                            .withFilterDirectories(false)
                            .withHiddenFiles(true)
                            .start();
                } else {
                    Snackbar.make(editor,
                            R.string.snackbar_access_denied, Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void saveScript() {
        if(FILE_PATH == null) {
            new MaterialDialog.Builder(this)
                    .title(R.string.dialog_save)
                    .customView(R.layout.dialog_save, true)
                    .positiveText(R.string.dialog_save)
                    .negativeText(R.string.cancel)
                    .autoDismiss(false)
                    .cancelable(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            View view = dialog.getCustomView();
                            assert view != null;
                            EditText name = view.findViewById(android.R.id.text1);
                            EditText path = view.findViewById(android.R.id.text2);
                            String valid_path = path.getText().toString();
                            String valid_name = name.getText().toString();
                            File directory = new File(valid_path);
                            if (TextUtils.isEmpty(valid_name)){ //Name is empty?
                                name.setError(getResources().getString(R.string.dialog_error_name));
                            } else if(TextUtils.isEmpty(valid_path)) { //Path is empty?
                                path.setError(getResources().getString(R.string.dialog_error_path));
                            } else if(!directory.isDirectory()) { //Path not a directory?
                                path.setError(
                                        getResources().getString(R.string.dialog_error_valid_path));
                            } else if(!TextUtils.isEmpty(valid_name) &&
                                    !TextUtils.isEmpty(valid_path) &&
                                    directory.isDirectory()) { //all over
                                if (Build.VERSION.SDK_INT >= 23) { //Android 6 "EACCES" Bug
                                    FILE_PATH = valid_path + valid_name;
                                    LModLogUtils.d(FILE_PATH);
                                    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
                                    requestPermissions(perms, 200); //200 - My own "Write" Code
                                } else {
                                    FILE_PATH = valid_path + valid_name;
                                    LModLogUtils.d(FILE_PATH);
                                    new FileSaveTask(editor.getString(),
                                            LModActivity.this).execute();
                                }
                                dialog.dismiss();
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            new FileSaveTask(editor.getString(), LModActivity.this).execute();
        }
    }

    public void openScript() {
        if (Build.VERSION.SDK_INT >= 23) { //Android 6
            String[] perms = {"android.permission.READ_EXTERNAL_STORAGE"};
            requestPermissions(perms, 101); //101 - My own "Read" Code
        } else {
            new MaterialFilePicker()
                    .withActivity(this)
                    .withRequestCode(1)
                    .withFilter(Pattern.compile(".*\\.js$"))
                    .withFilterDirectories(false)
                    .withHiddenFiles(true)
                    .start();
        }
    }

    public void newScript() {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_file_changes)
                .content(R.string.dialog_file_changes_desc)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        editor.setCurrentText(null);
                        FILE_PATH = null;
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            new FileOpenTask(new File(filePath), LModActivity.this, editor.getEditor()).execute();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onResume() { //Settings
        super.onResume();

        //Fullscreen
        if(preferenceManager.getFullScreenParameter()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        //Confirm Exit in onBackPressed() method

        //Language
        locale = new Locale(preferenceManager.getLanguageParameter());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        //noinspection deprecation
        config.locale = locale;
        //noinspection deprecation
        getBaseContext().getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());
        preferenceManager.updateLanguageParameter();

        //Show Line Numbers
        if(preferenceManager.getLineNumbersParameter()) {
            editor.setLineNumbersEnabled(true);
        } else {
            editor.setLineNumbersEnabled(false);
        }

        //Highlight Current Line
        if(preferenceManager.getHighlightCurrentLineParameter()) {
            editor.setHighlightCurrentLine(true);
        } else {
            editor.setHighlightCurrentLine(false);
        }

        //Typeface
        if(preferenceManager.getFontFaceParameter().contains("monospace")) {
            editor.setCurrentTypeface(new TypefaceManager(this).getMonospace());
        } else if(preferenceManager.getFontFaceParameter().contains("source_code_pro")){
            editor.setCurrentTypeface(new TypefaceManager(this).getSourceCodePro());
        } else {
            editor.setCurrentTypeface(new TypefaceManager(this).getDefault());
        }

        //Fixed Text Size
        try {
            if(preferenceManager.getFixedTextSizeParameter() <= 10) { //less than 10?
                preferenceManager.updateFixedTextSizeParameter(10);
                editor.setFixedTextSize(preferenceManager.getFixedTextSizeParameter());
            } else if(preferenceManager.getFixedTextSizeParameter() >= 20) { //more than 20?
                preferenceManager.updateFixedTextSizeParameter(20);
                editor.setFixedTextSize(preferenceManager.getFixedTextSizeParameter());
            } else { //normal
                editor.setFixedTextSize(preferenceManager.getFixedTextSizeParameter());
            }
        } catch (NumberFormatException e) { //not a float
            preferenceManager.updateFixedTextSizeParameter(14);
            editor.setFixedTextSize(preferenceManager.getFixedTextSizeParameter());
            LModLogUtils.e(e.toString());
        }

        //Read Only
        if(preferenceManager.getReadOnlyParameter()) {
            editor.setReadOnly(true);
        } else {
            editor.setReadOnly(false);
        }

        //Syntax Highlighting
        if(preferenceManager.getSyntaxHighlightParameter()) {
            editor.setSyntaxHighlightEnabled(true);
        } else {
            editor.setSyntaxHighlightEnabled(false);
        }

        //Brackets Matching
        if(preferenceManager.getBracketMatchingParameter()) {
            editor.setBracketMatchingEnabled(true);
        } else {
            editor.setBracketMatchingEnabled(false);
        }

        //Brackets Auto-Closing
        if(preferenceManager.getBracketsAutoCloseParameter()) {
            editor.setBracketsAutoClosing(true);
        } else {
            editor.setBracketsAutoClosing(false);
        }

        //Pinch Zoom
        if(preferenceManager.getPinchZoomParameter()) {
            editor.setPinchZoomEnabled(true);
        } else {
            editor.setPinchZoomEnabled(false);
        }

        //Highlights
        if(preferenceManager.getSymbolsHighlight()) {
            editor.highlightSymbols(true);
        } else {
            editor.highlightSymbols(false);
        }

        //Autocompletion
        if(preferenceManager.getAutoCompleteParameter()) {
            editor.setAutoCompleteEnabled(true);
        } else {
            editor.setAutoCompleteEnabled(false);
        }

        //Auto Indentation
        if(preferenceManager.getAutoIndentParameter()) {
            editor.setAutoIndentationEnabled(true);
        } else {
            editor.setAutoIndentationEnabled(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        locale = new Locale(preferenceManager.getLanguageParameter());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        //noinspection deprecation
        config.locale = locale;
        //noinspection deprecation
        getBaseContext().getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());
        preferenceManager.updateLanguageParameter();
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("ConstantConditions")
    private void closeKeyBoard() throws NullPointerException {
        InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder windowToken = getCurrentFocus().getWindowToken();
        int hideType = InputMethodManager.HIDE_NOT_ALWAYS;
        inputManager.hideSoftInputFromWindow(windowToken, hideType);
    }

    public void initEditor() {
        editor = findViewById(R.id.editor);
        editor.setSyntaxUpdateDelay(350); //syntax highlighting delay
        editor.setMaxHistorySize(100);

        editor.highlightKeywords(true);
        editor.highlightComments(true);
        editor.highlightKeywords2(true);
        editor.highlightNumbers(true);
        editor.highlightStrings(true);
        editor.highlightClasses(false);

        //Setting Up
        editor.setCodeColor(
                ContextCompat.getColor(this, R.color.ide_textcolor)); //text color
        editor.setBackgroundColor(
                ContextCompat.getColor(this, R.color.ide_background)); //background color
        editor.setCurrentLineHighlightColor(
                ContextCompat.getColor(this, R.color.ide_currentline)); //current line color
        editor.setSelectionHighlightColor(
                ContextCompat.getColor(this, R.color.ide_selection_color)); //selection color (long tap)
        editor.setLineNumbersColor(
                ContextCompat.getColor(this, R.color.ide_numbers_color)); //line numbers color
        editor.setCursorColor(editor.getEditor(),
                ContextCompat.getColor(this, R.color.ide_cursor_color)); //cursor color
        editor.setMatchedBracketsColor(
                ContextCompat.getColor(this, R.color.ide_brackets_color)); //brackets color

        //Setting Up Patterns and Colors
        editor.addSyntaxPattern(SyntaxPatterns.KEYWORDS, SyntaxType.KEYWORDS,
                ContextCompat.getColor(this, R.color.syntax_keyword)); //addSyntaxPattern(Pattern, Type, Color);
        editor.addSyntaxPattern(SyntaxPatterns.KEYWORDS2, SyntaxType.KEYWORDS_2,
                ContextCompat.getColor(this, R.color.syntax_keyword2));
        editor.addSyntaxPattern(SyntaxPatterns.COMMENTS, SyntaxType.COMMENTS,
                ContextCompat.getColor(this, R.color.syntax_comment));
        editor.addSyntaxPattern(SyntaxPatterns.STRINGS, SyntaxType.STRINGS,
                ContextCompat.getColor(this, R.color.syntax_string));
        editor.addSyntaxPattern(SyntaxPatterns.SYMBOLS, SyntaxType.SYMBOLS,
                ContextCompat.getColor(this, R.color.syntax_symbols));
        editor.addSyntaxPattern(SyntaxPatterns.NUMBERS, SyntaxType.NUMBERS,
                ContextCompat.getColor(this, R.color.syntax_number));
        editor.addSyntaxPattern(SyntaxPatterns.CLASSES, SyntaxType.CLASSES,
                ContextCompat.getColor(this, R.color.syntax_classes));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.rate:
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.rate_url))));
                break;
            case R.id.buy_premium:
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.premium_url))));
                break;
            case R.id.check_errors:
                checkErrors();
                break;
            case R.id.save:
                saveScript();
                break;
            case R.id.open:
                openScript();
                break;
            case R.id.newscript:
                newScript();
                break;
            case R.id.lists:
                new MaterialDialog.Builder(LModActivity.this)
                        .title(R.string.array_lists)
                        .items(R.array.lists)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch(which) {
                                    case 0:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_hooks)
                                                .items(R.array.all_hooks)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addHookToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 1:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_functions)
                                                .items(R.array.all_methods)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 2:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_chatcolors)
                                                .items(R.array.all_colors)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 3:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_itemcategories)
                                                .items(R.array.all_categories)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 4:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_particletypes)
                                                .items(R.array.all_particles)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 5:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_enttypes)
                                                .items(R.array.all_enttypes)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 6:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_entrendertypes)
                                                .items(R.array.all_entrendertypes)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 7:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_armortypes)
                                                .items(R.array.all_armortypes)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 8:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_mobeffects)
                                                .items(R.array.all_mobeffects)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 9:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_dimensions)
                                                .items(R.array.all_dimensions)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 10:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_blockfaces)
                                                .items(R.array.all_blockfaces)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 11:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_animations)
                                                .items(R.array.all_animations)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 12:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_enchants)
                                                .items(R.array.all_enchantments)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 13:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_enchanttypes)
                                                .items(R.array.all_enchanttypes)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 14:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_blockrenderlayers)
                                                .items(R.array.all_blockrenderlayers)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                    case 15:
                                        new MaterialDialog.Builder(LModActivity.this)
                                                .title(R.string.array_sounds)
                                                .items(R.array.all_sounds)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        addFuncToLModEditor(text);
                                                    }
                                                })
                                                .positiveText(R.string.close)
                                                .show();
                                        break;
                                }
                            }
                        })
                        .show();
                break;
            case R.id.goTo:
                new MaterialDialog.Builder(this)
                        .title(R.string.dialog_goto_title)
                        .customView(R.layout.dialog_goto, true)
                        .positiveText(R.string.apply)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {
                                View view = dialog.getCustomView();
                                assert view != null;
                                EditText line = view.findViewById(android.R.id.text1);
                                final String lineText = line.getText().toString();
                                if(lineText.isEmpty()) {
                                    Snackbar.make(editor,
                                            getResources().getString(R.string.dialog_goto_enter),
                                            Snackbar.LENGTH_LONG).show();
                                } else {
                                    dialog.getWindow().setSoftInputMode(
                                            WindowManager.LayoutParams.
                                                    SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            editor.goToLine(Integer.parseInt(lineText));
                                        }
                                    }, 300); // delay
                                }
                            }
                        }).show();
                break;
            case R.id.find:
                new MaterialDialog.Builder(this)
                        .title(R.string.dialog_find_title)
                        .customView(R.layout.dialog_find, true)
                        .positiveText(R.string.apply)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {
                                View view = dialog.getCustomView();
                                assert view != null;
                                EditText textField = view.findViewById(R.id.text_to_find);
                                CheckBox match_case = view.findViewById(R.id.match_case_check);
                                IGNORE_CASE = !match_case.isChecked();
                                SEARCH_TEXT = textField.getText().toString();
                                updateFABVisibility(true);
                            }
                        })
                        .show();
                break;
            case R.id.select_all:
                editor.selectAll();
                break;
            case R.id.select_line:
                editor.selectLine();
                break;
            case R.id.delete_line:
                editor.deleteLine();
                break;
            case R.id.toBegin:
                editor.toBegin();
                break;
            case R.id.toEnd:
                editor.toEnd();
                break;
            case R.id.replace_all:
                new MaterialDialog.Builder(this)
                        .title(R.string.dialog_replace_all_title)
                        .customView(R.layout.dialog_replace_all, true)
                        .positiveText(R.string.apply)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {
                                View view = dialog.getCustomView();
                                assert view != null;
                                EditText text = view.findViewById(android.R.id.text1);
                                EditText textWith = view.findViewById(android.R.id.text2);
                                editor.replaceAll(text.getText().toString(),
                                        textWith.getText().toString());

                            }
                        }).show();
                break;
            case R.id.support:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "lightteamsoftware@gmail.com", null));
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ModPE IDE");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Your problem/suggestion:\n\n");
                startActivity(Intent.createChooser(emailIntent, null));
                break;
            case R.id.im_undo:
                editor.undo();
                break;
            case R.id.im_redo:
                editor.redo();
                break;
            case R.id.cut:
                editor.cut();
                break;
            case R.id.copy:
                editor.copy();
                break;
            case R.id.paste:
                editor.paste();
                break;
            case R.id.shortcuts:
                new MaterialDialog.Builder(LModActivity.this)
                        .title(R.string.menu_shortcuts)
                        .items(R.array.shortcuts_overview)
                        .positiveText(R.string.close)
                        .show();
                break;
			case R.id.open_source:
                Intent toSource = new Intent(Intent.ACTION_VIEW);
                toSource.setData(Uri.parse("https://github.com/Light-Team/ModPE-IDE-Source"));
                startActivity(toSource);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addHookToLModEditor(CharSequence text) {
        int start = editor.getEditor().getSelectionStart();
        String str = "function " + text + " {\n\n}";
        editor.getEditor().getText().insert(start, str);
    }

    public void addFuncToLModEditor(CharSequence text) {
        int start = editor.getEditor().getSelectionStart();
        String str = text + "";
        editor.getEditor().getText().insert(start, str);
    }

    public void showLightTeamDialog(View view) {
        mDrawerLayout.closeDrawers();
        new MaterialDialog.Builder(this)
                .customView(R.layout.light_team_dialog, false)
                .positiveText(R.string.close)
                .show();
    }

    public static void showException(Exception e) {
        String ex = e.toString();
        LModLogUtils.e(ex);
        Snackbar.make(editor, "Error: " + ex, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(preferenceManager.getExitConfirmParameter()) {
                new MaterialDialog.Builder(this)
                        .title(R.string.exit)
                        .content(R.string.dialog_exit)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {
                                finish(); //Exit
                            }
                        }).show();
            } else {
                if (back_pressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                } else {
                    Snackbar.make(editor, R.string.snackbar_one_more, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.close, null).show();
                    back_pressed = System.currentTimeMillis();
                }
            }
        }
    }

    public void updateFABVisibility(boolean visible) {
        FloatingActionButton previous = findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.findPreviousText(SEARCH_TEXT, IGNORE_CASE);
            }
        });
        FloatingActionButton next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.findText(SEARCH_TEXT, IGNORE_CASE);
            }
        });
        FloatingActionButton close_find = findViewById(R.id.close_find);
        close_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFABVisibility(false);
            }
        });
        if(visible) {
            previous.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            close_find.setVisibility(View.VISIBLE);
        } else {
            previous.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            close_find.setVisibility(View.INVISIBLE);
        }
    }

    public void checkErrors() {
        new MaterialDialog.Builder(LModActivity.this)
                .title(R.string.dialog_premium_version)
                .content(R.string.dialog_only_in_premium)
                .positiveText(R.string.buy_premium)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(getResources().getString(R.string.premium_url))));
                    }
                })
                .show();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.drawer_textures:
                MaterialDialog md = new MaterialDialog.Builder(LModActivity.this)
                        .title(R.string.drawer_textures)
                        .customView(R.layout.textures, false)
                        .positiveText(R.string.close)
                        .show();
                View view = md.getCustomView();
                assert view != null;
                WebView web_textures = view.findViewById(R.id.web_textures);
                web_textures.setWebViewClient(new WebViewClient() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return false;
                    }
                });
                web_textures.getSettings().setJavaScriptEnabled(true);
                web_textures.getSettings().setBuiltInZoomControls(false);
                web_textures.loadUrl(getResources().getString(R.string.textures_url));
                break;
            case R.id.drawer_ui:
                Intent toUI = new Intent(LModActivity.this, LModDisplayActivity.class);
                startActivity(toUI);
                break;
            case R.id.drawer_changelog:
                LayoutInflater layoutInflater =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams")
                ChangeLogRecyclerView chgList =
                        (ChangeLogRecyclerView) layoutInflater.inflate(
                                R.layout.changelogrecycler, null);
                new MaterialDialog.Builder(LModActivity.this)
                        .title(R.string.dialog_changelog)
                        .customView(chgList, false)
                        .positiveText(R.string.close)
                        .show();

                break;
            case R.id.drawer_settings:
                Intent toPrefs = new Intent(LModActivity.this,
                        LModPreferencesActivity.class);
                startActivity(toPrefs);
                break;
        }
        mDrawerLayout.closeDrawers();
        return true;
    }
}