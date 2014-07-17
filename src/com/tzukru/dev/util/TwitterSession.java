/**
 * @author TzuKru
 */

package com.tzukru.dev.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

import twitter4j.auth.AccessToken;

public class TwitterSession {
	private SharedPreferences sharedPref;
	private Editor editor;
	
	private static final String TWEET_AUTH_KEY = "auth_key";
	private static final String TWEET_AUTH_SECRET_KEY = "auth_secret_key";
	private static final String TWEET_USER_NAME = "user_name";
    private static final String TWEET_USER_ID = "user_id";
    private static final String TWEET_SCREEN_NAME = "screen_name";
	private static final String SHARED = "Twitter_Preferences";

    public TwitterSession(Context context){
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

	public void storeAccessToken(AccessToken accessToken, String username, String id, String screenName) {
		editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
		editor.putString(TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
		editor.putString(TWEET_USER_NAME, username);
        editor.putString(TWEET_USER_ID, id);
        editor.putString(TWEET_SCREEN_NAME, screenName);
		
		editor.commit();
	}
	
	public void resetAccessToken() {
		editor.putString(TWEET_AUTH_KEY, null);
		editor.putString(TWEET_AUTH_SECRET_KEY, null);
		editor.putString(TWEET_USER_NAME, null);
        editor.putString(TWEET_USER_ID, null);
        editor.putString(TWEET_SCREEN_NAME, null);
		
		editor.commit();
	}
	
	public String getUsername() {
		return sharedPref.getString(TWEET_USER_NAME, "");
	}

    public String getUserId() {
        return sharedPref.getString(TWEET_USER_ID, "");
    }

    public String getScreenName() {
        return sharedPref.getString(TWEET_SCREEN_NAME, "");
    }
	
	public AccessToken getAccessToken() {
		String token 		= sharedPref.getString(TWEET_AUTH_KEY, null);
		String tokenSecret 	= sharedPref.getString(TWEET_AUTH_SECRET_KEY, null);
		
		if (token != null && tokenSecret != null) 
			return new AccessToken(token, tokenSecret);
		else
			return null;
	}
}