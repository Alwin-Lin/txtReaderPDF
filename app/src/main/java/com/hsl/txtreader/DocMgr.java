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

import com.hsl.txtreader.pdf.DocFile;
import com.hsl.txtreader.pdf.DocOutline;

public class DocMgr {
    public static final int STATE_INIT = 0;
    public static final int STATE_FILE_OPEN = 2;

    private DocFile docFile;
    private State curState;

    public DocMgr() {
        curState = new InitState();
    }

    public void openDoc(String name) {
        curState.OpenDoc(name);
    }

    public int getPageNo() {
        return curState.getPageNo();
    }

    public StringBuffer getPageContent(int pNo) {
        return curState.getPageContent(pNo);
    }

    public DocOutline getOutline() {
        return curState.getOutline();
    }

    public int getNumPages() {
        return curState.getNumPages();
    }

    public void setState(State newState) {
        curState = newState;
    }

    private class State {
        public void OpenDoc(String name) {}
        public StringBuffer getPageContent(int pNo) {
            return null;
        }
        public DocOutline getOutline() {
            return null;
        }
        public int getNumPages() {
            return 0;
        }
        public int getPageNo() {
            return 0;
        }
    }

    private class InitState extends State {
        public void OpenDoc(String name) {
            docFile = new DocFile(name);
            if (docFile.getErrorCode() == DocFile.ERR_OK) {
                docFile.getOutline();
                setState(new FileOpenedState());
            }
        }
    }

    private class FileOpenedState extends State {
        public void OpenDoc(String name) {
            if (!docFile.getFileName().equals(name)) {
                setState(new InitState());
                curState.OpenDoc(name);
            }
        }

        public StringBuffer getPageContent(int pNo) {
            return docFile.getPageContent(pNo);
        }

        public DocOutline getOutline() {
            return docFile.getOutline();
        }

        public int getNumPages() {
            return docFile.getNumPages();
        }

        public int getPageNo() {
            return docFile.getPageNo();
        }
    }
}
