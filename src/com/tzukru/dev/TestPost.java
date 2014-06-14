/**
 * @author TzuKru
 */

package com.tzukru.dev;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Window;
import android.view.WindowManager;
import com.tzukru.dev.TwitterApp.TwDialogListener;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

public class TestPost extends Activity {
	private TwitterApp mTwitter;
	private CheckBox mTwitterBtn;
	private String username = "";
	private boolean postToTwitter = false;
	
	private static final String twitter_consumer_key = "46XXK4pVKtA6bYq7fIcByJt94";
	private static final String twitter_secret_key = "TENAZ7fwwha5fpBZWUILkYG80G5cQ3rRgHUEnuLqhIveMj6pP8";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.post);
		
		Button postBtn = (Button) findViewById(R.id.button1);
		final EditText reviewEdit = (EditText) findViewById(R.id.revieew);
		
		postBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String review = reviewEdit.getText().toString();
				
				if (review.equals("")) return;
				
				postReview(review);
				
				postToTwitter(review);
			}
		});

		mTwitter = new TwitterApp(this, twitter_consumer_key,twitter_secret_key);
		
		mTwitter.setListener(mTwLoginDialogListener);

		mTwitterBtn	= (CheckBox) findViewById(R.id.twitterCheck);

		mTwitterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTwitter.hasAccessToken()) {
                    onTwitterClick();
				} else {
					finish();
				}
			}
		});
		
		if (mTwitter.hasAccessToken()) {
			username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;
			
			mTwitterBtn.setText(" " + username);
		}
	}
	
	private void postReview(String review) {
		//post to server
		Toast.makeText(this, "Review posted", Toast.LENGTH_SHORT).show();
	}

    private void onTwitterClick() {
        if (mTwitter.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Do you want to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mTwitter.resetAccessToken();
                            mTwitterBtn.setChecked(false);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            mTwitterBtn.setChecked(true);
                        }
                    });
            final AlertDialog alert = builder.create();

            alert.show();
        } else {
            mTwitterBtn.setChecked(false);

            mTwitter.authorize();
        }
    }
	
	private void postToTwitter(final String review) {
        if (mTwitter.hasAccessToken()) {
            new Thread() {
                @Override
                public void run() {
                    int what = 0;

                    try {
                        mTwitter.updateStatus(review);
                    } catch (Exception e) {
                        what = 1;
                    }

                    mHandler.sendMessage(mHandler.obtainMessage(what));
                }
            }.start();
        }
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String text = (msg.what == 0) ? "Posted to Twitter" : "Post to Twitter failed";
			
			Toast.makeText(TestPost.this, text, Toast.LENGTH_SHORT).show();
		}
	};

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			username 	= mTwitter.getUsername();
			username	= (username.equals("")) ? "No Name" : username;
		
			mTwitterBtn.setText(" " + username);
			mTwitterBtn.setChecked(true);
			
			postToTwitter = true;
			
			Toast.makeText(TestPost.this, "Connected to Twitter as " + username, Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void onError(String value) {
			mTwitterBtn.setChecked(false);
			
			Toast.makeText(TestPost.this, "Twitter connection failed", Toast.LENGTH_LONG).show();
		}
	};
}