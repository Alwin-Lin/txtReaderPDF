package com.hsl.txtreader.pdf.port;

import android.graphics.RectF;

public class Rectangle2D extends RectF {
    public float x, y, width, height;

    public Rectangle2D(float x, float y, float w, float h) {
        super(x, y, x+w, y+h);
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public float getWidth() {
        return width();
    }

    public float getHeight() {
        return height();
    }

    public float getMinX() {
        return left;
    }

    public float getMinY() {
        return top;
    }


    public float getX() {
        return left;
    }


    public float getY() {
        return top;
    }

    public static class Float extends Rectangle2D {
        public Float(float x, float y, float w, float h) {
            super(x, y, w, h);
        }
    }

    public static class Double extends Rectangle2D {
        public Double(float x, float y, float w, float h) {
            super(x, y, w, h);
        }
    }

}
