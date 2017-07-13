package com.licence.serban.farmcompanion.consumables.fragments;


import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.ConsumableEnum;
import com.licence.serban.farmcompanion.misc.Utilities;


/**
 * A simple {@link Fragment} subclass.
 */
public class InputsFragment extends Fragment {

  private OnAppTitleChange updateTitleCallback;
  private OnFragmentStart fragmentStart;

  private TabLayout tabLayout;
  private ViewPager viewPager;
  private ActionBar actionBar;
  private FloatingActionButton addButton;
  private String userID;

  public InputsFragment() {

  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      updateTitleCallback = (OnAppTitleChange) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnHeadlineSelectedListener");
    }
    try {
      fragmentStart = (OnFragmentStart) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnFragmentStart");
    }

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_inputs, container, false);

    updateTitleCallback.updateTitle(getResources().getString(R.string.nav_inputs));
    Bundle args = getArguments();
    if (args != null) {
      userID = args.getString(Utilities.Constants.USER_ID);
    }

    tabLayout = (TabLayout) view.findViewById(R.id.inputsTabLayout);
    viewPager = (ViewPager) view.findViewById(R.id.inputsViewPager);
    addButton = (FloatingActionButton) view.findViewById(R.id.addConsumableButton);
    addButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Fragment consAddFragment = new ConsumableAddFragment();
        Bundle args = new Bundle();
        args.putString(Utilities.Constants.USER_ID, userID);
        consAddFragment.setArguments(args);
        fragmentStart.startFragment(consAddFragment, true, Utilities.Constants.DATE_FRAGMENT);
      }
    });

    viewPager.setAdapter(new InputsPagerAdapter(getChildFragmentManager()));
    tabLayout.setupWithViewPager(viewPager);

    return view;
  }


  private class InputsPagerAdapter extends FragmentPagerAdapter {
    private int itemsNumber = 4;

    public InputsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      InputListFragment inputListFragment = new InputListFragment();
      Bundle inputTypeBundle = new Bundle();
      inputTypeBundle.putSerializable(Utilities.Constants.USER_ID, userID);
      switch (position) {
        case 0:
          inputTypeBundle.putSerializable(Utilities.Constants.BUNDLE_INPUT_TYPE, ConsumableEnum.SEMINTE);
          break;
        case 1:
          inputTypeBundle.putSerializable(Utilities.Constants.BUNDLE_INPUT_TYPE, ConsumableEnum.IERBICIDE);
          break;
        case 2:
          inputTypeBundle.putSerializable(Utilities.Constants.BUNDLE_INPUT_TYPE, ConsumableEnum.FERTILIZATORI);
          break;
        case 3:
          inputTypeBundle.putSerializable(Utilities.Constants.BUNDLE_INPUT_TYPE, ConsumableEnum.COMBUSTIBIL);
          break;
      }
      inputListFragment.setArguments(inputTypeBundle);
      return inputListFragment;
    }

    @Override
    public int getCount() {
      return itemsNumber;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return getResources().getString(R.string.seeds_title);
        case 1:
          return getResources().getString(R.string.chem_title);
        case 2:
          return getResources().getString(R.string.fertiliser_title);
        case 3:
          return getResources().getString(R.string.fuel_title);
      }
      return null;
    }
  }


}
