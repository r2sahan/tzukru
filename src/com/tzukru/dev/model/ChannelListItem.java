package com.tzukru.dev.model;

public class ChannelListItem {

    private String text;
    private String checked = "0";

    public ChannelListItem(String text, String checked) {
        this.text = text;
        this.checked = checked;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
