package com.carltondennis.glass.batterylevel;

import android.app.Activity;
import android.view.View;
import android.os.BatteryManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.android.glass.app.Card;


public class MainActivity extends Activity {
	private boolean mSpoken = false;
	private boolean mTTSReady = false;
	private boolean mBatteryInfoRecorded = false;
	private String mLevelText;
	
	public static String TAG = "Battery Info";
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			int level          = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale          = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			double levelDouble = Integer.valueOf(level).doubleValue();
			double scaleDouble = Integer.valueOf(scale).doubleValue();
			double batteryPercentage = (levelDouble / scaleDouble) * 100;
			
			mLevelText = getResources().getString(
					R.string.battery_level_label, batteryPercentage
			);
			
			mBatteryInfoRecorded = true;
			speakIfPossible();
			
			// Display it.
			Card card = new Card(c);
			card.setText(mLevelText);
			View cardView = card.getView();
			setContentView(cardView);
		}
	};
	
	private void speakIfPossible()
	{
		// Speak it if all the conditions are met.
		if (mBatteryInfoRecorded && mTTSReady && !mSpoken) {
			Log.d(TAG, "Speak!");
			mSpeech.speak(mLevelText, TextToSpeech.QUEUE_FLUSH, null);
			
			// Speak it once and only once.
			mSpoken = true;
		}
	}
	
	private TextToSpeech mSpeech;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.d(TAG, "Status: " + status);
            	if (status == TextToSpeech.SUCCESS) {
                    mTTSReady = true;
                    speakIfPossible();
                } else if (status == TextToSpeech.ERROR) {
                	Log.d(TAG, "Unable to setup TTS");
                }
            }
        });
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(mBatInfoReceiver, new IntentFilter(
		        Intent.ACTION_BATTERY_CHANGED));		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mBatInfoReceiver);
	}
	
	@Override
    public void onDestroy() {
		mSpeech.shutdown();
        mSpeech = null;
        
        super.onDestroy();
	}
}
