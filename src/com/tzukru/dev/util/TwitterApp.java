/**
 * @author TzuKru
 */

package com.tzukru.dev.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import twitter4j.*;
import twitter4j.auth.AccessToken;

import android.os.Handler;
import android.os.Message;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import twitter4j.conf.ConfigurationBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TwitterApp {
	private Twitter mTwitter;
	private TwitterSession mSession;
	private AccessToken mAccessToken;
	private CommonsHttpOAuthConsumer mHttpOauthConsumer;
	private OAuthProvider mHttpOauthprovider;
	private ProgressDialog mProgressDlg;
	private TwDialogListener mListener;
	private Context context;

    private User user;
    private TwitterStream twitterStream;

    private static final String mConsumerKey = "46XXK4pVKtA6bYq7fIcByJt94";
    private static final String mSecretKey = "TENAZ7fwwha5fpBZWUILkYG80G5cQ3rRgHUEnuLqhIveMj6pP8";
    private static final String mAccessTokenKey = "2485232768-lAnD0eUfCWVD5ThIrYDlMCCKSlYygfV8rQQ76VB";
    private static final String mAccessTokenSecretKey = "6qDvVrxtRCgfaXAyijQVxLHwRqUC4r73oMAhyuEcv0sMq";
    private static final String requestToken = "https://api.twitter.com/oauth/request_token";
    private static final String accessToken = "https://api.twitter.com/oauth/access_token";
    private static final String authorizationToken = "https://api.twitter.com/oauth/authorize";
	private static final String TAG = "TzuKru";

    public static final String CALLBACK_URL = "twitterapp://connect";

    public TwitterApp(Context context) {
		this.context	= context;
		
		mTwitter 		= new TwitterFactory().getInstance();
		mSession		= new TwitterSession(context);
		mProgressDlg	= new ProgressDialog(context);
		
		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(mConsumerKey, mSecretKey);
		mHttpOauthprovider = new DefaultOAuthProvider(requestToken, accessToken, authorizationToken);
		
		mAccessToken	= mSession.getAccessToken();

		configureToken();
	}

	public void setListener(TwDialogListener listener) {
		mListener = listener;
	}
	
	@SuppressWarnings("deprecation")
	private void configureToken() {
		if (mAccessToken != null) {
            try {
                mTwitter.setOAuthConsumer(mConsumerKey, mSecretKey);
                mTwitter.setOAuthAccessToken(mAccessToken);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
		}
	}
	
	public boolean hasAccessToken() {
        return (mSession.getAccessToken() != null);
	}
	
	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}
	
	public String getUsername() {
		return mSession.getUsername();
	}

    public String getScreenName() {
        return mSession.getScreenName();
    }

    public String getProfileImage(){
        return getUser().getBiggerProfileImageURL();
    }

    public User getUser(){
        if (user == null) {
            try {
                user = mTwitter.showUser(mSession.getScreenName());
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
	
	public void updateStatus(String status) throws Exception {
		mTwitter.updateStatus(status);
	}
	
	public void authorize() {
		mProgressDlg.setMessage("Initializing ...");
		mProgressDlg.show();
		
		new Thread() {
			@Override
			public void run() {
				String authUrl = "";
				int what = 1;
				
				try {
					authUrl = mHttpOauthprovider.retrieveRequestToken(mHttpOauthConsumer, CALLBACK_URL);	
					
					what = 0;
					
					Log.d(TAG, "Request token url " + authUrl);
				} catch (Exception e) {
					Log.d(TAG, "Failed to get request token");
					
					e.printStackTrace();
				}
				
				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0, authUrl));
			}
		}.start();
	}
	
	public void processToken(String callbackUrl)  {
		mProgressDlg.setMessage("Finalizing ...");
		mProgressDlg.show();
		
		final String verifier = getVerifier(callbackUrl);

		new Thread() {
			@Override
			public void run() {
				int what = 1;
				
				try {
					mHttpOauthprovider.retrieveAccessToken(mHttpOauthConsumer, verifier);
		
					mAccessToken = new AccessToken(mHttpOauthConsumer.getToken(), mHttpOauthConsumer.getTokenSecret());
				
					configureToken();
				
					user = mTwitter.verifyCredentials();
				
			        mSession.storeAccessToken(mAccessToken, user.getName(), user.getId() + "", user.getScreenName());
			        
			        what = 0;
				} catch (Exception e){
					Log.d(TAG, "Error getting access token");
					
					e.printStackTrace();
				}
				
				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}
	
	private String getVerifier(String callbackUrl) {
		String verifier	 = "";
		
		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");
			
			URL url 		= new URL(callbackUrl);
			String query 	= url.getQuery();
		
			String array[]	= query.split("&");

			for (String parameter : array) {
	             String v[] = parameter.split("=");

                if (URLDecoder.decode(v[0], "UTF-8").equals(oauth.signpost.OAuth.OAUTH_VERIFIER)) {
                    verifier = URLDecoder.decode(v[1], "UTF-8");
                    break;
                }
            }

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
		
		return verifier;
	}
	
	private void showLoginDialog(String url) {
		final TwDialogListener listener = new TwDialogListener() {
			@Override
			public void onComplete(String value) {
				processToken(value);
			}
			
			@Override
			public void onError(String value) {
				mListener.onError("Failed opening authorization page");
			}
		};
		
		new TwitterDialog(context, url, listener).show();
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgressDlg.dismiss();
			
			if (msg.what == 1) {
				if (msg.arg1 == 1)
					mListener.onError("Error getting request token");
				else
					mListener.onError("Error getting access token");
			} else {
				if (msg.arg1 == 1)
					showLoginDialog((String) msg.obj);
				else
					mListener.onComplete("");
			}
		}
	};
	
	public interface TwDialogListener {
		public void onComplete(String value);		
		
		public void onError(String value);
	}

    public ResponseList<Status> getTwitterList() {
        try {
//            return mTwitter.getUserTimeline(System.currentTimeMillis() - (15 * 60 * 1000));
            return mTwitter.getHomeTimeline(new Paging(1));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Status> getHashResult(String value, int count) {
        List<Status> list = new ArrayList<Status>();
        try {
            Query query = new Query(value);
            query.setCount(count);
//            query.setGeoCode(new GeoLocation(39.9033765,32.767873), 10, Query.Unit.km);
//            query.setSince("2014-07-10");
            QueryResult result = mTwitter.search(query);
            while (result.hasNext()) {
                list.addAll(result.getTweets());
                break;
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void stopStream() {
        if (twitterStream != null) {
            twitterStream.cleanUp();
            twitterStream.clearListeners();
            twitterStream.shutdown();
        }
    }

    private TwitterStream getTwitterStream(){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey(mConsumerKey);
        cb.setOAuthConsumerSecret(mSecretKey);
        cb.setOAuthAccessToken(mAccessTokenKey);
        cb.setOAuthAccessTokenSecret(mAccessTokenSecretKey);

        return new TwitterStreamFactory(cb.build()).getInstance();
    }

    public void startStream(String[] result, StatusListener listener){
        twitterStream = getTwitterStream();

        FilterQuery fq = new FilterQuery();
        fq.track(result);

        twitterStream.addListener(listener);
        twitterStream.filter(fq);
    }

}