package processing.test.sleeptracker;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import apwidgets.*; 
import android.util.DisplayMetrics; 
import ketai.ui.*; 
import ketai.data.*; 
import ketai.sensors.*; 
import android.os.Bundle; 
import android.os.Environment; 
import java.io.IOException; 
import java.io.File; 
import android.os.Bundle; 
import android.os.Environment; 
import android.util.Log; 
import android.media.MediaRecorder; 
import android.media.MediaPlayer; 
import java.io.IOException; 

import apwidgets.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SleepTracker extends PApplet {


 








String storagePath = null;
String storageFilename = null;
SoundCapture soundCapture;
KetaiSensor sensor;
KetaiSQLite db;
Boolean isCapturing = false;
float accelerometerX, accelerometerY, accelerometerZ;
String CREATE_DB_SQL = "CREATE TABLE data ( time INTEGER PRIMARY KEY, x FLOAT NOT NULL, y FLOAT NOT NULL, z FLOAT NOT NULL);";

APWidgetContainer widgetContainer; 

APButton sleepBtn;
APButton wakeupBtn;
APButton resetBtn;
APButton configBtn;

KetaiList configList;
ArrayList<String> soundConfigList = new ArrayList<String>();
String currentSoundConfig = null;
//int rectSize = 100;
String startTime = null;
String endTime = null;

public void configSound(){
  soundConfigList.add("4.4k");
  soundConfigList.add("3.3k");
}

public void configSensors(){
  sensor = new KetaiSensor(this);
  sensor.list();
  
}

public void configDb(){
  db = new KetaiSQLite(this);
   //lets make our table if it is the first time we're running 
  if ( db.connect() )
  {
    // for initial app launch there are no tables so we make one
    if (!db.tableExists("data"))
      db.execute(CREATE_DB_SQL);
  } 
}
public void initSensors(){
   //sound record setting
   soundCapture = new SoundCapture(storagePath, storageFilename);
   
   sensor.start();
   if(!sensor.isAccelerometerAvailable()){
      sensor.enableAccelerometer();
   }
}

public void initStorage(){
   storagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SleepTracker/";
   File storageDirectory = new File(storagePath);
   // have the object build the directory structure, if needed.
   storageDirectory.mkdirs();
   
   storageFilename = "test-" + year()+month()+day()+"-"+System.currentTimeMillis();
}
public void setup() {
   
   orientation(PORTRAIT); 
   //config
   configSound();
   configSensors();
   configDb();
   
   //get device info
   DisplayMetrics dm = new DisplayMetrics();
   getWindowManager().getDefaultDisplay().getMetrics(dm);
   float density = dm.density; 
   int densityDpi = dm.densityDpi;
   println("density is " + density); 
   println("densityDpi is " + densityDpi);
   println("Display Width is " + displayWidth);
   println("Display Height is " + displayHeight);
   
   smooth();
   noStroke();
   fill(255);
   
    widgetContainer = new APWidgetContainer(this); //create new container for widgets
    
    sleepBtn = new APButton(10, 10, displayWidth-20, 110, "I'm going to sleep."); //create new button from x- and y-pos. and label. size determined by text content
    wakeupBtn = new APButton(10, 10, displayWidth-20, 110, "I'm awake now."); //create new button from x- and y-pos., width, height and label
    sleepBtn.setTextSize(28);
    wakeupBtn.setTextSize(28);
    resetBtn = new APButton(10, 130, displayWidth-20, 50, "Reset");
    configBtn = new APButton(10, 190, displayWidth-20, 50, "Select Configuration");
    widgetContainer.addWidget(sleepBtn); //place button in container
     
    widgetContainer.addWidget(resetBtn); 
    widgetContainer.addWidget(configBtn);
   
   
   initStorage(); 
   initSensors();

}
 
public void draw() {
   background(0xffFF9900);
   rectMode(CENTER);     //This sets all rectangles to draw from the center point
   //rect(width/2, height/2, rectSize, rectSize); 
   if(isCapturing){
   text("Accelerometer: \n" + 
    "x: " + nfp(accelerometerX, 1, 3) + "\n" +
    "y: " + nfp(accelerometerY, 1, 3) + "\n" +
    "z: " + nfp(accelerometerZ, 1, 3), 10, 250);
   }
}

//onClickWidget is called when a widget is clicked/touched
public void onClickWidget(APWidget widget){

  if(widget == sleepBtn){ //if it was SleepBtn that was clicked
    //rectSize -= 50; //set the smaller size
    isCapturing = true;
    
    widgetContainer.removeWidget(sleepBtn);
    widgetContainer.addWidget(wakeupBtn);
    
  }else if(widget == wakeupBtn){ //or if it was WakeupBtn
    isCapturing = false;
    
    if (db.connect())
    {
      try{
        db.exportData(storagePath);
      }catch(IOException ex){
        println("Can not export data");
      }
      db.deleteAllData();
    }
    //rectSize += 50; //set the bigger size
    widgetContainer.removeWidget(wakeupBtn);
    widgetContainer.addWidget(sleepBtn);
    
  }else if(widget == resetBtn){
    //rectSize = 100;
    sensor.stop();
    sensor.start();
    isCapturing = false;
  }else if(widget == configBtn){
    configList = new KetaiList(this, soundConfigList);
  }
  soundCapture.onRecord(isCapturing);
}

public void onKetaiListSelection(KetaiList klist)
{
  String selection = klist.getSelection();
  if (selection == "4.4k"){
    println("4.4k");
    currentSoundConfig = "4.4k";
  }
  else if (selection == "3.3k"){
    println("3.3k");
    currentSoundConfig = "3.3k";
  }
}

/*
      collect accelerometer data and save it to the database
*/
public void onAccelerometerEvent(float x, float y, float z, long time, int accuracy)
{
  if (db.connect() && isCapturing)
  {
    if (!db.execute("INSERT into data (`time`,`x`,`y`,`z`) VALUES ('"+System.currentTimeMillis()+"', '"+x+"', '"+y+"', '"+z+"')"))
      println("Failed to record data!" );
  }
  
 
  if(isCapturing){
    accelerometerX = x;
    accelerometerY = y;
    accelerometerZ = z;
    println("Accelerometer: \t" + 
      "x: " + nfp(accelerometerX, 1, 3) + "\t" +
      "y: " + nfp(accelerometerY, 1, 3) + "\t" +
      "z: " + nfp(accelerometerZ, 1, 3));
  }
}












public class SoundCapture 
{
    private static final String LOG_TAG = "AudioRecord";
    private String mFileName = null;

    //private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    //private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;

    SoundCapture(String path, String fileName){
      this.mFileName = path + fileName + ".3gp";
      
    }
    
    public void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            println("prepare() faild");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

}

}
