package com.gnirt69.loveandlife;/**
 * Created by NgocTri on 9/12/2015.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gnirt69.loveandlife.bll.DatabaseHelper;
import com.gnirt69.loveandlife.bll.QuoteListAdapter;
import com.gnirt69.loveandlife.model.Common;
import com.gnirt69.loveandlife.model.QuoteItem;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class QuoteListActivity extends Activity {
    private ListView lvQuote;
    private DatabaseHelper myDBHelper;
    private QuoteListAdapter adapter = null;
    private int currentPosListView = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quote_list_activity);
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Trích dẫn hay");
        lvQuote =(ListView)findViewById(R.id.list_quote);
        ArrayList<QuoteItem> lstQuote = new ArrayList<QuoteItem>();
        myDBHelper = new DatabaseHelper(getApplicationContext());
        lstQuote = myDBHelper.getAllQuoteTable();
        adapter = new QuoteListAdapter(getApplicationContext(), lstQuote);
        lvQuote.setAdapter(adapter);

        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }

        lvQuote.setOnScrollListener(scrollListener);
        registerForContextMenu(lvQuote);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lvQuote.setSelection(Common.currentPostQuoteListview );
        Common.writeLogCat("QuoteListActivity", "onStart:" + Common.currentPostQuoteListview);
    }

    @Override
    protected void onPause() {
        Common.currentPostQuoteListview = currentPosListView;
        Common.writeLogCat("QuoteListActivity", "onPausee:" + Common.currentPostQuoteListview);
        super.onPause();
        SharedPreferences settings = getSharedPreferences(Common.SETTING_SHAREREFER_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Common.SETTING_SESSION_CURRPOS, currentPosListView);
        editor.commit();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_quote, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        View v = (View) lvQuote.getAdapter().getView(info.position, null, null);
        String quoteContent = ((QuoteListAdapter.ViewHolderQuote)v.getTag()).quoteContent.getText().toString();
        int dotPos = quoteContent.indexOf('.');
        quoteContent = quoteContent.substring(dotPos + 2);//Ignore number prefix in content
        switch (item.getItemId()) {
            case R.id.contextMenuSendSMS:
                Intent iSMS = new Intent(QuoteListActivity.this, SendSMSActivity.class);
                iSMS.putExtra("QuoteContent", quoteContent);
                startActivityForResult(iSMS, 1);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            case R.id.contextMenuShareFacebook:
                Intent i = new Intent(QuoteListActivity.this,ShareFacebookActivity.class);
                i.putExtra("StoryId", -1);
                i.putExtra("QuoteContent", quoteContent);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Press back button in action bar
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
                return true;
            default:
                break;
        }

        return true;
    }
    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //Common.writeLogCat("QuoteListActivity", "OnScroll:" + firstVisibleItem);
            currentPosListView = firstVisibleItem;
        }
    };
}
