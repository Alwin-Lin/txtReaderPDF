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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class NumberPicker extends Dialog implements OnClickListener {
    private EditText mEditText;
    private ImageButton mPlusButton, mMinusButton;
    private Button mOkButton, mCancelButton;
    private int mMax;
    private int mMin;
    private int mCurNum;
    private int mTitleId;
    private View.OnClickListener mOkClickListener;

    public NumberPicker(Context context,int titleId, int num, int max, int min, View.OnClickListener onClickListener) {
        super(context);
        mTitleId = titleId;
        mCurNum = num;
        mMax = max;
        mMin = min;
        mOkClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_picker);

        setTitle(mTitleId);

        mPlusButton = (ImageButton) findViewById(R.id.plus_button);
        mMinusButton = (ImageButton) findViewById(R.id.minus_button);
        mOkButton = (Button) findViewById(R.id.ok_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mEditText = (EditText) findViewById(R.id.number_editor);
        mEditText.setText(Integer.toString(mCurNum));

        mPlusButton.setOnClickListener(this);
        mMinusButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        mOkButton.setOnClickListener(mOkClickListener);
    }

    public void onClick(View view) {
        int num = Integer.parseInt(mEditText.getText().toString());
        switch (view.getId()) {
        case R.id.plus_button:
            num = (num < mMax) ? (num + 1) : mMax;
            mEditText.setText(Integer.toString(num));
            break;
        case R.id.minus_button:
            num = (num > mMin) ? (num - 1) : mMin;
            mEditText.setText(Integer.toString(num));
            break;
        case R.id.cancel_button:
            cancel();
            break;
        }


    }
}