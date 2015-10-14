package com.gnirt69.loveandlife.bll;

import java.util.ArrayList;
import java.util.List;

import com.gnirt69.loveandlife.R;
import com.gnirt69.loveandlife.model.Common;
import com.gnirt69.loveandlife.model.StoryItem;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StoryListAdapter extends BaseAdapter {

    private Context context;
    private List<StoryItem> lstStory;
    private int imgId;

    public StoryListAdapter(Context context, List<StoryItem> lstStory, int imgId) {

        this.context = context;
        this.lstStory = new ArrayList<StoryItem>();
        this.lstStory.addAll(lstStory);
        this.imgId = imgId;
    }

    @Override
    public int getCount() {
        return lstStory.size();
    }

    @Override
    public Object getItem(int position) {
        return lstStory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Typeface typeFace= Typeface.createFromAsset(context.getAssets(), "font2.ttf");
        if (null == convertView) {

            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.story_item, null);
            viewHolder = new ViewHolder();
            viewHolder.storyName = (TextView) convertView
                    .findViewById(R.id.txtStoryName);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.imgStory);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.storyName.setText(lstStory.get(position).getStoryName());
        viewHolder.storyName.setTextColor(Color.parseColor(Common.configValueFontColor));
        viewHolder.storyName.setTypeface(typeFace);
        //Set image for list story
        viewHolder.img.setImageResource(imgId);
        viewHolder.storyId = lstStory.get(position).getStoryId();
        return convertView;
    }

    public static class ViewHolder {
        public int storyId;
        ImageView img;
        TextView storyName;
    }

    public void updateList(List<StoryItem> lstItem) {
        lstStory.clear();
        lstStory.addAll(lstItem);
        this.notifyDataSetChanged();
    }


}
