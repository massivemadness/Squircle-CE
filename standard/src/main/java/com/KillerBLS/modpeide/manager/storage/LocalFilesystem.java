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

package com.KillerBLS.modpeide.manager.storage;

import android.os.Environment;

import com.KillerBLS.modpeide.adapter.model.FileModel;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class LocalFilesystem extends Filesystem {

    @Override
    public FileModel getDefaultLocation() {
        String filePath = Environment.getExternalStorageDirectory().toString();
        if(!new File(filePath).canWrite()) {
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        }
        if(!new File(filePath).canWrite()) {
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        }
        if(!new File(filePath).canWrite()) {
            filePath = "/storage/emulated/0";
        }
        return new FileModel(new File(filePath));
    }

    @Override
    public FileModel getParentFolder(FileModel fileModel) {
        File parentFile = new File(fileModel.getPath()).getParentFile();
        return new FileModel(parentFile);
    }

    @Override
    public List<FileModel> makeList(FileModel fileModel,
                                    Comparator<? super FileModel> comparator, boolean showHidden) {
        LinkedList<FileModel> filesList = new LinkedList<>();
        LinkedList<FileModel> foldersList = new LinkedList<>();
        for(File file : new File(fileModel.getPath()).listFiles()) {
            if(file.isDirectory()) { //Folder
                if(file.isHidden()) {
                    if(showHidden) {
                        foldersList.add(new FileModel(file));
                    }
                } else {
                    foldersList.add(new FileModel(file));
                }
            } else { //File
                if(file.isHidden()) {
                    if(showHidden) {
                        filesList.add(new FileModel(file));
                    }
                } else {
                    filesList.add(new FileModel(file));
                }
            }
        }
        Collections.sort(filesList, comparator); //Sort files
        Collections.sort(foldersList, comparator); //Sort folders

        if(!fileModel.equals(getDefaultLocation())) { //Up button
            FileModel up = getParentFolder(fileModel);
            up.setIsUp(true);
            foldersList.addFirst(up);
        }
        foldersList.addAll(filesList);
        return foldersList;
    }
}
