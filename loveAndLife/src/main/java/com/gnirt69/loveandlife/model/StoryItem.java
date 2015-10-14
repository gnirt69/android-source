package com.gnirt69.loveandlife.model;

public class StoryItem {

    private int storyId;
    private String storyName;

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }


    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }

    public StoryItem(int storyId, String storyName) {
        super();
        this.storyId = storyId;
        this.storyName = storyName;
    }

}
