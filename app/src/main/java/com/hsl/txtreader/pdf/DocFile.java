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

package com.hsl.txtreader.pdf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import android.util.Log;

import com.hsl.txtreader.pdf.port.PDFPage;
import com.sun.pdfview.OutlineNode;
import com.sun.pdfview.PDFFile;

public class DocFile {
    public static final int ERR_OK = 0;
    public static final int ERR_FILE_OPEN = 1;
    public static final int ERR_OUTLINE = 2;
    public static final int ERR_PAGE = 3;

    private String fileName;
    private int pageNo;
    private int numPages;

    private PDFFile docFile;
    private PDFPage docPage;
    private DocOutline mOutline;

    private int error;

    private RandomAccessFile ranAccFile;

    public DocFile(String fName) {
        if (openFile(fName)) {
            error = ERR_OK;
        } else {
            error = ERR_FILE_OPEN;
        }
        docPage = null;
        mOutline = null;
    }

    private boolean openFile(String fName) {
        boolean ok = true;
        fileName = fName;

        try {
            ranAccFile = new RandomAccessFile(new File(fileName), "r");
            FileChannel channel = ranAccFile.getChannel();

            ByteBuffer bBuffer =
                channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

            docFile = new PDFFile(bBuffer);
            numPages = docFile.getNumPages();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(this.toString(), fileName);
            docFile = null;
            numPages = 0;
            ok = false;
            if (ranAccFile!=null) {
                try {
                    ranAccFile.close();
                } catch (IOException excption) {
                    Log.e(this.toString(),"File close error");
                }
            }
        }

        return ok;
    }

    protected void finalize() {
        if (ranAccFile != null) {
            try {
                ranAccFile.close();
            } catch (IOException ex) {
                Log.e(this.toString(),"File close error");
            }
        }
    }

    public StringBuffer getPageContent(int pNo) {
        int newPageNo = (pNo < 1) ? 1 : ((pNo > numPages) ? numPages : pNo);

        try {
            if (newPageNo != pageNo) {
                docPage = docFile.getPage(newPageNo, true);
            }

            pageNo = docPage.getPageNumber();
        } catch (Exception ex) {
            return new StringBuffer("File Reading Error.");
        }

        return docPage.getContent();
    }

    public DocOutline getOutline() {
        if (mOutline == null) {
            try {
                OutlineNode aNode = docFile.getOutline();
                if (aNode != null) {
                    mOutline = new DocOutline(aNode);
                }

            } catch (IOException ex) {
                mOutline = null;
            }
        }
        return mOutline;
    }

    public int getErrorCode() {
        return error;
    }

    public String getFileName() {
        return fileName;
    }

    public int getNumPages() {
        return numPages;
    }

    public int getPageNo() {
        return pageNo;
    }
}
