package com.licence.serban.farmcompanion.interfaces;

/**
 * Created by Serban on 17.03.2017.
 */

public interface OnAddFieldListener {
    void openAddFieldUI();

    void openFieldDetailsFragment(String fieldID);

    void openEditFieldUI(String fieldID);
}
