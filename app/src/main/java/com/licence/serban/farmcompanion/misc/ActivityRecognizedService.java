package com.licence.serban.farmcompanion.misc;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.licence.serban.farmcompanion.emp_account.fragments.EmpTaskTrackingFragment;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Serban on 13.07.2017.
 */

public class ActivityRecognizedService extends IntentService {

  public static final String ACTIVITY_RESULT = "activity_result";
  public static final int RESULT_OK = 24;

  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */
  public ActivityRecognizedService(String name) {
    super(name);
  }

  public ActivityRecognizedService() {
    super("ActivityRecognizedService");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    if (ActivityRecognitionResult.hasResult(intent)) {
      ActivityRecognitionResult activityRecognitionResult = ActivityRecognitionResult.extractResult(intent);
      this.handleDetectedActivities(activityRecognitionResult.getProbableActivities());
    }
  }

  private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
    DetectedActivity detectedActivity;
    if (probableActivities.size() != 0) {
      detectedActivity = probableActivities.get(0);
      for (DetectedActivity activity : probableActivities) {
        if (activity.getConfidence() > detectedActivity.getConfidence())
          detectedActivity = activity;
      }

      Intent localIntent = new Intent(EmpTaskTrackingFragment.BROADCAST_ACTION);
      if (detectedActivity != null) {
        DetectedActivityWrapper wrapper = new DetectedActivityWrapper();
        wrapper.detectedActivity = detectedActivity;
        localIntent.putExtra(ACTIVITY_RESULT, wrapper);
      }
      LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
  }

  public class DetectedActivityWrapper implements Serializable {
    public DetectedActivity detectedActivity;
  }
}
