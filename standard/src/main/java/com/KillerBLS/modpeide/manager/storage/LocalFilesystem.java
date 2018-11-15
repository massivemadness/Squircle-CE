/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
