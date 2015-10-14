package com.gnirt69.loveandlife.bll;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnirt69.loveandlife.R;
import com.gnirt69.loveandlife.model.Common;
import com.gnirt69.loveandlife.model.QuoteItem;
import com.gnirt69.loveandlife.model.StoryItem;

import java.util.List;

/**
 * Created by NgocTri on 9/12/2015.
 */
public class QuoteListAdapter extends BaseAdapter {
    private Context context;
    private List<QuoteItem> lstQuote;

    public QuoteListAdapter(Context context, List<QuoteItem> lstQuote) {
        this.context = context;
        this.lstQuote = lstQuote;
    }

    @Override
    public int getCount() {
        return lstQuote.size();
    }

    @Override
    public Object getItem(int position) {
        return lstQuote.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuoteListAdapter.ViewHolderQuote viewHolder = null;
        if (null == convertView) {

            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.quote_item, null);
            viewHolder = new QuoteListAdapter.ViewHolderQuote();
            viewHolder.quoteContent = (TextView) convertView
                    .findViewById(R.id.txtQuoteContent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (QuoteListAdapter.ViewHolderQuote) convertView.getTag();
        }
        viewHolder.quoteId = (lstQuote.get(position).getId());
        viewHolder.quoteContent.setText(String.valueOf(position+1) + ". "+lstQuote.get(position).getQuoteContent());
        return convertView;
    }
    public static class ViewHolderQuote {
        public int quoteId;
        public TextView quoteContent;
    }

    public void updateList(List<QuoteItem> lstUpdate) {
        lstQuote.clear();
        lstQuote.addAll(lstUpdate);
        this.notifyDataSetChanged();
    }
}
