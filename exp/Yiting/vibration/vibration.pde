// Imports:
import android.content.Context;
import android.app.Notification;
import android.app.NotificationManager;
import android.view.MotionEvent;

// Setup vibration globals:
NotificationManager gNotificationManager;
Notification gNotification;
long[] gVibrate = {0,250,50,125,50,62};

void setup() {
  size(displayWidth, displayHeight);
}

void draw() {
  // do nothing...
}

void onResume() {
  super.onResume();
  gNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
  // Create our Notification that will do the vibration:
  gNotification = new Notification();
  // Set the vibration:
  gNotification.vibrate = gVibrate;
}

public boolean surfaceTouchEvent(MotionEvent event) {
  gNotificationManager.notify(1, gNotification);
  return super.surfaceTouchEvent(event);
}

