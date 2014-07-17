/**
 * @author TzuKru
 */

package com.tzukru.dev;

import android.graphics.Typeface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.tzukru.dev.util.TwitterApp;

import android.widget.CheckBox;
import android.widget.Toast;

import android.app.Activity;

import android.view.View;
import android.view.View.OnClickListener;

import android.content.Intent;

import android.os.Bundle;
import com.tzukru.dev.util.TwitterConstants;

public class TwitterConnect extends Activity {
	private TwitterApp mTwitter;
	private CheckBox mTwitterBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.main);

        TextView tzuKuruText = (TextView) findViewById(R.id.tzu_kru_txt);
        tzuKuruText.setTypeface(TwitterConstants.getHelveticaTF(this.getAssets()));

		mTwitterBtn	= (CheckBox) findViewById(R.id.twitterCheck);

		mTwitterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onTwitterClick();
			}
		});
		
		mTwitter = new TwitterApp(this);

        if (mTwitter.hasAccessToken()) {
            startActivity(new Intent(TwitterConnect.this, MainActivity.class));
        }
		
		mTwitter.setListener(mTwLoginDialogListener);
		
		if (mTwitter.hasAccessToken()) {
			mTwitterBtn.setChecked(true);
		}
	}
	
	private void onTwitterClick() {
		if (mTwitter.hasAccessToken()) {
            startActivity(new Intent(TwitterConnect.this, MainActivity.class));
		} else {
			mTwitterBtn.setChecked(false);
			mTwitter.authorize();
		}
	}

	private final TwitterApp.TwDialogListener mTwLoginDialogListener = new TwitterApp.TwDialogListener() {
		@Override
		public void onComplete(String value) {
			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;

			Toast.makeText(TwitterConnect.this, "Connected to Twitter as " + username, Toast.LENGTH_LONG).show();

            startActivity(new Intent(TwitterConnect.this, MainActivity.class));
		}
		
		@Override
		public void onError(String value) {
			mTwitterBtn.setChecked(false);
			
			Toast.makeText(TwitterConnect.this, "Twitter connection failed", Toast.LENGTH_LONG).show();
		}
	};
}