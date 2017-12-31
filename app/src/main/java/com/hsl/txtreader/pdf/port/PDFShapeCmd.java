/*
 * $Id: PDFShapeCmd.java,v 1.3 2009/01/16 16:26:15 tomoke Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.hsl.txtreader.pdf.port;

import com.sun.pdfview.PDFCmd;

/**
 * Encapsulates a path.  Also contains extra fields and logic to check
 * for consecutive abutting anti-aliased regions.  We stroke the shared
 * line between these regions again with a 1-pixel wide line so that
 * the background doesn't show through between them.
 *
 * @author Mike Wessler
 */
public class PDFShapeCmd extends PDFCmd {

    /** stroke the outline of the path with the stroke paint */
    public static final int STROKE = 1;
    /** fill the path with the fill paint */
    public static final int FILL = 2;
    /** perform both stroke and fill */
    public static final int BOTH = 3;
    /** set the clip region to the path */
    public static final int CLIP = 4;
    /** base path */
    private GeneralPath gp;
    /** the style */
    private int style;
    /** the bounding box of the path */
    private Rectangle2D bounds;

    /**
     * create a new PDFShapeCmd and check it against the previous one
     * to find any shared edges.
     * @param gp the path
     * @param style the style: an OR of STROKE, FILL, or CLIP.  As a
     * convenience, BOTH = STROKE | FILL.
     */
    public PDFShapeCmd(GeneralPath gp, int style) {
    }

    /**
     * perform the stroke and record the dirty region
     */
    public Rectangle2D execute(PDFRenderer state) {
        return null;
    }

    /**
     * Check for overlap with the previous shape to make anti-aliased shapes
     * that are near each other look good
     */
    private GeneralPath checkOverlap(PDFRenderer state) {
        return null;
    }

    /**
     * Get an array of 16 points from a path
     * @return the number of points we actually got
     */
    private int getPoints(GeneralPath path, float[] mypoints) {
        return 0;
    }

    /** Get detailed information about this shape
     */
    @Override
    public String getDetails() {
        return "";
    }
}
