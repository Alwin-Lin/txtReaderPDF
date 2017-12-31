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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import com.hsl.txtreader.pdf.port.DefaultMutableTreeNode;
import com.sun.pdfview.OutlineNode;
import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.action.GoToAction;
import com.sun.pdfview.action.PDFAction;

public class DocOutline {
    private OutlineNode mOutline;
    private OutlineNode mOutlineCursor;
    private Stack<String> mBranchNameStack;

    public DocOutline(OutlineNode ol) {
        mOutline = ol;
        mOutlineCursor = mOutline;
        mBranchNameStack = new Stack<String>();
    }

    public void setRootName(String rootName) {
        if (mBranchNameStack.empty()) {
            mBranchNameStack.push(rootName);
        }
    }

    public String getBranchName() {
        return mBranchNameStack.peek();
    }

    public int getChildren(ArrayList<String> titles, ArrayList<Integer> types) {
        for (DefaultMutableTreeNode curNode : mOutlineCursor.getChildren()) {
            OutlineNode aNode = (OutlineNode) curNode;
            titles.add(aNode.toString());
            if (curNode.getChildCount() > 0) {
                types.add(0);
            } else {
                types.add(1);
            }
        }
        return mOutlineCursor.getChildCount();
    }

    public int getChildrenCount(int idx) {
        OutlineNode aNode = (OutlineNode) mOutlineCursor.getChildren().get(idx);
        return aNode.getChildCount();
    }

    public int getPageNo(int idx) {
        OutlineNode aNode = (OutlineNode) mOutlineCursor.getChildren().get(idx);

        PDFAction action = aNode.getAction();
        int thePageNum =0;

        if (action != null) {
            if (action instanceof GoToAction) {
                PDFDestination dest = ((GoToAction) action).getDestination();

                if (dest != null) {
                    PDFObject page = dest.getPage();

                    if (page != null) {
                        try {
                            thePageNum = getPageNumber(page);
                        } catch (IOException ex) {
                        }
                    }
                }
            }
        }

        return thePageNum;
    }

    public int getPageNumber(PDFObject page) throws IOException {
        if (page.getType() == PDFObject.ARRAY) {
            page = page.getAt(0);
        }

        // now we've got a page.  Make sure.
        PDFObject typeObj = page.getDictRef("Type");
        if (typeObj == null || !typeObj.getStringValue().equals("Page")) {
            return 0;
        }

        int count = 0;
        while (true) {
            PDFObject parent = page.getDictRef("Parent");
            if (parent == null) {
                break;
            }
            PDFObject kids[] = parent.getDictRef("Kids").getArray();
            for (int i = 0; i < kids.length; i++) {
                if (kids[i].equals(page)) {
                    break;
                } else {
                    PDFObject kcount = kids[i].getDictRef("Count");
                    if (kcount != null) {
                        count += kcount.getIntValue();
                    } else {
                        count += 1;
                    }
                }
            }
            page = parent;
        }
        return count+1;
    }

    public void moveTo(int idx) {
        if (idx < mOutlineCursor.getChildCount()) {
            OutlineNode aNode = (OutlineNode) mOutlineCursor.getChildren().get(idx);
            mBranchNameStack.push(aNode.toString());
            mOutlineCursor = aNode;
        }
    }

    public void moveBackUp() {
        if (mOutlineCursor != mOutline) {
            mOutlineCursor = (OutlineNode) mOutlineCursor.getParent();
            mBranchNameStack.pop();
        }
    }

}
