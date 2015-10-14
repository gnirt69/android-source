package com.gnirt69.loveandlife;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import com.gnirt69.loveandlife.bll.CategoryListAdapter;
import com.gnirt69.loveandlife.bll.DatabaseHelper;
import com.gnirt69.loveandlife.model.Category;
import com.gnirt69.loveandlife.model.Common;
import com.gnirt69.loveandlife.model.StoryItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private CategoryListAdapter adapter;
    private ArrayList<Category> lstCategory;
    private ListView listViewCategory;
    private ProgressDialog mProgressDialog;
    private DatabaseHelper dbHelper = null;
    private ArrayList<StoryItem> lstFavouriteStory = null;
    private boolean isDBReady = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        listViewCategory = (ListView) findViewById(R.id.list_category);
        // Check and copy database to db location when not exist
        checkAndCopyDatabase();
        getSettingData();
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }

        //Test git
        lstCategory = new ArrayList<Category>();
        lstCategory.add(new Category(1, "Gia đình", R.drawable.family2));
        lstCategory.add(new Category(2, "Cuộc sống", R.drawable.life1));
        lstCategory.add(new Category(3, "Tình yêu", R.drawable.love1));
        lstCategory.add(new Category(4, "Tình bạn", R.drawable.friend1));
        lstCategory.add(new Category(5, "Trường học", R.drawable.school1));
        lstCategory.add(new Category(6, "Trích dẫn hay", R.drawable.ic_quote));

        if (true == isDBReady) {
            addFavouriteCategory();
            Common.writeLogCat("MainActivity", "MainThread add fav");
        }
        adapter = new CategoryListAdapter(getApplicationContext(), lstCategory);

        listViewCategory.setAdapter(adapter);
        Common.writeLogCat("MainActivity", "MainThread set adapter");

        listViewCategory.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (6 == lstCategory.get(position).getCategoryId()) {
                    Intent i = new Intent(getApplicationContext(),
                            QuoteListActivity.class);
                    startActivity(i);
//                    overridePendingTransition(android.R.anim.slide_in_left,
//                            android.R.anim.slide_out_right);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                } else {
                    Intent i = new Intent(getApplicationContext(),
                            StoryListActivity.class);
                    i.putExtra("CategoryName", lstCategory.get(position)
                            .getCategoryName());
                    i.putExtra("CategoryId", lstCategory.get(position)
                            .getCategoryId());
                    startActivityForResult(i, 1);
//                    overridePendingTransition(android.R.anim.slide_in_left,
//                            android.R.anim.slide_out_right);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case R.id.menuItemShareFace:
                i = new Intent(MainActivity.this, ShareAppFBActivity.class);
                startActivityForResult(i, 2);
//                overridePendingTransition(android.R.anim.slide_in_left,
//                        android.R.anim.slide_out_right);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;

            case R.id.menuItemSendFeedback:
                i = new Intent(MainActivity.this, SendFeedbackActivity.class);
                startActivityForResult(i, 3);
//                overridePendingTransition(android.R.anim.slide_in_left,
//                        android.R.anim.slide_out_right);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;

            default:
                break;
        }

        return true;
    }

    // Handler double tap in back button
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onResume() {
        super.onResume();
        this.doubleBackToExitPressedOnce = false;
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            System.exit(0);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn Back 1 lần nửa để thoát ứng dụng",
                Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (lstCategory.size() == 6) {
            addFavouriteCategory();
        } else {
            removeFavouriteCategory();
        }
        adapter.updateList(lstCategory);
        Common.writeLogCat("MainActivity", "onActivityResult");
    }

    /**
     * Check database, if not exists,copy it form asset to database location
     */
    public void checkAndCopyDatabase() {
        File database = getApplicationContext().getDatabasePath(Common.DB_NAME);
        if (!database.exists()) {
            isDBReady = false;
            // Database does not exist so copy it from assets here
            Common.writeLogCat("checkAndCopyDatabase", " DB Not Found");
            dbHelper = new DatabaseHelper(this);

            dbHelper.getReadableDatabase();
            new TaskCopyData().execute();
            dbHelper.close();
        } else {
            Common.writeLogCat("checkAndCopyDatabase", " DB Found");
        }
    }

    public void getSettingData() {

        SharedPreferences settings = getSharedPreferences(Common.SETTING_SHAREREFER_NAME, Context.MODE_PRIVATE);
        Common.configValueFontColor = settings.getString(Common.SETTING_SESSION_FONTCOLOR, "#006386");
        Common.configValueFontSize = settings.getInt(Common.SETTING_SESSION_FONTSIZE, 18);
        Common.currentPostQuoteListview = settings.getInt(Common.SETTING_SESSION_CURRPOS, 0);
        Common.writeLogCat("MainActivity:GetSettingValue","Color:"+Common.configValueFontColor +",FontSize:"+Common.configValueFontSize+",Pos:"+Common.currentPostQuoteListview);
    }

    public void addFavouriteCategory() {
        if (lstCategory.size() == 7) {
            return;
        }
        lstFavouriteStory = new ArrayList<StoryItem>();
        dbHelper = new DatabaseHelper(getApplicationContext());
        lstFavouriteStory = dbHelper.getListFavouriteStory();
        Common.writeLogCat("MainActivity", "NumOfFavourite=" + lstFavouriteStory.size());
        if (lstFavouriteStory.size() > 0) {
            lstCategory.add(new Category(7, "Yêu thích", R.drawable.love3));
        }

    }

    public void removeFavouriteCategory() {
        if ((lstCategory.size() == 7) && (0 == dbHelper.getNumOfFavouriteStory())) {
            lstCategory.remove(6);
        }
    }

    // Copy database
    class TaskCopyData extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(MainActivity.this,
                    "Cài đặt dữ liệu...", "Xin đợi...", true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return dbHelper.copyDatabase();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressDialog.dismiss();
            if (result) {
                isDBReady = true;
                Toast.makeText(MainActivity.this, "Cài đặt hoàn tất! ",
                        Toast.LENGTH_SHORT).show();
                addFavouriteCategory();
                Common.writeLogCat("MainActivity", "AsyncTask");
                adapter.updateList(lstCategory);
            } else {
                Toast.makeText(MainActivity.this, "Lổi! Vui lòng thử lại",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
