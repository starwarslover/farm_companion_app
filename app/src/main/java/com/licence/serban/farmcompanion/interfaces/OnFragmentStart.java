package com.licence.serban.farmcompanion.interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by Serban on 23.04.2017.
 */

public interface OnFragmentStart {
    void startFragment(Fragment fragmentToStart, boolean addToBackStack);

    void popBackStack();
}