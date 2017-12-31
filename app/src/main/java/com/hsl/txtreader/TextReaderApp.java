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

import java.util.ArrayList;

import android.app.Application;

public class TextReaderApp extends Application {
    public static final int SHARE_OBJ_OUTLINE=0;
    private ArrayList<Object> mObjects;

    public void onCreate() {
        mObjects = new ArrayList<Object>();
    }

    public void putObject(int idx, Object obj) {
        mObjects.add(idx, obj);
    }

    public Object getObject(int idx) {
        return mObjects.get(idx);
    }

    public void onTerminate() {
    }
}
