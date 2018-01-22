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

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.content.pm.PackageManager;



public class FilePicker extends Picker
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String KEY_PATH = "Path";
    public static final String KEY_FILE_NAME = "FileName";

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private static final String TAG = "FilePicker";
    private String curPath;
    private Bitmap folderIcon, fileIcon;
    public View mLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        folderIcon = BitmapFactory.decodeResource(getResources(), R.drawable.folder);
        fileIcon = BitmapFactory.decodeResource(getResources(), R.drawable.file);
        setContentView(R.layout.file_picker);
        LinearLayout mLayout = findViewById(R.id.picker);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.picker, null, false);
        mLayout.addView(contentView, 0);

        setTitle(R.string.file_picker);
        setHeadIcon(R.drawable.back);

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_PATH)) {
            curPath = intent.getStringExtra(KEY_PATH);
        } else {
            curPath = Environment.getExternalStorageDirectory().toString();
            Log.v(TAG, "curPath = "+curPath);
        }

        exploreCurPath();
    }
//    public int permissionCheck = ContextCompat.checkSelfPermission(FilePicker.this,
//            Manifest.permission.READ_EXTERNAL_STORAGE);

    private void exploreCurPath() {
        setContentView(R.layout.file_picker);
        LinearLayout mLayout = findViewById(R.id.picker);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.picker, null, false);
        mLayout.addView(contentView, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            Toast.makeText(this,
                    "Read external file permission is available. Exploring current path.",
                    Toast.LENGTH_SHORT).show();
            setHeadText(curPath);
            buildFileList(curPath);

            setListAdapter(new listAdapter(this, mItems, mIcons));
        } else {
            // Permission is missing and must be requested.
            requestReadExternalFilePermission();
        }
        // END_INCLUDE(exploreCurrentFilePath)
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
            curPath = Environment.getExternalStorageDirectory().toString();
            Log.v(TAG, "curPath = "+curPath);
            setTitle("No Files Here!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
           int[] grantResults) {
        setContentView(R.layout.file_picker);
        LinearLayout mLayout = findViewById(R.id.picker);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.picker, null, false);
        mLayout.addView(contentView, 0);

        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read external files permission was granted. Starting preview.",
                        Toast.LENGTH_SHORT)
                        .show();
                exploreCurPath();
            } else {
                Toast.makeText(this, "Read external files permission request was denied.",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    private void requestReadExternalFilePermission() {
        setContentView(R.layout.file_picker);
        LinearLayout mLayout = findViewById(R.id.picker);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.picker, null, false);
        mLayout.addView(contentView, 0);

        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "External file access is required to display the file preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(FilePicker.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            Toast.makeText(this,
                    "Permission is not available. Requesting read external file permission.",
                    Toast.LENGTH_SHORT).show();
             //Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
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
        File seleFile = new File(curPath + "/" + seleFileStr);

        if (seleFile.isFile()) {
            Intent intent = new Intent();
            intent.putExtra(KEY_PATH, curPath);
            intent.putExtra(KEY_FILE_NAME, curPath + "/" + seleFileStr);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            curPath += "/" + seleFileStr;
            exploreCurPath();
        }
    }
}