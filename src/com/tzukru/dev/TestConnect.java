/**
 * @author TzuKru
 */

package com.tzukru.dev;

import android.view.Window;
import android.view.WindowManager;
import com.tzukru.dev.TwitterApp.TwDialogListener;

import android.widget.CheckBox;
import android.widget.Toast;

import android.app.AlertDialog;
import android.app.Activity;

import android.view.View;
import android.view.View.OnClickListener;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.graphics.Color;

public class TestConnect extends Activity {
	private TwitterApp mTwitter;
	private CheckBox mTwitterBtn;

	private static final String twitter_consumer_key = "46XXK4pVKtA6bYq7fIcByJt94";
	private static final String twitter_secret_key = "TENAZ7fwwha5fpBZWUILkYG80G5cQ3rRgHUEnuLqhIveMj6pP8";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.main);

//        EditText tzuKuruText = (EditText) findViewById(R.id.tzu_kru_txt);
//        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/HelveticaNeueUltraLight.ttf");
//        tzuKuruText.setTypeface(tf);

		mTwitterBtn	= (CheckBox) findViewById(R.id.twitterCheck);

		mTwitterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onTwitterClick();
			}
		});
		
		mTwitter = new TwitterApp(this, twitter_consumer_key,twitter_secret_key);

        if (mTwitter.hasAccessToken()) {
            startActivity(new Intent(TestConnect.this, TestPost.class));
        }
		
		mTwitter.setListener(mTwLoginDialogListener);
		
		if (mTwitter.hasAccessToken()) {
			mTwitterBtn.setChecked(true);

//			String username = mTwitter.getUsername();
//			username		= (username.equals("")) ? "Unknown" : username;
//
//			mTwitterBtn.setText(" " + username);
//			mTwitterBtn.setTextColor(Color.WHITE);
		}
		
//		Button goBtn = (Button) findViewById(R.id.button1);
//
//		goBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(TestConnect.this, TestPost.class));
//			}
//		});
	}
	
	private void onTwitterClick() {
		if (mTwitter.hasAccessToken()) {
//			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//			builder.setMessage("Delete current Twitter connection?")
//			       .setCancelable(false)
//			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//			           public void onClick(DialogInterface dialog, int id) {
//			        	   mTwitter.resetAccessToken();
//
//			        	   mTwitterBtn.setChecked(false);
//			        	   mTwitterBtn.setText(" Login");
//			        	   mTwitterBtn.setTextColor(Color.GRAY);
//			           }
//			       })
//			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
//			           public void onClick(DialogInterface dialog, int id) {
//			                dialog.cancel();
//
//			                mTwitterBtn.setChecked(true);
//			           }
//			       });
//			final AlertDialog alert = builder.create();
//
//			alert.show();
            startActivity(new Intent(TestConnect.this, TestPost.class));
		} else {
			mTwitterBtn.setChecked(false);
			mTwitter.authorize();
		}
	}

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;

			Toast.makeText(TestConnect.this, "Connected to Twitter as " + username, Toast.LENGTH_LONG).show();

            startActivity(new Intent(TestConnect.this, TestPost.class));
		}
		
		@Override
		public void onError(String value) {
			mTwitterBtn.setChecked(false);
			
			Toast.makeText(TestConnect.this, "Twitter connection failed", Toast.LENGTH_LONG).show();
		}
	};
}