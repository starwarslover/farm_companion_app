package com.licence.serban.farmcompanion.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.consumables.fragments.InputsFragment;
import com.licence.serban.farmcompanion.dashboard.fragments.DashboardFragment;
import com.licence.serban.farmcompanion.emp_account.fragments.EmpTaskTrackingFragment;
import com.licence.serban.farmcompanion.emp_account.fragments.EmployeeTasksFragment;
import com.licence.serban.farmcompanion.employees.fragments.AddEmployeeFragment;
import com.licence.serban.farmcompanion.employees.fragments.EmployeesFragment;
import com.licence.serban.farmcompanion.equipment.fragments.EquipmentFragment;
import com.licence.serban.farmcompanion.fields.fragments.AddFieldFragment;
import com.licence.serban.farmcompanion.fields.fragments.FieldDetailsFragment;
import com.licence.serban.farmcompanion.fields.fragments.FieldsFragment;
import com.licence.serban.farmcompanion.interfaces.OnAddEmployeeListener;
import com.licence.serban.farmcompanion.interfaces.OnAddFieldListener;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnCreateNewTaskListener;
import com.licence.serban.farmcompanion.interfaces.OnDatePickerSelectedListener;
import com.licence.serban.farmcompanion.interfaces.OnDateSelectedListener;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnElementAdded;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.interfaces.OnTaskCreatedListener;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.fragments.CompanyInfoFragment;
import com.licence.serban.farmcompanion.misc.fragments.DateFragment;
import com.licence.serban.farmcompanion.misc.models.Company;
import com.licence.serban.farmcompanion.misc.models.User;
import com.licence.serban.farmcompanion.tasks.fragments.NewTaskFragment;
import com.licence.serban.farmcompanion.tasks.fragments.TaskTrackingFragment;
import com.licence.serban.farmcompanion.tasks.fragments.TasksFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnAppTitleChange, OnDrawerMenuLock,
        OnAddEmployeeListener, OnElementAdded, OnDateSelectedListener, OnAddFieldListener, OnCreateNewTaskListener, OnFragmentStart, OnTaskCreatedListener {

  private DatabaseReference databaseReference;
  private FirebaseAuth firebaseAuth;
  private FragmentManager fragmentManager;
  public static String adminID;
  public static boolean isAdmin;
  private String userID;
  private User currentUser;

  private TextView navNameTextView;
  private TextView navEmailTextView;
  private TextView navCompanyNameTextView;
  private ActionBar actionBar;
  private DrawerLayout drawer;
  private ActionBarDrawerToggle toggle;
  private NavigationView navigationView;
  private String employerID;
  private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    firebaseAuth = FirebaseAuth.getInstance();
    fragmentManager = getSupportFragmentManager();

    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    if (firebaseUser != null) {
      userID = firebaseUser.getUid();
      getInfo();
    } else {
      logout();
    }

    setViews();
  }

  private void getInfo() {
    databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference usersDatabaseReference = databaseReference.child(Utilities.Constants.DB_USERS);
    usersDatabaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        ((ViewAnimator) findViewById(R.id.viewAnimator)).setDisplayedChild(1);
        currentUser = dataSnapshot.getValue(User.class);
        if (currentUser != null) {
          if (currentUser.isAdmin()) {
            adminID = currentUser.getId();
          } else {
            adminID = currentUser.getEmployerID();
          }
          getCompanyInfo();
          navNameTextView.setText(currentUser.getName());
          navEmailTextView.setText(currentUser.getEmail());
          showMenu(navigationView);
          setFirstStart();
        } else {
          logout();
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });


  }

  private void getCompanyInfo() {
    DatabaseReference companyRef = databaseReference.child(CompanyInfoFragment.DB_COMPANIES).child(adminID);
    companyRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
          Company company = dataSnapshot.getValue(Company.class);
          if (company != null) {
            navCompanyNameTextView.setText(company.getName());
          }
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  private void loadDashboard() {
  }

  private void setViews() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    actionBar = getSupportActionBar();
    drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    View header = navigationView.getHeaderView(0);


    navNameTextView = (TextView) header.findViewById(R.id.navNameTextView);
    navEmailTextView = (TextView) header.findViewById(R.id.navEmailTextView);
    navCompanyNameTextView = (TextView) header.findViewById(R.id.navCompanyTextView);
  }

  private void setFirstStart() {
    Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_main);
    if (currentFragment == null) {
      FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
      fragmentTransaction.add(R.id.content_main, new DashboardFragment()).commit();
      requestPermissions(this);
    }
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_logout) {
      logout();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void logout() {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    firebaseAuth.signOut();
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(Utilities.Constants.EMAIL, "");
    editor.putString(Utilities.Constants.PASSWORD, "");
    editor.apply();
    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    MainActivity.this.finish();
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);

    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    Bundle bundle = new Bundle();
    bundle.putString(Utilities.Constants.USER_ID, userID);
    switch (item.getItemId()) {
      case R.id.nav_dash:
        fragmentTransaction.replace(R.id.content_main, new DashboardFragment()).commit();
        break;
      case R.id.nav_inputs:
        Fragment inputs = new InputsFragment();
        inputs.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, inputs).commit();
        break;
      case R.id.nav_fields:
        FieldsFragment fieldsFragment = new FieldsFragment();
        fieldsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, fieldsFragment).commit();
        break;
      case R.id.nav_equipment:
        Fragment fragment = new EquipmentFragment();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, fragment).commit();
        break;
      case R.id.nav_employees:
        EmployeesFragment employeesFragment = new EmployeesFragment();
        employeesFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, employeesFragment).commit();
        break;
      case R.id.nav_activities:
        TasksFragment activities = new TasksFragment();
        activities.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, activities).commit();
        break;
      case R.id.nav_company_info:
        CompanyInfoFragment companyInfoFragment = new CompanyInfoFragment();
        companyInfoFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, companyInfoFragment).commit();
        break;
      case R.id.nav_emp_start_activity:
        EmployeeTasksFragment workFragment = new EmployeeTasksFragment();
        bundle.putString(Utilities.Constants.DB_EMPLOYER_ID, employerID);
        workFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, workFragment).commit();
        break;
      case R.id.nav_emp_dashboard:
        Fragment dashboardFragment = new DashboardFragment();
        dashboardFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, dashboardFragment).commit();
        break;
      case R.id.nav_ongoing_tasks:
        TaskTrackingFragment taskTrackingFragment = new TaskTrackingFragment();
        taskTrackingFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, taskTrackingFragment).commit();
        break;
    }
    return true;
  }

  public void showMenu(NavigationView navView) {
    if (currentUser.isAdmin()) {
      navView.getMenu().setGroupVisible(R.id.nav_admin_menu, true);
      navView.getMenu().setGroupVisible(R.id.nav_emp_menu, false);
    } else {
      navView.getMenu().setGroupVisible(R.id.nav_admin_menu, false);
      navView.getMenu().setGroupVisible(R.id.nav_emp_menu, true);
      employerID = currentUser.getEmployerID();
    }
  }

  @Override
  public void updateTitle(String title) {
    actionBar.setTitle(title);
  }

  @Override
  public void lockDrawer() {
    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
  }

  @Override
  public void unlockDrawerMenu() {
    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
  }

  @Override
  public void openAddEmployeeUI() {
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    AddEmployeeFragment addEmployeeFragment = new AddEmployeeFragment();
    Bundle bundle = new Bundle();
    bundle.putString(Utilities.Constants.USER_ID, userID);
    addEmployeeFragment.setArguments(bundle);
    fragmentTransaction.replace(R.id.content_main, addEmployeeFragment, Utilities.Constants.DATE_FRAGMENT).addToBackStack(null).commit();
  }

  @Override
  public void openEditEmployeeUI(String employeeId) {
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    AddEmployeeFragment addEmployeeFragment = new AddEmployeeFragment();
    Bundle bundle = new Bundle();
    bundle.putString(Utilities.Constants.USER_ID, userID);
    bundle.putString(Utilities.Constants.EMPLOYEE_ID, employeeId);
    addEmployeeFragment.setArguments(bundle);
    fragmentTransaction.replace(R.id.content_main, addEmployeeFragment, Utilities.Constants.DATE_FRAGMENT).addToBackStack(null).commit();
  }

  @Override
  public void endFragment() {
    fragmentManager.popBackStackImmediate();
  }

  @Override
  public void openDatePicker(long date) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    DateFragment dateFragment = new DateFragment();
    if (date != -1) {
      Bundle bundle = new Bundle();
      bundle.putLong(Utilities.Constants.DATE_EDIT, date);
      dateFragment.setArguments(bundle);
    }
    dateFragment.show(fragmentTransaction, "dialog");
  }

  @Override
  public void onDateSelected(String date) {
    OnDatePickerSelectedListener currentFragment = (OnDatePickerSelectedListener) getSupportFragmentManager().findFragmentByTag(Utilities.Constants.DATE_FRAGMENT);
    if (currentFragment != null) {
      currentFragment.setDate(date);
    }
  }

  @Override
  public void openAddFieldUI() {
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    AddFieldFragment addFieldFragment = new AddFieldFragment();
    Bundle bundle = new Bundle();
    bundle.putString(Utilities.Constants.USER_ID, userID);
    addFieldFragment.setArguments(bundle);
    fragmentTransaction.replace(R.id.content_main, addFieldFragment).addToBackStack(null).commit();
  }

  @Override
  public void openFieldDetailsFragment(String fieldID) {
    FieldDetailsFragment fieldDetailsFragment = new FieldDetailsFragment();
    Bundle arguments = new Bundle();
    arguments.putString(Utilities.Constants.USER_ID, userID);
    arguments.putString(Utilities.Constants.FIELD_ID, fieldID);
    fieldDetailsFragment.setArguments(arguments);

    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.content_main, fieldDetailsFragment).addToBackStack(null).commit();
  }

  @Override
  public void openEditFieldUI(String fieldID) {
    AddFieldFragment editFieldFragment = new AddFieldFragment();
    Bundle arguments = new Bundle();
    arguments.putString(Utilities.Constants.USER_ID, userID);
    arguments.putString(Utilities.Constants.FIELD_ID, fieldID);
    editFieldFragment.setArguments(arguments);

    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.content_main, editFieldFragment).addToBackStack(null).commit();
  }

  public void requestPermissions(Activity activity) {
    if (ContextCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(activity,
              new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Utilities.Constants.REQUEST_FINE_LOCATION);


    }

  }

  @Override
  public void openNewTaskUI() {
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    NewTaskFragment taskFragment = new NewTaskFragment();
    Bundle bundle = new Bundle();
    bundle.putString(Utilities.Constants.USER_ID, userID);
    bundle.putString(Utilities.Constants.DB_EMPLOYER_ID, employerID);
    taskFragment.setArguments(bundle);
    fragmentTransaction.replace(R.id.content_main, taskFragment).addToBackStack(null).commit();
  }

  @Override
  public void StartActivityTracking(String taskId) {
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    EmpTaskTrackingFragment trackingFragment = new EmpTaskTrackingFragment();
    Bundle args = new Bundle();
    args.putString(Utilities.Constants.TASK_ID_EXTRA, taskId);
    args.putString(Utilities.Constants.DB_EMPLOYER_ID, employerID);
    trackingFragment.setArguments(args);
    fragmentTransaction.replace(R.id.content_main, trackingFragment).addToBackStack(null).commit();
  }

  @Override
  public void startFragment(Fragment fragmentToStart, boolean addToBackStack) {
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(R.id.content_main, fragmentToStart);
    if (addToBackStack) {
      transaction.addToBackStack(null);
    }
    transaction.commit();
  }

  @Override
  public void startFragment(Fragment fragmentToStart, boolean addToBackStack, String tag) {
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(R.id.content_main, fragmentToStart, tag);
    if (addToBackStack) {
      transaction.addToBackStack(null);
    }
    transaction.commit();
  }

  @Override
  public void popBackStack() {
    fragmentManager.popBackStack();
  }

  public boolean isUserAdmin() {
    if (currentUser != null)
      return currentUser.isAdmin();
    return false;
  }
}
