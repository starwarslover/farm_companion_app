package com.licence.serban.farmcompanion.interfaces;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by Serban on 23.04.2017.
 */

public interface OnFragmentStart {
  void startFragment(Fragment fragmentToStart, boolean addToBackStack);

  void startFragment(Fragment fragmentToStart, boolean addToBackStack, String tag);

  void showDialog(DialogFragment dialogFragment);

  void popBackStack();
}
