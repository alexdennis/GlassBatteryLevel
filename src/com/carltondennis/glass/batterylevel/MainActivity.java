package com.carltondennis.glass.batterylevel;

import android.app.Activity;
import android.view.View;
import android.os.BatteryManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.speech.tts.TextToSpeech;

import com.google.android.glass.app.Card;

import java.text.DecimalFormat;

public class MainActivity extends Activity {
	private boolean mSpoken = false;
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			Resources res 	   = getResources();
			int level          = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale          = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			double levelDouble = Integer.valueOf(level).doubleValue();
			double scaleDouble = Integer.valueOf(scale).doubleValue();
			double batteryFrac = levelDouble / scaleDouble;
			DecimalFormat df   = new DecimalFormat("#%");
			
			String levelText   = 
					res.getString(R.string.battery_level_label) 
					+ " " 
					+ df.format(batteryFrac);
			
			// Speak it.
			if (!mSpoken) {
				// This is to avoid repetition
				mSpeech.speak(levelText, TextToSpeech.QUEUE_FLUSH, null);
				mSpoken = true;
			}
			
			// Display it.
			Card card = new Card(c);
			card.setText(levelText);
			View cardView = card.toView();
			setContentView(cardView);
		}
	};
	
	private TextToSpeech mSpeech;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Do nothing.
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
