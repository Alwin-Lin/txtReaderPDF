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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

class PageCacheManager {
    private static final String CACHE_FILE_NAME = "tempfile.html";
    private Context hostContext;

    public PageCacheManager(Context parent) {
        hostContext = parent;
    }

    public String getCachedPage(String fName, int pNo) {
        StringBuffer tmpSB = new StringBuffer();
        FileInputStream fis;
        try {
            fis = hostContext.openFileInput(CACHE_FILE_NAME);
            byte [] buf = new byte[2048];
            int count;
            while ((count = fis.read(buf)) != -1) {
                tmpSB.append(new String(buf,0, count));
            }
            fis.close();
        } catch (IOException exc) {
            Log.e("PageCacheManager", "Cache file Read IO error", exc);
            tmpSB = new StringBuffer("Cache file Read IO error");
        }

        return tmpSB.toString();
    }

    public void putPageCached(String fName, int pNo, String content) {
        try {
            FileOutputStream fos = hostContext.openFileOutput(CACHE_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException exc) {
            Log.e("PageCacheManager", "Cache file Write IO error", exc);
        }
    }
}
