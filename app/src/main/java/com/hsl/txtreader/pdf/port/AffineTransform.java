package com.hsl.txtreader.pdf.port;

import android.graphics.Matrix;

public class AffineTransform extends Matrix {

    public AffineTransform(float scaleX, float skewX, float skewY, float scaleY, float transX, float transY) {
        super();
        this.convert(scaleX, skewX, skewY, scaleY, transX, transY);
    }

    public AffineTransform(float at[]) {
        super();
        this.convert(at[0], at[1], at[2], at[3], at[4], at[5]);
    }

    public AffineTransform() {
        super();
    }

    public void convert(float scaleX, float skewX, float skewY, float scaleY, float transX, float transY) {
        float[] floatArray =  { scaleX, skewX, transX,
                                skewY, scaleY, transY,
                                0, 0, 1
                              };
        this.setValues(floatArray);
    }

    public boolean scale(double sc, double sy) {
        return this.preScale((float) sc, (float) sy);
    }

    public boolean translate(double sc, double sy) {
        return this.preTranslate((float) sc, (float) sy);
    }

    public static Matrix getTranslateInstance(float x, float y) {
        Matrix m = new Matrix();
        m.setTranslate(x, y);
        return m;
    }

    public void concatenate(Matrix translateMatrix) {
        this.preConcat(translateMatrix);
    }


    public void setTransform(Matrix m) {
        this.set(m);
    }

    public void setToIdentity() {
        this.reset();
    }

}
