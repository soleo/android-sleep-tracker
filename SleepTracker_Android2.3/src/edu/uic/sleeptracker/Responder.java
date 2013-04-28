package edu.uic.sleeptracker;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Responder {

	private int m_type;
	private int m_level;
	Responder(int type, int level)
	{
		m_type = type;
		m_level = level;
	}
	
	
}
