package com.gnirt69.loveandlife.model;

import android.annotation.SuppressLint;
import android.util.Log;

public class Common {
    @SuppressLint("SdCardPath")
    public static final String DB_PATH = "/data/data/com.gnirt69.loveandlife/databases/";
    public static final String DB_NAME = "storydb.sqlite";
    public static int WRITE_LOGCAT_MODE = 1; // 1 = write, 0 = dont write
    public static final String FOOTER_SHARE = "\n\n< Nội dung từ Love&Life phiên bản 1.0, phát triển bởi gnirt69@gmail.com >\n\n";
    public static final String CONFIG_FILE_NAME = "config.cfg";
    public static final String LINK_APP_IN_PLAY_STORE = "https://play.google.com/store/apps/details?id=com.gnirt69.loveandlife";
    public static final String MY_EMAIL_ADDRESS = "gnirt69@gmail.com";
    public static int configValueFontSize = 0;
    public static String configValueFontColor = "#000000";//Default
    public static int currentPostQuoteListview = 0;
    public static String SETTING_SESSION_FONTSIZE = "FONT_SIZE";
    public static String SETTING_SESSION_FONTCOLOR = "FONT_COLOR";
    public static String SETTING_SESSION_CURRPOS = "CURR_POS_QUOTE";
    public static String SETTING_SHAREREFER_NAME = "SETTING";

    public static void writeLogCat(String tag, String content) {
        if (1 == WRITE_LOGCAT_MODE) {
            Log.i("TORISAN:" + tag, content);
        } else {
            // Do nothing
        }
    }
}
