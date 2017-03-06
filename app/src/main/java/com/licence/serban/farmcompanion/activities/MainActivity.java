package com.licence.serban.farmcompanion.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.licence.serban.farmcompanion.classes.Company;
import com.licence.serban.farmcompanion.fragments.AddEmployeeFragment;
import com.licence.serban.farmcompanion.fragments.CompanyInfoFragment;
import com.licence.serban.farmcompanion.interfaces.OnAddEmployeeListener;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.fragments.ActivitiesFragment;
import com.licence.serban.farmcompanion.fragments.DashboardFragment;
import com.licence.serban.farmcompanion.fragments.EmployeesFragment;
import com.licence.serban.farmcompanion.fragments.EquipmentFragment;
import com.licence.serban.farmcompanion.fragments.FieldsFragment;
import com.licence.serban.farmcompanion.fragments.InputsFragment;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.User;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnAppTitleChange, OnDrawerMenuLock, OnAddEmployeeListener {

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
                fragmentTransaction.replace(R.id.content_main, new FieldsFragment()).commit();
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
                fragmentTransaction.replace(R.id.content_main, new ActivitiesFragment()).commit();
                break;
            case R.id.nav_company_info:
                CompanyInfoFragment companyInfoFragment = new CompanyInfoFragment();
                companyInfoFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_main, companyInfoFragment).commit();
                break;
        }
        return true;
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
        fragmentTransaction.replace(R.id.content_main, addEmployeeFragment).addToBackStack(null).commit();
    }
}
