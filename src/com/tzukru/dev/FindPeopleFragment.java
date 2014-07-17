package com.tzukru.dev;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.tzukru.dev.adapter.TwitterListAdapter;
import com.tzukru.dev.model.TwitterListItem;
import com.tzukru.dev.util.ChannelManager;
import com.tzukru.dev.util.ImageLoader;
import com.tzukru.dev.util.TwitterApp;
import twitter4j.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindPeopleFragment extends Fragment {

    private final static String TAG = "TzuKru";

    private TwitterApp mTwitter;
    private ProgressDialog mProgressDlg;
    private ListView listView;
    private TextView txtLabel;
    private Context context;

    private TwitterListAdapter adapter;
    private ArrayList<TwitterListItem> twitList = new ArrayList<TwitterListItem>();

    private ChannelManager channelManager;
    private ImageLoader imgLoader;

	public FindPeopleFragment(TwitterApp mTwitter){
        this.mTwitter = mTwitter;
    }

	@Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_find_people, container, false);

        context = rootView.getContext();

        channelManager = new ChannelManager(rootView.getContext());
        imgLoader = new ImageLoader(rootView.getContext());

        txtLabel = (TextView) rootView.findViewById(R.id.txtLabel);
        final ImageView imageViewer = (ImageView) rootView.findViewById(R.id.image_viewer);

        listView = (ListView) rootView.findViewById(R.id.list1);
        listView.setCacheColorHint(Color.WHITE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (twitList.get(position).getTwitImage() != null && twitList.get(position).getTwitImage().size() > 0) {
                    imageViewer.setVisibility(View.VISIBLE);
                    for (String twitUrl : twitList.get(position).getTwitImage()) {
                        imgLoader.DisplayImage(twitUrl, -1, imageViewer);
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

        List<String> chList = channelManager.getChannels();

        if (chList.size() > 0)
            new ListTask().execute(chList.toArray(new String[chList.size()]));

        Log.i(TAG, "FindPeopleFragment started.");
    }

    @Override
    public void onStop(){
        super.onStop();
        mTwitter.stopStream();
        Log.i(TAG, "FindPeopleFragment stopped.");
    }

    private void updateAdapter(final Status status){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<String> urls = new ArrayList<String>();
                MediaEntity[] mediaEntities = status.getMediaEntities();
                if (mediaEntities != null)
                    for (MediaEntity mediaEntity : mediaEntities) {
                        if (mediaEntity.getType().equals("photo"))
                            urls.add(mediaEntity.getMediaURL());
                    }
                twitList.add(new TwitterListItem(
                        status.getUser().getProfileImageURL(),
                        status.getUser().getName(),
                        status.getCreatedAt().getTime(),
                        status.getText(),
                        urls));
                txtLabel.setText(twitList.size() + "");
                adapter.notifyDataSetInvalidated();
            }
        });
        Log.i(TAG, "List adapter updated.");
    }

    private class ListTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            mProgressDlg = new ProgressDialog(getView().getContext());
            mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDlg.setMessage("Loading...");
            mProgressDlg.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            for (String param : params) {
                List<twitter4j.Status> twitterList = mTwitter.getHashResult(param, 10);
                if (twitterList != null) for (twitter4j.Status status : twitterList) {
                    List<String> urls = new ArrayList<String>();
                    MediaEntity[] mediaEntities = status.getMediaEntities();
                    if (mediaEntities != null)
                        for (MediaEntity mediaEntity : mediaEntities) {
                            if (mediaEntity.getType().equals("photo"))
                                urls.add(mediaEntity.getMediaURL());
                        }
                    twitList.add(new TwitterListItem(
                            status.getUser().getProfileImageURL(),
                            status.getUser().getName(),
                            status.getCreatedAt().getTime(),
                            status.getText(),
                            urls));
                }
            }
            return params;
        }

        @Override
        protected void onPostExecute(String[] result) {
            Collections.sort(twitList, new Comparator<TwitterListItem>() {
                @Override
                public int compare(TwitterListItem ml1, TwitterListItem ml2) {
                    long d1 = System.currentTimeMillis() - ml1.getCreatedAt();
                    long d2 = System.currentTimeMillis() - ml2.getCreatedAt();
                    return d1 > d2 ? -1 : d1 < d2 ? 1 : 0;
                }
            });

            adapter = new TwitterListAdapter(context, getActivity(), R.layout.list_item, twitList);
            listView.setAdapter(adapter);
            txtLabel.setText(twitList.size() + "");

            mProgressDlg.hide();

            mTwitter.startStream(result, new QueryListener());
        }
    }

    private class QueryListener implements StatusListener {

        @Override
        public void onStatus(Status status) {
            updateAdapter(status);
            Log.i(TAG, "QueryListener::onStatus");
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            Log.i(TAG, "QueryListener::onDeletionNotice");
        }

        @Override
        public void onTrackLimitationNotice(int i) {
            Log.i(TAG, "QueryListener::onTrackLimitationNotice");
        }

        @Override
        public void onScrubGeo(long l1, long l2) {
            Log.i(TAG, "QueryListener::onScrubGeo");
        }

        @Override
        public void onStallWarning(StallWarning stallWarning) {
            Log.i(TAG, "QueryListener::onStallWarning");
        }

        @Override
        public void onException(Exception e) {
            Log.e(TAG, "QueryListener: " + e.getMessage());
        }
    }

}
