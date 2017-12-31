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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.hsl.txtreader.pdf.DocOutline;


public class PagePicker extends Picker {
    public static final String KEY_PAGE_NO = "PageNo";
    public static final String KEY_OUTLINE = "Outline";
    private DocOutline mOutline;
    private ArrayList<Integer> mTypes;
    private Bitmap mBranchIcon, mLeafIcon;
    private String mTitle;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTypes = new ArrayList<Integer>();


        mBranchIcon = BitmapFactory.decodeResource(getResources(), R.drawable.branch);
        mLeafIcon = BitmapFactory.decodeResource(getResources(), R.drawable.leaf);

        setTitle(R.string.page_picker);
        setHeadIcon(R.drawable.back);

        TextReaderApp ap = (TextReaderApp) getApplication();
        mOutline = (DocOutline) ap.getObject(TextReaderApp.SHARE_OBJ_OUTLINE);

        mTitle = getString(R.string.outline);
        mOutline.setRootName(mTitle);
        exploreCurOutline();
    }

    private void exploreCurOutline() {
        mTitle = mOutline.getBranchName();
        setHeadText(mTitle);
        buildOutlineList();
        setListAdapter(new listAdapter(this, mItems, mIcons));
    }

    public void buildOutlineList() {
        mItems.clear();
        mIcons.clear();
        mTypes.clear();

        mOutline.getChildren(mItems, mTypes);
        for (Integer type : mTypes) {
            if (type == 0) {
                mIcons.add(mBranchIcon);
            } else {
                mIcons.add(mLeafIcon);
            }

        }
    }

    @Override
    public void onClick(View v) {
        mOutline.moveBackUp();
        exploreCurOutline();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mOutline.getChildrenCount(position) > 0) {
            mOutline.moveTo(position);
            exploreCurOutline();
        } else {
            int pageNum = mOutline.getPageNo(position);
            Intent intent = new Intent();
            intent.putExtra(KEY_PAGE_NO, pageNum);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}