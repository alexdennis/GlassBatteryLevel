package com.carltondennis.glass.batterylevel;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.BatteryManager;

import com.google.android.glass.app.Card;

public class MainActivity extends Activity {
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context c, Intent i) {
	    	int level 		 = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale 		 = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);			
			float batteryPct = (level / (float)scale) * 100;
	        
			Card card = new Card(c);
			card.setText("Battery Level: " + Float.toString(batteryPct) + "%");
			View cardView = card.toView();
			setContentView(cardView);
	    }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		registerReceiver(mBatInfoReceiver, new IntentFilter(
	            Intent.ACTION_BATTERY_CHANGED));		
	}
}
