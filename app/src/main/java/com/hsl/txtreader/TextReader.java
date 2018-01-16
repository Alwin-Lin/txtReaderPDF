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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TextReader extends Activity {
    private static final String README_FILE_NAME = "txtReader.pdf";
    private static final int ACTIVITY_PICK_FILE=0;
    private static final int ACTIVITY_PICK_PAGE=1;
    private static final int ACTIVITY_PICK_BACKGROUND_COLOR=2;
    private static final int ACTIVITY_PICK_FONT_COLOR=3;
    private static final int MENU_DOC_ITEM = 0;
    private static final int MENU_PAGE_ITEM = 1;
    private static final int MENU_OTHERS = 2;
    private static final int MENU_TEXT_ALIGN = 3;
    private static final int SELE_FILE = Menu.FIRST;
    private static final int SELE_PRE_PAGE = Menu.FIRST+1;
    private static final int SELE_NEXT_PAGE = Menu.FIRST+2;
    private static final int SELE_GO = Menu.FIRST+3;
    private static final int SELE_INDEX = Menu.FIRST+4;
    private static final int SELE_SETTINGS = Menu.FIRST+5;
    private static final int SELE_SETTINGS_FONT_SIZE = Menu.FIRST+6;
    private static final int SELE_SETTINGS_BG_COLOR = Menu.FIRST+7;
    private static final int SELE_SETTINGS_FONT_COLOR = Menu.FIRST+8;
    private static final int SELE_SETTINGS_TEXT_ALIGN_LEFT = Menu.FIRST+9;
    private static final int SELE_SETTINGS_TEXT_ALIGN_FULL = Menu.FIRST+10;
    private static final int SELE_INFO = Menu.FIRST+11;


    private static final String CURRENT_PATH_KEY = "CURRENT_PATH_KEY";
    private static final String CURRENT_FILE_KEY = "CURRENT_FILE_KEY";
    private static final String CURRENT_PAGE_KEY = "CURRENT_PAGE_KEY";
    private static final String CURRENT_X_KEY = "CURRENT_X_KEY";
    private static final String CURRENT_Y_KEY = "CURRENT_Y_KEY";
    private static final String CURRENT_BG_COLOR_KEY = "CURRENT_BG_COLOR_KEY";
    private static final String CURRENT_FONT_COLOR_KEY = "CURRENT_FONT_COLOR_KEY";
    private static final String CURRENT_FONT_SIZE_KEY = "CURRENT_FONT_SIZE_KEY";
    private static final String CURRENT_TEXT_ALIGN_KEY = "CURRENT_TEXT_ALIGN_KEY";

    private static final String mimeType = "text/html";
    private static final String encoding = "utf-8";

    private DocMgr docMgr;

    private String mReadMeFileName;
    private String dataFileName;
    private String curPath;
    private int page;
    private int numPages;
    private Dialog dlg;
    private PageCacheManager pageCacheMgr;
    private WebView webView;
    private int curX, curY;

    private Handler handler;
    private StringBuffer mContentStringBuffer;
    private String mStyle;
    private String mTextAlign;
    private int mBGColor, mFontColor, mFontSize;
    private int mMenuItemId;
    private MenuItem mAlignLeft;
    private MenuItem mAlignFull;

    private boolean inProgress;
    private TextView fileNameTxt;
    private TextView pageNoTxt;
    private ProgressBar progressBar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        handler = new Handler();
        docMgr = new DocMgr();
        pageCacheMgr = new PageCacheManager(this);
        fileNameTxt = findViewById(R.id.file_name);
        pageNoTxt = findViewById(R.id.page_no);
        progressBar = findViewById(R.id.determinateBar);
        webView = findViewById(R.id.pdfPageWebView);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.requestFocus();
        registerForContextMenu(webView);

        curX = 0;
        curY = 0;
        inProgress = false;

        restoreUIState();
    }

    private void restoreUIState() {
        SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);

        curPath = settings.getString(CURRENT_PATH_KEY, "/");
        dataFileName = settings.getString(CURRENT_FILE_KEY, "");

        mTextAlign = settings.getString(CURRENT_TEXT_ALIGN_KEY, "justify");
        mBGColor = settings.getInt(CURRENT_BG_COLOR_KEY, 0x0);
        mFontColor = settings.getInt(CURRENT_FONT_COLOR_KEY, 0xFFFFFF);
        mFontSize = settings.getInt(CURRENT_FONT_SIZE_KEY, 20);

        page = settings.getInt(CURRENT_PAGE_KEY, 1);
        curX = settings.getInt(CURRENT_X_KEY, 0);
        curY = settings.getInt(CURRENT_Y_KEY, 0);

        buildStyle();

        Intent intent = getIntent();
        if (intent != null  &&
                Intent.ACTION_VIEW.equals(intent.getAction())) {
            dataFileName = intent.getData().getPath();
            page = 1;
            requestPage();
        } else if (dataFileName.equals("")) {
            //pickFile();
            openReadMeFile();
        } else {
            mContentStringBuffer = new StringBuffer(pageCacheMgr.getCachedPage(dataFileName, page));
            updateView();
            setTitle();
            openDoc();
        }
    }

    private void updateView() {
        //webView.loadData(mStyle+mContentStringBuffer.toString(), mimeType, encoding);
        //seem loadData has problem with encoding(loading data as a data: Uri)
        //webView.reload();

        webView.loadDataWithBaseURL("x-data://base", mStyle+mContentStringBuffer.toString(), mimeType, encoding, null);
    }

    private void openReadMeFile() {
        try {
        	String nameTag = getString(R.string.read_me_file); 
        	int fileID = getResources().getIdentifier(nameTag, "raw",   getPackageName());
        	Log.i("TextReader", nameTag+", "+fileID);
            InputStream inFile = getResources().openRawResource(fileID);
            
            FileOutputStream outFile = openFileOutput(README_FILE_NAME, Context.MODE_PRIVATE);
            byte [] buf = new byte[2048];
            int count;
            while ((count = inFile.read(buf)) != -1) {
                outFile.write(buf, 0, count);
            }
            inFile.close();
            outFile.close();

        } catch (IOException exc) {
            Log.e(this.toString(), "Coping R.raw.textreader to FS error", exc);
        }

        File dataPathFile = getFilesDir();
        String aName = new String(dataPathFile.getAbsolutePath() +"/"+ README_FILE_NAME);
        File aFile = new File(aName);
        aFile = new File(aName);
        if (aFile.exists()) {
            mReadMeFileName = aName;
            dataFileName = aName;
            page = 1;
            requestPage();
        }
    }

    private void setTitle() {
        fileNameTxt.setText(dataFileName);
        pageNoTxt.setText(page+"/"+numPages);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(R.string.menu_settings);
        menu.setHeaderIcon(android.R.drawable.ic_menu_preferences);

        menu.add(MENU_OTHERS, SELE_SETTINGS_FONT_SIZE, Menu.NONE, R.string.menu_settings_font_size);
        menu.add(MENU_OTHERS, SELE_SETTINGS_BG_COLOR, Menu.NONE, R.string.menu_settings_bg_color);
        menu.add(MENU_OTHERS, SELE_SETTINGS_FONT_COLOR, Menu.NONE, R.string.menu_settings_font_color);
        mAlignLeft = menu.add(MENU_TEXT_ALIGN, SELE_SETTINGS_TEXT_ALIGN_LEFT, Menu.NONE, R.string.menu_settings_text_align_left);
        mAlignFull = menu.add(MENU_TEXT_ALIGN, SELE_SETTINGS_TEXT_ALIGN_FULL, Menu.NONE, R.string.menu_settings_text_align_full);
        if (mTextAlign.equals("left")) {
            mAlignLeft.setChecked(true);
        } else {
            mAlignFull.setChecked(true);
        }
        menu.setGroupCheckable(MENU_TEXT_ALIGN, true, true);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        mMenuItemId = item.getItemId();
        return onSettingMenu();
    }

    private boolean onSettingMenu() {
        switch (mMenuItemId) {
        case SELE_SETTINGS_FONT_SIZE:
            dlg = new NumberPicker(this, R.string.menu_settings_font_size, mFontSize, 100, 6, new View.OnClickListener() {
                public void onClick(View view) {
                    EditText pageNoEdit = dlg.findViewById(R.id.number_editor);

                    try {
                        mFontSize = Integer.parseInt(pageNoEdit.getText().toString());
                    } catch (Exception ex) {

                    }
                    dlg.dismiss();
                    buildStyle();
                    updateView();
                }
            });
            dlg.show();
            return true;
        case SELE_SETTINGS_BG_COLOR:
            pickColor(mBGColor, R.string.background_color_picker, ACTIVITY_PICK_BACKGROUND_COLOR);
            return true;
        case SELE_SETTINGS_FONT_COLOR:
            pickColor(mBGColor, R.string.font_color_picker, ACTIVITY_PICK_FONT_COLOR);
            return true;

        case SELE_SETTINGS_TEXT_ALIGN_LEFT:
            mTextAlign = new String("left");
            mAlignLeft.setChecked(true);
            buildStyle();
            updateView();
            return true;

        case SELE_SETTINGS_TEXT_ALIGN_FULL:
            mTextAlign = new String("justify");
            mAlignFull.setChecked(true);
            buildStyle();
            updateView();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(MENU_DOC_ITEM, SELE_FILE,0, R.string.menu_sele_file).setIcon(R.drawable.open);
        menu.add(MENU_DOC_ITEM, SELE_INDEX,0, R.string.menu_index).setIcon(R.drawable.branch);
        menu.add(MENU_PAGE_ITEM, SELE_GO,0, R.string.menu_go).setIcon(android.R.drawable.ic_media_ff);
        menu.add(MENU_PAGE_ITEM, SELE_PRE_PAGE,0, R.string.menu_pre_page).setIcon(R.drawable.minus);
        menu.add(MENU_PAGE_ITEM, SELE_NEXT_PAGE,0, R.string.menu_next_page).setIcon(R.drawable.plus);
        menu.add(MENU_OTHERS, SELE_INFO,0, R.string.menu_info).setIcon(R.drawable.info);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
    public boolean onMenuOpened(int featureId, Menu menu) {
        super.onMenuOpened(featureId, menu);
        
        if (inProgress) {
            Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (numPages>1 && menu!= null) {
                menu.setGroupVisible(MENU_PAGE_ITEM, true);
            } else {
                if (numPages<1 && menu!= null) {
                    menu.setGroupVisible(MENU_PAGE_ITEM, false);
                }
                else {onPrepareOptionsMenu(menu);{
                    return super.onPrepareOptionsMenu(menu);
                }

                }
            }


            if (docMgr.getOutline() != null) {
                menu.findItem(SELE_INDEX).setVisible(true);
            } else {
                menu.findItem(SELE_INDEX).setVisible(false);
            }

        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        mMenuItemId = item.getItemId();
        switch (mMenuItemId) {
        case SELE_INFO:
            openReadMeFile();
            return true;

        case SELE_FILE:
            pickFile();
            return true;
        case SELE_INDEX:
            pickPage();
            return true;
        case SELE_PRE_PAGE:
            if (page!=1) {
                page -= 1;
                requestPage();
            }
            return true;
        case SELE_NEXT_PAGE:
            if (page!=numPages) {
                page += 1;
                requestPage();
            }
            return true;
        case SELE_GO:
            dlg = new NumberPicker(this, R.string.dialog_goto_page, page, numPages, 1, new View.OnClickListener() {
                public void onClick(View view) {
                    EditText pageNoEdit = dlg.findViewById(R.id.number_editor);

                    try {
                        page = Integer.parseInt(pageNoEdit.getText().toString());
                    } catch (Exception ex) {

                    }
                    page = (page < 1) ? 1 : ((page > numPages) ? numPages : page);
                    dlg.dismiss();
                    requestPage();
                }
            });
            dlg.show();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
        case ACTIVITY_PICK_FILE:
            if (resultCode == RESULT_OK) {
                dataFileName = intent.getStringExtra(FilePicker.KEY_FILE_NAME);
                curPath = intent.getStringExtra(FilePicker.KEY_PATH);
                page = 1;
                requestPage();
            }
            break;
        case ACTIVITY_PICK_PAGE:
            if (resultCode == RESULT_OK) {
                page = intent.getIntExtra(PagePicker.KEY_PAGE_NO, 1);
                requestPage();
            }
            break;
        case ACTIVITY_PICK_BACKGROUND_COLOR:
            if (resultCode == RESULT_OK) {
                mBGColor = (intent.getIntExtra(ColorPicker.KEY_COLOR, 0x0) &0xFFFFFF);
                buildStyle();
                updateView();
            }
            break;

        case ACTIVITY_PICK_FONT_COLOR:
            if (resultCode == RESULT_OK) {
                mFontColor = (intent.getIntExtra(ColorPicker.KEY_COLOR, 0xFFFFFF) &0xFFFFFF);
                buildStyle();
                updateView();
            }
            break;

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences uiState = getPreferences(0);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(CURRENT_PATH_KEY, curPath);
        editor.putString(CURRENT_FILE_KEY, dataFileName);
        editor.putInt(CURRENT_PAGE_KEY, page);

        editor.putString(CURRENT_TEXT_ALIGN_KEY, mTextAlign);
        editor.putInt(CURRENT_BG_COLOR_KEY, mBGColor);
        editor.putInt(CURRENT_FONT_COLOR_KEY, mFontColor);
        editor.putInt(CURRENT_FONT_SIZE_KEY, mFontSize);

        editor.putInt(CURRENT_X_KEY, webView.getScrollX());
        editor.putInt(CURRENT_Y_KEY, webView.getScrollY());
        editor.commit();
    }

    private void pickFile() {
        Intent intent = new Intent(this, FilePicker.class);
        intent.putExtra(FilePicker.KEY_PATH, curPath);
        startActivityForResult(intent, ACTIVITY_PICK_FILE);
    }

    private void pickPage() {
        Intent intent = new Intent(this, PagePicker.class);
        TextReaderApp ap = (TextReaderApp) getApplication();
        ap.putObject(TextReaderApp.SHARE_OBJ_OUTLINE, docMgr.getOutline());
        startActivityForResult(intent, ACTIVITY_PICK_PAGE);
    }

    private void pickColor(int color, int title, int activity) {
        Intent intent = new Intent(this, ColorPicker.class);
        intent.putExtra(ColorPicker.KEY_COLOR, color);
        intent.putExtra(ColorPicker.KEY_TITLE, title);
        startActivityForResult(intent, activity);
    }

    private void setInProgress(boolean state) {
        inProgress = state;
        if (inProgress) {
            pageNoTxt.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            setProgressBarIndeterminateVisibility(inProgress);
        } else {
            pageNoTxt.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void openDoc() {
        if (!inProgress && !dataFileName.equals("")) {
            setInProgress(true);
            new BackgroundThread(handler, doOpenDoc, openDocDone);
        }
    }

    private void buildStyle() {
        StringBuffer styleStringBuffer = new StringBuffer("<!DOCTYPE html> <html>");
        styleStringBuffer.append("<style type=text/css>");
        Formatter f = new Formatter();
        styleStringBuffer.append(f.format("body { color:#%06x; background-color:#%06x;  text-align:%s; font-size:%dpx; }",
                                          mFontColor, mBGColor, mTextAlign, mFontSize));
        styleStringBuffer.append("</STYLE>");
        mStyle = styleStringBuffer.toString();
    }

    private void requestPage() {
        if (!inProgress) {
            setInProgress(true);
            new BackgroundThread(handler, doRequestPage, pageDone);
        }
    }

    private Runnable doRequestPage = new Runnable() {
        public void run() {
            docMgr.openDoc(dataFileName);
            numPages = docMgr.getNumPages();

            mContentStringBuffer = new StringBuffer("<body> <p>");
            // Method tracing to "/sdcard/requestPage.trace"
            //Debug.startMethodTracing("requestPage");
            mContentStringBuffer.append(docMgr.getPageContent(page));
            // Stop Method tracing
            //Debug.stopMethodTracing();
            mContentStringBuffer.append("</p> </body> </html>");

        }
    };

    private Runnable pageDone = new Runnable() {
        public void run() {
            updateView();
            //To Do: find a way to scroll back to the top
            //webView.pageUp(true); //does not work.

            setInProgress(false);
            setTitle();
            pageCacheMgr.putPageCached(dataFileName, page, mContentStringBuffer.toString());
        }
    };

    private Runnable doOpenDoc = new Runnable() {
        public void run() {
            docMgr.openDoc(dataFileName);
            numPages = docMgr.getNumPages();
        }
    };

    private Runnable openDocDone = new Runnable() {
        public void run() {
            setInProgress(false);
            setTitle();
        }
    };
}
