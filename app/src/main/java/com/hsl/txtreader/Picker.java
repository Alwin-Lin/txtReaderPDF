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
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class Picker extends ListActivity implements OnClickListener  {

    protected ArrayList<String> mItems;
    protected ArrayList<Bitmap> mIcons;
    private TextView mHead;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<String>();
        mIcons = new ArrayList<Bitmap>();
        setContentView(R.layout.picker);
        mHead = (TextView) findViewById(R.id.head_text);
        mHead.setOnClickListener(this);

        ImageView headIcon = (ImageView) findViewById(R.id.head_icon);
        headIcon.setOnClickListener(this);

        ListView lv = getListView();
        lv.requestFocus();
        lv.setTextFilterEnabled(true);
    }

    public void setHeadText(String title) {
        mHead.setText(title);
    }

    public void setHeadIcon(int resID) {
        ImageView headIcon = (ImageView) findViewById(R.id.head_icon);
        headIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), resID));
    }

    public void onClick(View v) {

    }

    protected class listAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<Bitmap> mIcons;
        private List<String> mItems;

        public listAdapter(Context context, List<String> list, List<Bitmap> icons) {
            mItems = list;
            mIcons = icons;
            mInflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder itemHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.icon_text_item, null);

                itemHolder = new ItemHolder();
                itemHolder.text = (TextView) convertView.findViewById(R.id.text);
                itemHolder.icon = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder) convertView.getTag();
            }

            itemHolder.text.setText(mItems.get(position));
            itemHolder.icon.setImageBitmap(mIcons.get(position));

            return convertView;
        }

        class ItemHolder {
            TextView text;
            ImageView icon;
        }

        public int getCount() {
            return mItems.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

    }

}