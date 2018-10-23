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
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.manager;

import android.os.Environment;
import android.util.Log;

import com.KillerBLS.modpeide.App;
import com.KillerBLS.modpeide.adapter.model.FileModel;
import com.KillerBLS.modpeide.fragment.FragmentDocument;
import com.KillerBLS.modpeide.manager.database.Document;
import com.KillerBLS.modpeide.utils.commons.EditorController;
import com.KillerBLS.modpeide.utils.text.LinesCollection;
import com.KillerBLS.modpeide.utils.text.UndoStack;
import com.KillerBLS.modpeide.utils.text.language.LanguageProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileManager {

    private static final String TAG = FileManager.class.getSimpleName();

    // region LOAD

    /**
     * Загрузка текста из внутреннего хранилища.
     * @param controller - контроллер редактора.
     * @param filePath - путь к файлу.
     */
    public void loadFromStorage(EditorController controller, String filePath) {
        final File file = new File(filePath);
        if(file.canRead()) {
            LinesCollection lines = new LinesCollection();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                int line = 0;
                int currentLineStart = 0;
                while(true) {
                    String text = bufferedReader.readLine();
                    if(text == null) {
                        break;
                    }
                    lines.add(line, currentLineStart);
                    stringBuilder.append(text);
                    stringBuilder.append('\n');
                    currentLineStart += text.length() + 1;
                    line++;
                }
                if(lines.getLineCount() == 0) {
                    lines.add(0, 0);
                }
                if(stringBuilder.length() >= 1) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                bufferedReader.close();
                controller.setUndoStack(new UndoStack());
                controller.setRedoStack(new UndoStack());
                controller.setLanguage(LanguageProvider.getLanguage(file));
                controller.setText(stringBuilder.toString(), FragmentDocument.FLAG_SET_TEXT_DONT_SHIFT_LINES);
                controller.setLinesCollection(lines);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * Проверка, можем ли загрузить файл из кеша.
     * @param uuid - UUID кешированного документа.
     * @return - вернет true, если кешированный документ существует.
     */
    public boolean canLoadFromCache(String uuid) {
        return openCache(uuid + ".cache").exists();
    }

    /**
     * Загрузка документа из кеша.
     * @param controller - контроллер редактора.
     * @param uuid - название файла, из которого читается кеш.
     */
    public void loadFromCache(EditorController controller, String uuid) {
        final File file = openCache(uuid + ".cache");
        if(file.canRead()) {
            LinesCollection lines = new LinesCollection();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                int line = 0;
                int currentLineStart = 0;
                while(true) {
                    String text = bufferedReader.readLine();
                    if(text == null) {
                        break;
                    }
                    lines.add(line, currentLineStart);
                    stringBuilder.append(text);
                    stringBuilder.append('\n');
                    currentLineStart += text.length() + 1;
                    line++;
                }
                if(lines.getLineCount() == 0) {
                    lines.add(0, 0);
                }
                /*if(stringBuilder.length() >= 1) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }*/
                bufferedReader.close();
                controller.setUndoStack(restoreUndoStack(uuid));
                controller.setRedoStack(restoreRedoStack(uuid));
                //controller.setLanguage(LanguageProvider.getLanguage(file));
                controller.setLinesCollection(lines);
                controller.setText(stringBuilder.toString(), FragmentDocument.FLAG_SET_TEXT_DEFAULT);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    // endregion LOAD

    // region SAVE

    /**
     * Сохранение документа во внутреннее хранилище.
     * @param controller - контроллер, через который получаем необходимые данные.
     * @param path - путь, по которому будет сохранен файл.
     */
    public void saveToStorage(EditorController controller, String path) {
        try {
            String text = controller.getText();
            OutputStreamWriter textOutputStreamWriter =
                    new OutputStreamWriter(new FileOutputStream(new File(path)));
            textOutputStreamWriter.write(text);
            textOutputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Error when saving the document to " + path + ":\n" + e.getMessage());
        }
    }

    /**
     * Сохранение документа в кеш.
     * @param controller - контроллер, через который получаем необходимые данные.
     * @param uuid - UUID документа, который сохраняем.
     */
    public void saveToCache(EditorController controller, String uuid) {
        createCacheFilesIfNecessary(uuid);
        try {
            String textCache = controller.getText();
            OutputStreamWriter textOutputStreamWriter =
                    new OutputStreamWriter(new FileOutputStream(openCache(uuid + ".cache")));
            textOutputStreamWriter.write(textCache);
            textOutputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Error when caching the text of the document with UUID " + uuid + ":\n" + e.getMessage());
        }
        try {
            String undoCache = encodeUndoStack(controller.getUndoStack());
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(new FileOutputStream(openCache(uuid + "-undo.cache")));
            outputStreamWriter.write(undoCache);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Error when caching the undo stack of the document with UUID " + uuid + ":\n" + e.getMessage());
        }
        try {
            String redoCache = encodeUndoStack(controller.getRedoStack());
            OutputStreamWriter redoOutputStreamWriter =
                    new OutputStreamWriter(new FileOutputStream(openCache(uuid + "-redo.cache")));
            redoOutputStreamWriter.write(redoCache);
            redoOutputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Error when caching the redo stack of the document with UUID " + uuid + ":\n" + e.getMessage());
        }
    }

    /**
     * Создание файлов кеша, если еще не созданы.
     * @param uuid - UUID документа, который будет кешироваться.
     */
    private void createCacheFilesIfNecessary(String uuid) {
        File textCacheFile = openCache(uuid + ".cache");
        File undoCacheFile = openCache(uuid + "-undo.cache");
        File redoCacheFile = openCache(uuid + "-redo.cache");
        if (!textCacheFile.exists()) {
            try {
                textCacheFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Failed to create text cache file for document with UUID " + uuid + ":\n" + e.getMessage());
            }
        }
        if (!undoCacheFile.exists()) {
            try {
                undoCacheFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Failed to create undo cache file for document with UUID " + uuid + ":\n" + e.getMessage());
            }
        }
        if (!redoCacheFile.exists()) {
            try {
                redoCacheFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Failed to create redo cache file for document with UUID " + uuid + ":\n" + e.getMessage());
            }
        }
    }

    // endregion SAVE

    // region STACKS

    /**
     * Кодировка стэка с историей undo/redo операций.
     * @param stack - стэк для кодировки.
     * @return - возвращает закодированную строку.
     */
    private String encodeUndoStack(UndoStack stack) {
        StringBuilder builder = new StringBuilder();
        String delimiter = "\u0005";
        for (int i = stack.count() - 1; i >= 0; i--) {
            UndoStack.TextChange change = stack.getItemAt(i);
            builder.append(change.oldText);
            builder.append(delimiter);
            builder.append(change.newText);
            builder.append(delimiter);
            builder.append(change.start);
            builder.append(delimiter);
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     * Проверка undo-стэка.
     * @param uuid - UUID читаемого документа (стэка).
     * @return - возвращает полученный {@link UndoStack}.
     */
    private UndoStack restoreUndoStack(String uuid) {
        File file = openCache(uuid + "-undo.cache");
        if (file.exists()) {
            return readUndoStackCache(file);
        } else {
            Log.e(TAG, "Undo cache file does not exist");
        }
        return new UndoStack();
    }

    /**
     * Проверка redo-стэка.
     * @param uuid - UUID читаемого документа (стэка).
     * @return - возвращает полученный {@link UndoStack}.
     */
    private UndoStack restoreRedoStack(String uuid) {
        File file = openCache(uuid + "-undo.cache");
        if (file.exists()) {
            return readUndoStackCache(file);
        } else {
            Log.e(TAG, "Redo cache file does not exist");
        }
        return new UndoStack();
    }

    /**
     * Чтение undo/redo стэка из кеша.
     * @param file - файл для чтения.
     * @return - возвращает {@link UndoStack}.
     */
    private UndoStack readUndoStackCache(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String read = bufferedReader.readLine();
                if (read == null) {
                    break;
                }
                stringBuilder.append(read);
                stringBuilder.append('\n');
            }
            String contents = stringBuilder.toString();
            bufferedReader.close();
            inputStream.close();
            return decodeUndoStack(contents);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return new UndoStack();
        }
    }

    /**
     * Декодирование undo/redo стэка из кеша.
     * @param raw - текст для декодирования.
     * @return - возвращает декодированный {@link UndoStack}.
     */
    private UndoStack decodeUndoStack(String raw) {
        UndoStack result = new UndoStack();
        if (!(raw == null || raw.length() == 0)) {
            String[] items = raw.split("\u0005");
            if (items[items.length - 1].endsWith("\n")) {
                String item = items[items.length - 1];
                items[items.length - 1] = item.substring(0, item.length() - 1);
            }
            for (int i = items.length - 3; i >= 0; i -= 3) {
                UndoStack.TextChange change = new UndoStack.TextChange();
                change.oldText = items[i];
                change.newText = items[i + 1];
                change.start = Integer.parseInt(items[i + 2]);
                result.push(change);
            }
        }
        return result;
    }

    // endregion STACKS

    // region COMMONS

    /**
     * Удаление файла/папки со всеми вложенными файлами/папками.
     * @param path - файл/папка которую хотим удалить.
     */
    public static boolean deleteRecursive(final File path) {
        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                if (!deleteRecursive(file)) {
                    return false;
                }
            }
        }
        return path.delete();
    }

    /**
     * Конвертация класса {@link FileModel} в класс {@link Document}
     * @param fileModel - модель файла которую хотим перенести в Document.
     * @return - возвращает Document, полученный из чистого FileModel.
     */
    public static Document convert(final FileModel fileModel) {
        Document document = new Document();
        document.setName(fileModel.getName());
        document.setPath(fileModel.getPath());
        document.setLanguage("null");
        document.setScrollX(0);
        document.setScrollY(0);
        document.setSelectionStart(0);
        document.setSelectionEnd(0);
        return document;
    }

    /**
     * Получение папки с кешом.
     */
    public static File getCachedFilesDir() {
        final File file = new File(
                Environment.getExternalStorageDirectory().toString() + "/Android/data/" + App.PACKAGE_NAME);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * Удаление документа из кеша.
     * @param uuid - UUID удаляемого документа.
     */
    public static void clearCache(String uuid) {
        File textCacheFile = openCache(uuid + ".cache");
        File undoCacheFile = openCache(uuid + "-undo.cache");
        File redoCacheFile = openCache(uuid + "-redo.cache");
        if (textCacheFile.exists()) {
            textCacheFile.delete();
        }
        if (undoCacheFile.exists()) {
            undoCacheFile.delete();
        }
        if (redoCacheFile.exists()) {
            redoCacheFile.delete();
        }
    }

    /**
     * Получение файла из кеша с указаным UUID.
     * @param name - имя документа + расширение (.cache, -undo.cache, -redo.cache)
     * @return - файл с указанным расширением.
     */
    private static File openCache(String name) {
        return new File(getCachedFilesDir(), name);
    }

    // endregion COMMONS
}
