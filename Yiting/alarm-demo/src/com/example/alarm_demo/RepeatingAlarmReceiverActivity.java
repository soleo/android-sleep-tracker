package com.example.alarm_demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RepeatingAlarmReceiverActivity extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);
		
		Button stopAlarm = (Button) findViewById(R.id.btnStopAlarm);
		stopAlarm.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				finish();
			}
		});
	}
}
