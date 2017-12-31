package com.hsl.txtreader.pdf.port;

import android.graphics.RectF;

public class ImageObserver extends RectF {
    public ImageObserver(float x, float y, float w, float h) {
        super(x, y, x+w, y+h);
    }
}
