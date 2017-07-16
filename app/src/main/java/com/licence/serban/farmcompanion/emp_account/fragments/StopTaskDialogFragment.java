package com.licence.serban.farmcompanion.emp_account.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.licence.serban.farmcompanion.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StopTaskDialogFragment extends DialogFragment {

  private View.OnClickListener stopTaskListener;
  private View.OnClickListener pauseTaskListener;

  private Button stopTaskButton;
  private Button pauseTaskButton;
  private Button cancelButton;

  public StopTaskDialogFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_stop_task_dialog, container, false);

    setViews(view);

    return view;
  }

  private void setViews(View view) {
    stopTaskButton = (Button) view.findViewById(R.id.stopTaskButton);
    pauseTaskButton = (Button) view.findViewById(R.id.pauseTaskButton);
    cancelButton = (Button) view.findViewById(R.id.cancelButton);

    stopTaskButton.setOnClickListener(this.stopTaskListener);

    pauseTaskButton.setOnClickListener(this.pauseTaskListener);

    cancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        StopTaskDialogFragment.this.dismiss();
      }
    });
  }

  public void addStopTaskListener(View.OnClickListener listener) {
    this.stopTaskListener = listener;
  }

  public void addOnPauseListener(View.OnClickListener listener) {
    this.pauseTaskListener = listener;
  }

}
