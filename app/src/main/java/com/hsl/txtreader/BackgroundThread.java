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

import android.os.Handler;

public class BackgroundThread {
    private Handler handler;
    private Runnable doStuff;
    private Runnable done;
    private Thread thread;

    public BackgroundThread(Handler aHandler, Runnable aDoStuff, Runnable aDone) {
        handler = aHandler;
        doStuff = aDoStuff;
        done = aDone;

        thread = new Thread(null, doBackgroundProcessing, "BackgroundThread");
        thread.start();
    }

    private Runnable doBackgroundProcessing = new Runnable() {
        public void run() {
            doStuff.run();
            handler.post(done);
        }
    };

}
