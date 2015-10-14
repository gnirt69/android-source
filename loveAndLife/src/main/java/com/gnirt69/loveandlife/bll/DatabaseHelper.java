package com.gnirt69.loveandlife.bll;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.gnirt69.loveandlife.model.Common;
import com.gnirt69.loveandlife.model.QuoteItem;
import com.gnirt69.loveandlife.model.StoryItem;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table name
    static final String DB_TABLE_NAME_STORYTABLE = "StoryTable";
    static final String DB_TABLE_NAME_FAVOURITE = "FavouriteStory";
    static final String DB_TABLE_NAME_QUOTETABLE = "QuoteTable";
    // Field name of table
    static final String DB_TABLE_FIELD_STORY_ID = "StoryId";
    static final String DB_TABLE_FIELD_CATEGORY_ID = "CategoryId";
    static final String DB_TABLE_FIELD_STORY_NAME = "StoryName";
    static final String DB_TABLE_FIELD_STORY_CONTENT = "StoryContent";
    static final String DB_TABLE_FIELD_AUTHOR = "Author";
    static final String DB_TABLE_FIELD_URLREFER = "UrlRefer";
    static final String DB_TABLE_FIELD_DATETIME = "TimeToAdd";
    static final String DB_TABLE_FIELD_QUOTEID = "Id";
    static final String DB_TABLE_FIELD_QUOTECONTENT = "QuoteContent";

    private Context context;

    private SQLiteDatabase myDataBase;

    public DatabaseHelper(Context context) {
        super(context, Common.DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

   // public void openDatabase() throws SQLException {
     public void openDatabase(){

        String myPath = Common.DB_PATH + Common.DB_NAME;
        if (myDataBase != null && myDataBase.isOpen()) {
            return;
        }
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        Common.writeLogCat("DATABASE", "Database opened");
    }

    public void closeDatabase() {
        close();
    }
    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    /**
     * Copy database from asset to database location in device
     *
     * @return
     */
    public boolean copyDatabase() {
        try {

            InputStream myInput = context.getAssets().open(Common.DB_NAME);
            String outFileName = Common.DB_PATH + Common.DB_NAME;
            // if (myDataBase != null) {
            // myDataBase.close();
            // }
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
            Common.writeLogCat("DB", "DB was copied");
            return true;
        } catch (Exception e) {
            Common.writeLogCat("DB", "DB copy err");
            return false;
        }

    }

    /**
     * Get list of story in database
     *
     * @param categoryId
     * @return array of story
     */
    public ArrayList<StoryItem> getListStoryByCategory(int categoryId) {

        StoryItem storyItem = null;
        ArrayList<StoryItem> listResult = new ArrayList<StoryItem>();
        openDatabase();
        Cursor cursor = myDataBase.query(DB_TABLE_NAME_STORYTABLE,
                new String[]{DB_TABLE_FIELD_STORY_ID,
                        DB_TABLE_FIELD_STORY_NAME}, DB_TABLE_FIELD_CATEGORY_ID
                        + " = ?", new String[]{String.valueOf(categoryId)},
                null, null, DB_TABLE_FIELD_STORY_ID);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            storyItem = new StoryItem(cursor.getInt(0), Encrypter.decryptIt(cursor.getString(1)));
            listResult.add(storyItem);
            cursor.moveToNext();
        }
        Common.writeLogCat("DATABASE",
                "getListStoryByCategory return num record:" + listResult.size());
        cursor.close();
        closeDatabase();
        return listResult;
    }

    /**
     * Get story name, story content and author of story
     *
     * @param storyId
     * @return array[0] = name, array[1] = content, array[2] = author
     */
    public String[] getNameAndContentAndAuthorById(int storyId) {
        String[] strReturn = null;
        openDatabase();
        Cursor cursor = myDataBase.query(DB_TABLE_NAME_STORYTABLE,
                new String[]{DB_TABLE_FIELD_STORY_NAME,
                        DB_TABLE_FIELD_STORY_CONTENT, DB_TABLE_FIELD_AUTHOR},
                DB_TABLE_FIELD_STORY_ID + "=?",
                new String[]{String.valueOf(storyId)}, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            strReturn = new String[3];
            strReturn[0] = Encrypter.decryptIt(cursor.getString(0));// NAME
            strReturn[1] = Encrypter.decryptIt(cursor.getString(1));// CONTENT
            strReturn[2] = Encrypter.decryptIt(cursor.getString(2));// AUTHOR
        }
        cursor.close();
        closeDatabase();
        return strReturn;
    }

    public long addFavouriteStory(int storyId) {
        //Get current datetime
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_TABLE_FIELD_STORY_ID, storyId);
        contentValues.put(DB_TABLE_FIELD_DATETIME, getCurrentDateTimeToString());
        openDatabase();
        long returnValue = myDataBase.insert(DB_TABLE_NAME_FAVOURITE, null, contentValues);
        closeDatabase();
        Common.writeLogCat("DATABASE", "addFavouriteStory result :" + returnValue);
        return returnValue;
    }

    public ArrayList<StoryItem> getListFavouriteStory() {

        String sqlQuery = "SELECT STORYTABLE.STORYID, STORYNAME, STORYCONTENT,AUTHOR FROM STORYTABLE " +
                "JOIN FAVOURITESTORY WHERE STORYTABLE.STORYID = FAVOURITESTORY.STORYID  " +
                "ORDER BY TIMETOADD";
        StoryItem storyItem = null;
        ArrayList<StoryItem> listResult = new ArrayList<StoryItem>();
        openDatabase();
        Cursor cursor = myDataBase.rawQuery(sqlQuery, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            storyItem = new StoryItem(cursor.getInt(0), Encrypter.decryptIt(cursor.getString(1)));
            listResult.add(storyItem);
            cursor.moveToNext();
        }
        Common.writeLogCat("DATABASE",
                "getListStoryByCategory return num record:" + listResult.size());
        cursor.close();
        closeDatabase();
        return listResult;
    }

    public String getCurrentDateTimeToString() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        return df.format(c.getTime());
    }

    public boolean isFavouriteStory(int storyId) {
        openDatabase();
        Cursor cursor = myDataBase.rawQuery("SELECT COUNT(*) FROM FavouriteStory WHERE StoryId = ?", new String[]{String.valueOf(storyId)});

        cursor.moveToFirst();
        int result = cursor.getInt(0);
        Common.writeLogCat("DATABASE", "isFavouriteStory :" + result);
        cursor.close();
        closeDatabase();
        return  result != 0;
    }

    public boolean deleteFavouriteStory(int storyId) {
        openDatabase();
        int result = myDataBase.delete(DB_TABLE_NAME_FAVOURITE, DB_TABLE_FIELD_STORY_ID +"=?", new String[]{String.valueOf(storyId)});
        closeDatabase();
        Common.writeLogCat("DATABASE", "deleteFavouriteStory :" + result);
        return result !=0;
    }

    public int getNumOfFavouriteStory() {
        openDatabase();
        Cursor cursor = myDataBase.rawQuery("SELECT COUNT(*) FROM FavouriteStory", null);

        cursor.moveToFirst();
        int result = cursor.getInt(0);
        Common.writeLogCat("DATABASE", "getNumOfFavouriteStory :" + result);
        cursor.close();
        closeDatabase();
        return  result;
    }

    public ArrayList<QuoteItem> getAllQuoteTable() {
        ArrayList<QuoteItem> lstQuote = new ArrayList<QuoteItem>();

        openDatabase();
        Cursor cursor = myDataBase.query(DB_TABLE_NAME_QUOTETABLE,
                new String[]{DB_TABLE_FIELD_QUOTEID,
                        DB_TABLE_FIELD_QUOTECONTENT},null,null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            lstQuote.add(new QuoteItem(cursor.getInt(0), Encrypter.decryptIt(cursor.getString(1))));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        Common.writeLogCat("DATABASE", "getAllQuoteTable return:" + lstQuote.size());
        return lstQuote;
    }
}
