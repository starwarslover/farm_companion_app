package com.licence.serban.farmcompanion.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.classes.models.Company;
import com.licence.serban.farmcompanion.fragments.activities.NewTaskFragment;
import com.licence.serban.farmcompanion.fragments.employees.AddEmployeeFragment;
import com.licence.serban.farmcompanion.fragments.fields.AddFieldFragment;
import com.licence.serban.farmcompanion.fragments.misc.CompanyInfoFragment;
import com.licence.serban.farmcompanion.fragments.misc.DateFragment;
import com.licence.serban.farmcompanion.fragments.emp_account.EmployeeDashboardFragment;
import com.licence.serban.farmcompanion.fragments.emp_account.EmployeeTasksFragment;
import com.licence.serban.farmcompanion.fragments.fields.FieldDetailsFragment;
import com.licence.serban.farmcompanion.fragments.activities.TaskTrackingFragment;
import com.licence.serban.farmcompanion.interfaces.OnAddEmployeeListener;
import com.licence.serban.farmcompanion.interfaces.OnAddFieldListener;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.fragments.activities.ActivitiesFragment;
import com.licence.serban.farmcompanion.fragments.dashboard.DashboardFragment;
import com.licence.serban.farmcompanion.fragments.employees.EmployeesFragment;
import com.licence.serban.farmcompanion.fragments.equipment.EquipmentFragment;
import com.licence.serban.farmcompanion.fragments.fields.FieldsFragment;
import com.licence.serban.farmcompanion.fragments.consumables.InputsFragment;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.models.User;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.interfaces.OnCreateNewTaskListener;
import com.licence.serban.farmcompanion.interfaces.OnDateSelectedListener;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnElementAdded;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnAppTitleChange, OnDrawerMenuLock,
        OnAddEmployeeListener, OnElementAdded, OnDateSelectedListener, OnAddFieldListener, OnCreateNewTaskListener {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FragmentManager fragmentManager;


    private String userID;
    private User currentUser;

    private TextView navNameTextView;
    private TextView navEmailTextView;
    private TextView navCompanyNameTextView;
    private ActionBar actionBar;
    private DrawerLayout drawer;
    private String employerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
        actionBar = getSupportActionBar();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);


        navNameTextView = (TextView) header.findViewById(R.id.navNameTextView);
        navEmailTextView = (TextView) header.findViewById(R.id.navEmailTextView);
        navCompanyNameTextView = (TextView) header.findViewById(R.id.navCompanyTextView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersDatabaseReference = databaseReference.child(Utilities.Constants.DB_USERS);
        usersDatabaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                navNameTextView.setText(currentUser.getName());
                navEmailTextView.setText(currentUser.getEmail());
                showMenu(navigationView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        usersDatabaseReference.child(userID).child(Utilities.Constants.DB_COMPANY).addValueEventListener(new ValueEventListener() {
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


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_main, new DashboardFragment()).commit();
        requestPermissions(this);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                fragmentTransaction.replace(R.id.content_main, new InputsFragment()).commit();
                break;
            case R.id.nav_fields:
                FieldsFragment fieldsFragment = new FieldsFragment();
                fieldsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_main, fieldsFragment).commit();
                break;
            case R.id.nav_equipment:
                fragmentTransaction.replace(R.id.content_main, new EquipmentFragment()).commit();
                break;
            case R.id.nav_employees:
                EmployeesFragment employeesFragment = new EmployeesFragment();
                employeesFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_main, employeesFragment).commit();
                break;
            case R.id.nav_activities:
                ActivitiesFragment activities = new ActivitiesFragment();
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
                EmployeeDashboardFragment dashboardFragment = new EmployeeDashboardFragment();
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
            databaseReference.child(Utilities.Constants.DB_EMPLOYEES).child(userID).child(Utilities.Constants.DB_EMPLOYER_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    employerID = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
        fragmentTransaction.replace(R.id.content_main, addEmployeeFragment, Utilities.Constants.EMPLOYEE_TAG).addToBackStack(null).commit();
    }

    @Override
    public void openEditEmployeeUI(String employeeId) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddEmployeeFragment addEmployeeFragment = new AddEmployeeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Utilities.Constants.USER_ID, userID);
        bundle.putString(Utilities.Constants.EMPLOYEE_ID, employeeId);
        addEmployeeFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, addEmployeeFragment, Utilities.Constants.EMPLOYEE_TAG).addToBackStack(null).commit();
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
        AddEmployeeFragment currentFragment = (AddEmployeeFragment) getSupportFragmentManager().findFragmentByTag(Utilities.Constants.EMPLOYEE_TAG);
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
}
