package com.tzukru.dev.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tzukru.dev.R;
import com.tzukru.dev.model.TwitterListItem;
import com.tzukru.dev.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TwitterListAdapter extends ArrayAdapter<TwitterListItem> {

	private ArrayList<TwitterListItem> twitterList;
    private ImageLoader imgLoader;
    private Activity activity;
    private Map<Integer, View> listViewRowMap;

	public TwitterListAdapter(Context context, Activity activity, int textViewResourceId, ArrayList<TwitterListItem> twitterList){
        super(context, textViewResourceId, twitterList);
		this.twitterList = twitterList;
        imgLoader = new ImageLoader(context);
        this.activity = activity;
        listViewRowMap = new HashMap<Integer, View>();
	}

    static class BufferView {
        public ImageView userImage;
        public TextView username;
        public TextView datetime;
        public TextView text;
        public TextView twitImage;
        public TwitterListItem item;
    }

    @Override
    public View getView(int viewPosition, View convertView, ViewGroup parent) {
        final int position = viewPosition;
        BufferView viewSet;
        View row = convertView;

        View row_map = listViewRowMap.get(viewPosition);
        if (row_map != null) {
            row = row_map;
        } else {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.list_item, null, true);
            viewSet = new BufferView();

            viewSet.userImage = (ImageView) row.findViewById(R.id.user_image);
            viewSet.username = (TextView) row.findViewById(R.id.twit_username);
            viewSet.datetime = (TextView) row.findViewById(R.id.twit_datetime);
            viewSet.text = (TextView) row.findViewById(R.id.twit_text);
            viewSet.twitImage = (TextView) row.findViewById(R.id.twit_image);

            TwitterListItem twitterListItem =  getItem(position);
            viewSet.item = twitterListItem;

            if (twitterListItem.getUserImage() != null && viewSet.userImage.getDrawable() == null)
                imgLoader.DisplayImage(twitterListItem.getUserImage(), R.drawable.twitter_icon, viewSet.userImage, false);

            viewSet.username.setText(twitterListItem.getUsername());
            viewSet.datetime.setText(twitterListItem.getElapsed());
            viewSet.text.setText(twitterListItem.getText());

            if (twitterListItem.getTwitImage() != null && twitterListItem.getTwitImage().size() > 0 && !twitterListItem.isDone())
                viewSet.twitImage.setText(twitterListItem.getTwitImage().size() + "");

            row.setTag(viewSet);
            listViewRowMap.put(viewPosition, row);
        }

        BufferView tag = (BufferView) row.getTag();
        tag.datetime.setText(tag.item.getElapsed());
        row.setTag(tag);

        return row;
    }

    public void sort() {
//        int size = listViewRowMap.size();
//        for (int i = size - 2; i >= 0; i--)
//            listViewRowMap.put(i+1, listViewRowMap.get(i));
//
//        listViewRowMap.put(0, listViewRowMap.get(size));
    }

//    public void sort(){
//        Collections.sort(twitterList, new Comparator<TwitterListItem>() {
//            @Override
//            public int compare(TwitterListItem ml1, TwitterListItem ml2) {
//                long d1 = System.currentTimeMillis() - ml1.getCreatedAt();
//                long d2 = System.currentTimeMillis() - ml2.getCreatedAt();
//                return d1 > d2 ? 1 : d1 < d2 ? -1 : 0;
//            }
//        });
//    }

}
