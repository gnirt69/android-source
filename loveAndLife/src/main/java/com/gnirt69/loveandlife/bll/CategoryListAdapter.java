package com.gnirt69.loveandlife.bll;

import java.util.ArrayList;
import java.util.List;

import com.gnirt69.loveandlife.R;
import com.gnirt69.loveandlife.model.Category;
import com.gnirt69.loveandlife.model.Common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryListAdapter extends BaseAdapter {

    private Context context;
    private List<Category> lstCategory;

    public CategoryListAdapter(Context context, List<Category> lstCategory) {

        this.context = context;
        this.lstCategory = new ArrayList<Category>();
        this.lstCategory.addAll(lstCategory);
    }

    @Override
    public int getCount() {
        return lstCategory.size();
    }

    @Override
    public Object getItem(int position) {
        return lstCategory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderCategory viewHolder = null;
        Typeface typeFace= Typeface.createFromAsset(context.getAssets(), "font.ttf");
        if (null == convertView) {

            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.category_item, null);
            viewHolder = new ViewHolderCategory();
            viewHolder.categoryName = (TextView) convertView
                    .findViewById(R.id.txtCategoryName);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.imgCategory);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderCategory) convertView.getTag();
        }
        viewHolder.categoryName.setText(lstCategory.get(position).getCategoryName());
        viewHolder.categoryName.setTextColor(Color.parseColor(Common.configValueFontColor));
        viewHolder.categoryName.setTextColor(Color.WHITE);
        viewHolder.categoryName.setTypeface(typeFace);
        //Set image for list story
        viewHolder.img.setImageResource(lstCategory.get(position).getImgResourceCategoryId());
        viewHolder.categoryId = (lstCategory.get(position).getCategoryId());
        return convertView;
    }

    static class ViewHolderCategory {
        int categoryId;
        ImageView img;
        TextView categoryName;
    }

    public void updateList(List<Category> lstItem) {
        lstCategory.clear();
        lstCategory.addAll(lstItem);
        this.notifyDataSetChanged();
    }


}
