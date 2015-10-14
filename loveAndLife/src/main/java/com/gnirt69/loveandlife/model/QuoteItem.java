package com.gnirt69.loveandlife.model;

/**
 * Created by NgocTri on 9/12/2015.
 */
public class QuoteItem {
    private int id;
    private String quoteContent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuoteContent() {
        return quoteContent;
    }

    public void setQuoteContent(String quoteContent) {
        this.quoteContent = quoteContent;
    }

    public QuoteItem(int id, String quoteContent) {
        this.id = id;
        this.quoteContent = quoteContent;
    }
}
