package com.hsl.txtreader.pdf.port;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Graphics2D extends Canvas {
    private Paint mPaint;
    public Graphics2D() {
        super();
        mPaint = new Paint();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void transform(AffineTransform at) {
        super.setMatrix(at);
    }

    public Object getTransform() {
        // TODO Auto-generated method stub
        return null;
    }

    public void fill(GeneralPath s) {
        // TODO Auto-generated method stub

    }

    public void setPaint(Paint mainPaint) {
        // TODO Auto-generated method stub

    }
}
