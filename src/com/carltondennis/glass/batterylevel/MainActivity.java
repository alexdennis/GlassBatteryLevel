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

import java.text.DecimalFormat;

public class MainActivity extends Activity {
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			int level          = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale          = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			double levelDouble = Integer.valueOf(level).doubleValue();
			double scaleDouble = Integer.valueOf(scale).doubleValue();
			double batteryFrac = levelDouble / scaleDouble;
			DecimalFormat df   = new DecimalFormat("#%");
			
			Card card = new Card(c);
			card.setText("Battery Level: " + df.format(batteryFrac));
			View cardView = card.toView();
			setContentView(cardView);
		}
	};

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
}
