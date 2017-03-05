package com.licence.serban.farmcompanion.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.InputTypeEnum;
import com.licence.serban.farmcompanion.classes.Utilities;


/**
 * A simple {@link Fragment} subclass.
 */
public class InputsFragment extends Fragment {

    private OnAppTitleChange updateTitleCallback;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActionBar actionBar;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inputs, container, false);

        updateTitleCallback.updateTitle(getResources().getString(R.string.nav_inputs));

        tabLayout = (TabLayout) view.findViewById(R.id.inputsTabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.inputsViewPager);

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
            switch (position) {
                case 0:
                    inputTypeBundle.putSerializable(Utilities.Constants.BUNDLE_INPUT_TYPE, InputTypeEnum.SEED);
                    break;
                case 1:
                    inputTypeBundle.putSerializable(Utilities.Constants.BUNDLE_INPUT_TYPE, InputTypeEnum.CHEMICAL);
                    break;
                case 2:
                    inputTypeBundle.putSerializable(Utilities.Constants.BUNDLE_INPUT_TYPE, InputTypeEnum.FERTILISER);
                    break;
                case 3:
                    inputTypeBundle.putSerializable(Utilities.Constants.BUNDLE_INPUT_TYPE, InputTypeEnum.FUEL);
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
