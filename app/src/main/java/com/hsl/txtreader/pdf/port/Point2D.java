package com.hsl.txtreader.pdf.port;
public class Point2D {
    public float x, y;

    public static class Float extends Point2D {
        public Float(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void set(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return y;
    }
}
