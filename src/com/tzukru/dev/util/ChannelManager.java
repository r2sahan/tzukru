package com.tzukru.dev.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.tzukru.dev.model.ChannelListItem;

import java.util.ArrayList;
import java.util.List;

public class ChannelManager {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor edit;

    public ChannelManager(Context context) {
        this.sharedPref = context.getSharedPreferences(TwitterConstants.TZUKRU, Context.MODE_PRIVATE);
        this.edit = sharedPref.edit();
    }

    public List<ChannelListItem> getChannelList(){
        List<ChannelListItem> channelList = new ArrayList<ChannelListItem>();
        String channel = sharedPref.getString(TwitterConstants.CHANNEL, "");
        if (channel.length() > 0) {
            String[] channels = channel.split("-");
            if (channels != null) for (String ch : channels) {
                String[] s = ch.split(":");
                channelList.add(Integer.valueOf(s[0]), new ChannelListItem(s[1],s[2]));
            }
        }
        return channelList;
    }

    public void addChannel(String size, String review){
        String channel = sharedPref.getString(TwitterConstants.CHANNEL, "");
        channel += size + ":" + review + ":0-";
        edit.putString(TwitterConstants.CHANNEL,channel);
        edit.commit();
    }

    public void checkChannel(int position, boolean checked){
        String newChannel = "";
        String channel = sharedPref.getString(TwitterConstants.CHANNEL, "");
        if (channel.length() > 0) {
            String[] channels = channel.split("-");
            if (channels != null) for (String ch : channels) {
                String[] s = ch.split(":");
                if (s[0].equals(position + ""))
                    s[2] = checked ? "1" : "0";
                newChannel += s[0] + ":" + s[1] + ":" + s[2] + "-";
            }
        }
        edit.putString(TwitterConstants.CHANNEL, newChannel);
        edit.commit();
    }

    public void deleteChannel(int position){
        String newChannel = "";
        String channel = sharedPref.getString(TwitterConstants.CHANNEL, "");
        if (channel.length() > 0) {
            String[] channels = channel.split("-");
            if (channels != null) {
                boolean after = false;
                for (String ch : channels) {
                    String[] s = ch.split(":");
                    if (s[0].equals(position + "")) {
                        after = true;
                    } else if (after) {
                        newChannel += (Integer.valueOf(s[0]) - 1) + ":" + s[1] + ":" + s[2] + "-";
                    } else {
                        newChannel += ch + "-";
                    }
                }
            }
        }
        edit.putString(TwitterConstants.CHANNEL, newChannel);
        edit.commit();
    }

    public List<String> getChannels(){
        List<String> channels = new ArrayList<String>();
        String channel = sharedPref.getString(TwitterConstants.CHANNEL, "");
        if (channel.length() > 0) {
            String[] chs = channel.split("-");
            if (chs != null) for (String ch : chs) {
                String[] s = ch.split(":");
                if (s[2].equals("1"))
                    channels.add("#"+s[1]);
            }
        }
        return channels;
    }

    public int getActiveChannelCount() {
        String channel = sharedPref.getString(TwitterConstants.CHANNEL, "");
        if (channel.length() > 0) {
            int i = 0;
            String[] chs = channel.split("-");
            if (chs != null) for (String ch : chs) {
                String[] s = ch.split(":");
                if (s[2].equals("1"))
                    i++;
            }
            return i;
        }
        return 0;
    }

    public int getChannelCount(){
        String channel = sharedPref.getString(TwitterConstants.CHANNEL, "");
        if (channel.length() > 0) {
            int i = 0;
            String[] chs = channel.split("-");
            return chs.length;
        }
        return 0;
    }

    public String getCount(){
        String channel = sharedPref.getString(TwitterConstants.CHANNEL, "");
        if (channel.length() > 0) {
            int i = 0;
            String[] chs = channel.split("-");
            if (chs != null) for (String ch : chs) {
                String[] s = ch.split(":");
                if (s[2].equals("1"))
                    i++;
            }
            return chs.length  + " (" + i + ")";
        }

        return "0";
    }
}
