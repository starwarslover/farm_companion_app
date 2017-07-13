package com.licence.serban.farmcompanion.misc;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by Serban on 13.07.2017.
 */

public class ActivityRecognizedService extends IntentService {

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
    for (DetectedActivity activity : probableActivities) {
      switch (activity.getType()) {
        case DetectedActivity.IN_VEHICLE: {
          Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
          break;
        }
        case DetectedActivity.ON_BICYCLE: {
          Log.e("ActivityRecogition", "On Bicycle: " + activity.getConfidence());
          break;
        }
        case DetectedActivity.ON_FOOT: {
          Log.e("ActivityRecogition", "On Foot: " + activity.getConfidence());
          break;
        }
        case DetectedActivity.RUNNING: {
          Log.e("ActivityRecogition", "Running: " + activity.getConfidence());
          break;
        }
        case DetectedActivity.STILL: {
          Log.e("ActivityRecogition", "Still: " + activity.getConfidence());
          break;
        }
        case DetectedActivity.TILTING: {
          Log.e("ActivityRecogition", "Tilting: " + activity.getConfidence());
          break;
        }
        case DetectedActivity.WALKING: {
          Log.e("ActivityRecogition", "Walking: " + activity.getConfidence());
          break;
        }
        case DetectedActivity.UNKNOWN: {
          Log.e("ActivityRecogition", "Unknown: " + activity.getConfidence());
          break;
        }
      }
    }
  }
}
