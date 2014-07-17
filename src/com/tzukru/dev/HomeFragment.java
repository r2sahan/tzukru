package com.tzukru.dev;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tzukru.dev.adapter.TwitterListAdapter;
import com.tzukru.dev.model.TextProgressBar;
import com.tzukru.dev.model.TwitterListItem;
import com.tzukru.dev.util.ChannelManager;
import com.tzukru.dev.util.ImageLoader;
import com.tzukru.dev.util.TwitterApp;
import twitter4j.MediaEntity;
import twitter4j.User;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class HomeFragment extends Fragment {
	
	public HomeFragment(){}

    private TextView txtLabel;
    private TextView profileName;
    private TextView screenName;
    private ImageView profileImage;

    private TextView wifiTitle;
    private TextProgressBar wifiBar;
    private TextView channelTitle;
    private TextProgressBar channelBar;

    private Context context;
    private TwitterApp mTwitter;
    private ImageLoader imageLoader;
    private ChannelManager channelManager;

    private ArrayList<TwitterListItem> specList = new ArrayList<TwitterListItem>();
    private ListView listView;
    private ArrayAdapter<TwitterListItem> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        context = rootView.getContext();
        imageLoader = new ImageLoader(context);
        mTwitter= new TwitterApp(context);
        channelManager = new ChannelManager(context);

        txtLabel = (TextView) rootView.findViewById(R.id.txt_label);
        profileName = (TextView) rootView.findViewById(R.id.profile_name);
        screenName = (TextView) rootView.findViewById(R.id.screen_name);
        profileImage = (ImageView) rootView.findViewById(R.id.profile_image);

        wifiTitle = (TextView) rootView.findViewById(R.id.wifi_title);
        wifiBar = (TextProgressBar) rootView.findViewById(R.id.wifi_bar);

        channelTitle = (TextView) rootView.findViewById(R.id.channel_title);
        channelBar = (TextProgressBar) rootView.findViewById(R.id.channel_bar);

        listView = (ListView) rootView.findViewById(R.id.device_list);

        profileName.setText(mTwitter.getUsername());
        screenName.setText("@" + mTwitter.getScreenName());

        TextProgressBar progressXp = (TextProgressBar) rootView.findViewById(R.id.progress_xp);
        progressXp.setMax(100);
        progressXp.setProgress(24);
        progressXp.setSecondaryProgress(55);
        progressXp.setText("XP: " + 24 + "/" + 55 + "/" + 100);
        progressXp.setProgressDrawable(getResources().getDrawable(R.drawable.goldprogress));

        txtLabel.setText("Features");

        channelTitle.setText("Channels");
        channelBar.setText(channelManager.getActiveChannelCount() + "/" + channelManager.getChannelCount());
        channelBar.setMax(channelManager.getChannelCount());
        channelBar.setProgress(channelManager.getActiveChannelCount());


        final ImageView imageViewer = (ImageView) rootView.findViewById(R.id.image_viewer);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (specList.get(position).getTwitImage() != null && specList.get(position).getTwitImage().size() > 0) {
                    imageViewer.setVisibility(View.VISIBLE);
                    for (String twitUrl : specList.get(position).getTwitImage()) {
                        imageLoader.DisplayImage(twitUrl, -1, imageViewer);
                    }
                }
            }
        });

        imageViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewer.setImageDrawable(null);
                imageViewer.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        context.getApplicationContext().registerReceiver(new ConnectionReceiver(), new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        new ProfileTask().execute();
    }

    private class ProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            User user = mTwitter.getUser();
            if (user == null)
                cancel(true);

            List<twitter4j.Status> twitterList = mTwitter.getTwitterList();
            if (twitterList != null) for (twitter4j.Status status : twitterList) {
                List<String> urls = new ArrayList<String>();
                MediaEntity[] mediaEntities = status.getMediaEntities();
                if (mediaEntities != null)
                    for (MediaEntity mediaEntity : mediaEntities) {
                        if (mediaEntity.getType().equals("photo"))
                            urls.add(mediaEntity.getMediaURL());
                    }
                specList.add(new TwitterListItem(
                        status.getUser().getProfileImageURL(),
                        status.getUser().getName(),
                        status.getCreatedAt().getTime(),
                        status.getText(),
                        urls));
            }
            return mTwitter.getProfileImage();
        }

        @Override
        protected void onPostExecute(String result) {
            imageLoader.DisplayImage(result, -1, profileImage);

            adapter = new TwitterListAdapter(context, getActivity(), R.layout.list_item, specList);
            listView.setAdapter(adapter);
        }

        @Override
        protected void onCancelled(String result){
             getActivity().finish();
        }

    }

    class ConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            String action = intent.getAction();
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifiInfo != null && wifiInfo.isConnected()) {
                    WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wifiManager.getConnectionInfo();
                    int rssi = info.getRssi();
                    int quality;
                    if(rssi <= -100)
                        quality = 0;
                    else if(rssi >= -50)
                        quality = 100;
                    else
                        quality = 2 * (rssi + 100);

                    wifiTitle.setText("Wifi quality");
                    wifiBar.setText(quality + "%");
                    wifiBar.setMax(100);
                    wifiBar.setProgress(quality);
                }
            }
        }
    }

}
