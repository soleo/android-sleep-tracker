package edu.uic.sleeptracker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
public class MainActivity extends Activity {
	public static final int DETECT_NONE = 0;
	public static final int DETECT_SNORE = 1;
	public static int selectedDetection = DETECT_NONE;
	
	// detection parameters
	private DetectorThread detectorThread;
	private RecorderThread recorderThread;
	private Thread detectedTextThread;
	public static int snoreValue = 0;
	
	// views
	private View mainView, listeningView;
	private Button sleepButton;
	private TextView totalSnoreDetectedNumberText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("UIC SleepTracker Demo");

		// set views
		LayoutInflater inflater = LayoutInflater.from(this);
		mainView = inflater.inflate(R.layout.main, null);
		listeningView = inflater.inflate(R.layout.listening, null);
		setContentView(mainView);

		sleepButton = (Button) this.findViewById(R.id.sleepButton);
		sleepButton.setOnClickListener(new ClickEvent());
        //setContentView(R.layout.activity_main);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
    private void goHomeView() {
		setContentView(mainView);
		if (recorderThread != null) {
			recorderThread.stopRecording();
			recorderThread = null;
		}
		if (detectorThread != null) {
			detectorThread.stopDetection();
			detectorThread = null;
		}
		selectedDetection = DETECT_NONE;
	}

	private void goListeningView(){
		setContentView(listeningView);

		if (totalSnoreDetectedNumberText == null){
			totalSnoreDetectedNumberText = (TextView) this.findViewById(R.id.detectedNumberText);
		}

		// thread for detecting environmental noise
		if (detectedTextThread == null){
			detectedTextThread = new Thread() {
			     public void run() {
			    	 try {
						while (recorderThread != null && detectorThread != null) {
							runOnUiThread(new Runnable() {
								public void run() {
									if (detectorThread != null){
										totalSnoreDetectedNumberText.setText(String.valueOf(detectorThread.getTotalSnoreDetected()));
									}
								}
							});
							sleep(100);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						detectedTextThread = null;
					}
			    }
			};
			detectedTextThread.start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Quit demo");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			finish();
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			goHomeView();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class ClickEvent implements OnClickListener {
		public void onClick(View view) {
			if (view == sleepButton) {
				selectedDetection = DETECT_SNORE;
				recorderThread = new RecorderThread();
				recorderThread.start();
				detectorThread = new DetectorThread(recorderThread);
				detectorThread.start();
				goListeningView();
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
