package com.tzukru.dev.model;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ramazansahan on 07/07/14.
 */
public class TwitterListItem {

    private String username;
    private long createdAt;
    private String text;
    private List<String> twitImage;
    private String userImage;
    private boolean done = false;

    public TwitterListItem(String userImage, String username, long createdAt, String text, List<String> twitImage) {
        this.userImage = userImage;
        this.username = username;
        this.createdAt = createdAt;
        this.text = text;
        this.twitImage = twitImage;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUsername() {
        return username;
    }

    public long getCreatedAt() { return createdAt; }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getText() {
        return text;
    }

    public List<String> getTwitImage() {
        return twitImage;
    }

    public String getElapsed() {
        long duration = System.currentTimeMillis() - createdAt;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        if (days > 0) {
            return days + " d";
        }
        if (hours > 0) {
            return hours + " h";
        }
        if (minutes > 0) {
            return minutes + " m";
        }

        return seconds + " s";
    }

}
