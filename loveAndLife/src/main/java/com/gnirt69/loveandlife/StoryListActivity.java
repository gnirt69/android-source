package com.gnirt69.loveandlife;

import java.util.ArrayList;

import com.gnirt69.loveandlife.bll.DatabaseHelper;
import com.gnirt69.loveandlife.bll.StoryListAdapter;
import com.gnirt69.loveandlife.model.Category;
import com.gnirt69.loveandlife.model.Common;
import com.gnirt69.loveandlife.model.StoryItem;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class StoryListActivity extends Activity {

    private StoryListAdapter adapter;
    private ArrayList<StoryItem> lstStory;
    private ListView listViewStory;
    private DatabaseHelper dbHelper = null;
    private int imgId = 0;
    private int categoryId;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_list_activity);
        ActionBar bar = getActionBar();
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }

        //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fbecc3")));
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(getIntent().getExtras().getString("CategoryName"));
        categoryId = getIntent().getExtras().getInt("CategoryId");
        listViewStory = (ListView) findViewById(R.id.list_story);
        lstStory = new ArrayList<StoryItem>();
        dbHelper = new DatabaseHelper(getApplicationContext());
        if(7 == categoryId) {
          //Get list favourite
            lstStory = dbHelper.getListFavouriteStory();
            registerForContextMenu(listViewStory);
        } else {
            lstStory = dbHelper.getListStoryByCategory(categoryId);
        }

        switch (categoryId) {
            case 1: //Family
                imgId = R.drawable.family1;
                break;
            case 2: //Life
                imgId = R.drawable.life2;
                break;
            case 3: //Love
                imgId = R.drawable.love2;
                break;
            case 4: //friend
                imgId = R.drawable.friend2;
                break;
            case 5: //School
                imgId = R.drawable.school2;
                break;
            default: //Quote
                imgId = R.drawable.love3;
                break;
        }
        adapter = new StoryListAdapter(getApplicationContext(), lstStory, imgId);
        listViewStory.setAdapter(adapter);

        listViewStory.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getApplicationContext(),
                        StoryDetailActivity.class);
                i.putExtra("StoryId", lstStory.get(position).getStoryId());
                i.putExtra("StoryName", lstStory.get(position).getStoryName());
                startActivityForResult(i, 1);
//                overridePendingTransition(android.R.anim.slide_in_left,
//                        android.R.anim.slide_out_right);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(7 == categoryId) {
            //Get list favourite
            lstStory = dbHelper.getListFavouriteStory();
        } else {
            lstStory = dbHelper.getListStoryByCategory(categoryId);
        }
        adapter.updateList(lstStory);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Press back button in action bar
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
                return true;
            default:
                break;
        }

        return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_favourite, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.contextMenuDelete:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                //Get id of item clicked
                //Long id = adapter.getItemId(info.position);
                // Retrieve the item that was clicked on
                View v = (View) listViewStory.getAdapter().getView(
                        info.position, null, null);
                //Get memeber variable of inner class ViewHolder
                int storyId = ((StoryListAdapter.ViewHolder)v.getTag()).storyId;
                //Get item clicked
                StoryItem storyRemove = (StoryItem)adapter.getItem(info.position);
                //Delete item from db
                dbHelper.deleteFavouriteStory(storyId);
                //Remove item from list
                lstStory.remove(storyRemove);
                //Update listview after delete
                adapter.updateList(lstStory);
                Toast.makeText(getApplicationContext(),"Đã xóa khỏi danh mục yêu thích", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    }
