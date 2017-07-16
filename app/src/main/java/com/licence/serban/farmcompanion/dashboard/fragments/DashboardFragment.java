package com.licence.serban.farmcompanion.dashboard.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.activities.MainActivity;
import com.licence.serban.farmcompanion.emp_account.adapters.EmpTasksAdapter;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.StringDateFormatter;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.fragments.CompanyInfoFragment;
import com.licence.serban.farmcompanion.misc.models.Company;
import com.licence.serban.farmcompanion.misc.weather.WeatherHelper;
import com.licence.serban.farmcompanion.misc.weather.datasource.OpenWeatherMapClient;
import com.licence.serban.farmcompanion.misc.weather.datasource.WeatherDataSource;
import com.licence.serban.farmcompanion.misc.weather.models.WeatherCondition;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.tasks.fragments.TaskDetailsFragment;
import com.licence.serban.farmcompanion.tasks.models.Task;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

  private OnAppTitleChange updateTitleCallback;
  private DatabaseReference mainRef;
  private ValueEventListener companyEventListener;
  private RelativeLayout empTasksLayout;
  private boolean isAdmin;
  private ListView empDashTasksListView;
  private EmpTasksAdapter taskAdapter;
  private OnFragmentStart startFragmentCallback;

  public DashboardFragment() {
    // Required empty public constructor
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
      startFragmentCallback = (OnFragmentStart) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnFragmentStart");
    }

  }

  @Override
  public void onDetach() {
    super.onDetach();
    mainRef.child(CompanyInfoFragment.DB_COMPANIES).child(MainActivity.adminID).removeEventListener(companyEventListener);
    if (this.taskAdapter != null)
      TasksDatabaseAdapter.getInstance(MainActivity.adminID).removeListeners();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
    updateTitleCallback.updateTitle(getResources().getString(R.string.nav_dash));
    mainRef = FirebaseDatabase.getInstance().getReference();

    getViews(view);

    loadCompany(view);

    return view;
  }

  private void getViews(View view) {
    empTasksLayout = (RelativeLayout) view.findViewById(R.id.empTasksLayout);


    isAdmin = ((MainActivity) DashboardFragment.this.getActivity()).isUserAdmin();
    if (!isAdmin)
      setEmpLayouts(view);

  }

  private void setEmpLayouts(View view) {
    empTasksLayout.setVisibility(View.VISIBLE);
    empDashTasksListView = (ListView) view.findViewById(R.id.empDashTasksListView);
    taskAdapter = new EmpTasksAdapter(DashboardFragment.this.getActivity(), R.layout.emp_task_row, new ArrayList<Task>());
    empDashTasksListView.setAdapter(taskAdapter);
    empDashTasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = taskAdapter.getItem(position);
        startTaskDetails(task, position);
      }
    });

    TasksDatabaseAdapter.getInstance(MainActivity.adminID).setListener(taskAdapter, FirebaseAuth.getInstance().getCurrentUser().getUid());
  }

  private void startTaskDetails(Task task, int position) {
    Fragment fragment = new TaskDetailsFragment();
    Bundle args = new Bundle();
    args.putString(Utilities.Constants.TASK_ID_EXTRA, task.getId());
    args.putString(Utilities.Constants.DB_EMPLOYER_ID, MainActivity.adminID);
    args.putInt("position", position);
    fragment.setArguments(args);
    startFragmentCallback.startFragment(fragment, true);
  }

  private void loadCompany(final View view) {
    companyEventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Company company = dataSnapshot.getValue(Company.class);
        if (company != null)
          loadForecast(company, view);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    mainRef.child(CompanyInfoFragment.DB_COMPANIES).child(MainActivity.adminID)
            .addListenerForSingleValueEvent(companyEventListener);
  }

  private void loadForecast(Company company, final View view) {
    WeatherDataSource dataSource = new OpenWeatherMapClient();
    dataSource.getForecast(company.getCoordinates().toLatLng(), new WeatherDataSource.Callback<List<WeatherCondition>>() {
      @Override
      public void onSuccess(List<WeatherCondition> arg) {
        renderForecastUi(arg, view);
      }

      @Override
      public void onError() {

      }
    });
  }

  private void renderForecastUi(List<WeatherCondition> arg, View view) {
    ((ViewAnimator) view.findViewById(R.id.switcher_forecast)).setDisplayedChild(1);
    LinearLayout layout = (LinearLayout) view.findViewById(R.id.hourly_forecast_layout);
    for (WeatherCondition condition : arg) {
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
      View forecast = LayoutInflater.from(DashboardFragment.this.getActivity()).inflate(R.layout.item_forecast, layout, false);
      forecast.setLayoutParams(params);
      TextView tv_hour = (TextView) forecast.findViewById(R.id.forecastItemHourTextView);
      TextView tv_temp = (TextView) forecast.findViewById(R.id.forecastItemTempTextView);
      ImageView forecastImg = (ImageView) forecast.findViewById(R.id.forecastImage);

      forecastImg.setBackgroundResource(WeatherHelper.getIconForCondition(condition));
      tv_hour.setText(StringDateFormatter.formatDate(condition.getDate(), "HH:mm"));
      tv_temp.setText(WeatherHelper.getTemperatureString(condition.getTemp()));
      layout.addView(forecast);
    }
  }

}
