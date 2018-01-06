/*
 * Copyright (C) 2009 Hsing-Sheng Lin
 * 
 * This file is part of txtReader.PDF
 * 
 * txtReader.PDF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * txtReader.PDF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with txtReader.PDF. If not, see <http://www.gnu.org/licenses/>.
 */

package com.hsl.txtreader;

import java.io.File;
import java.io.FileFilter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


public class FilePicker extends Picker {
    public static final String KEY_PATH = "Path";
    public static final String KEY_FILE_NAME = "FileName";

    private String curPath;
    private Bitmap folderIcon, fileIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        folderIcon = BitmapFactory.decodeResource(getResources(), R.drawable.folder);
        fileIcon = BitmapFactory.decodeResource(getResources(), R.drawable.file);

        setTitle(R.string.file_picker);
        setHeadIcon(R.drawable.back);

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_PATH)) {
            curPath = intent.getStringExtra(KEY_PATH);
        } else {
            curPath = new String("/");
        }

        exploreCurPath();
    }

    private void exploreCurPath() {
        setHeadText(curPath);
        buildFileList(curPath);

        setListAdapter(new listAdapter(this, mItems, mIcons));
    }

    public void buildFileList(String path) {
        File curDir = new File(path);

        if (!curDir.exists()) {
            curPath = new String("/");
            setHeadText(curPath);
            curDir = new File(curPath);
        }

        mItems.clear();
        mIcons.clear();

        File [] files = curDir.listFiles(new FileFilter() {
            public boolean accept(File aFile) {
                return (!aFile.isHidden() && (aFile.isDirectory() || aFile.getName().endsWith(".pdf")));
            }
        });

        if (files!= null){
            for (File curFile : files) {
                mItems.add(curFile.getName());
                if (curFile.isDirectory()) {
                    mIcons.add(folderIcon);
                } else {
                    mIcons.add(fileIcon);
                }
            }
        } else {
            setTitle("No Files Here!");
        }
    }

    @Override
    public void onClick(View v) {
        File curDir = new File(curPath);
        String parentPath = curDir.getParent();
        if (parentPath!=null) {
            curPath = parentPath;
            if (!curPath.endsWith("/")) {
                curPath += "/";
            }
            exploreCurPath();
        } else {
            Toast.makeText(this, getString(R.string.reach_root), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String seleFileStr = mItems.get(position);
        File seleFile = new File(curPath+seleFileStr);

        if (seleFile.isFile()) {
            Intent intent = new Intent();
            intent.putExtra(KEY_PATH, curPath);
            intent.putExtra(KEY_FILE_NAME, curPath +  seleFileStr);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            curPath += seleFileStr+"/";
            exploreCurPath();
        }
    }
}