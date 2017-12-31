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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

public class ColorPicker extends Activity {
    public static final String KEY_COLOR = "COLOR";
    public static final String KEY_TITLE = "TITLE";

    private int mColor;
    private TouchSurfaceView mGLSurfaceView;
    private float mMinMovement = 10f;
    private int mHitSpotSize = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        setTitle(intent.getIntExtra(KEY_TITLE, R.string.color_picker));

        mColor = intent.getIntExtra(KEY_TITLE, 0xFFFFFF);

        mGLSurfaceView = new TouchSurfaceView(this);
        setContentView(mGLSurfaceView);
        mGLSurfaceView.requestFocus();
        mGLSurfaceView.setFocusableInTouchMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    private class TouchSurfaceView extends GLSurfaceView {
        private ModelRenderer mRenderer;
        private float mPreviousX;
        private float mPreviousY;

        public TouchSurfaceView(Context context) {
            super(context);
            mRenderer = new ModelRenderer(new ModelData());
            setRenderer(mRenderer);
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        // -1 to 1
        private float normalizeX(float x) {
            return (float)(x/this.getWidth() - 0.5f) * 2;
        }

        // 1 to -1
        private float normalizeY(float y) {
            return (float)(0.5 - y/this.getHeight()) * 2;
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            float x = evt.getX();
            float y = evt.getY();
            float dx = x - mPreviousX;
            float dy = y - mPreviousY;

            switch (evt.getAction()) {
            case MotionEvent.ACTION_UP:
                if (dx < mMinMovement && dy < mMinMovement) {
                    if (x > this.getWidth()/2-mHitSpotSize &&
                            x < this.getWidth()/2+mHitSpotSize &&
                            y > this.getHeight()/2-mHitSpotSize &&
                            y < this.getHeight()/2+mHitSpotSize) {

                        mColor = mRenderer.getColor();
                        Intent intent = new Intent();
                        intent.putExtra(KEY_COLOR, mColor);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }
            case MotionEvent.ACTION_MOVE:
                mRenderer.mP0[0] = normalizeX(mPreviousX);
                mRenderer.mP0[1] = normalizeY(mPreviousY);
                mRenderer.mP1[0] = normalizeX(x);
                mRenderer.mP1[1] = normalizeY(y);
                //Log.i("onTouchEvent", mRenderer.mP0[0] + ", " + mRenderer.mP0[1] + " - " +
                //                      mRenderer.mP1[0] + ", " + mRenderer.mP1[1]);

                requestRender();
            }

            mPreviousX = x;
            mPreviousY = y;
            return true;
        }
    }
}

