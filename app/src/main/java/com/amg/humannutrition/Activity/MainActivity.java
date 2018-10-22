package com.amg.humannutrition.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewGroupCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amg.humannutrition.Fragment.GoalFragment;
import com.amg.humannutrition.Fragment.HomeFragment;
import com.amg.humannutrition.Fragment.NutritionFragment;
import com.amg.humannutrition.Fragment.SettingsFragment;
import com.amg.humannutrition.Fragment.ToolsFragment;
import com.amg.humannutrition.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_GOAL = "goal";
    private static final String TAG_NUTRITION = "nutrition";
    private static final String TAG_TOOLS = "tools";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        ImageView fabIcon = new ImageView(this);
        fabIcon.setImageResource(R.drawable.ic_add_black_24dp);
        FloatingActionButton fab = new FloatingActionButton.Builder(this)
                .setContentView(fabIcon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView addWaterIcon = new ImageView(this);
        addWaterIcon.setImageResource(R.drawable.ic_water_24dp);
        SubActionButton addWater = itemBuilder.setContentView(addWaterIcon)
                .setLayoutParams(new SubActionButton.LayoutParams(100,100))
                .build();


        ImageView addMealIcon = new ImageView(this);
        addMealIcon.setImageResource(R.drawable.ic_local_meal_24dp);
        SubActionButton addMeal = itemBuilder.setContentView(addMealIcon)
                .setLayoutParams(new SubActionButton.LayoutParams(100,100))
                .build();



        ImageView addActivityIcon = new ImageView(this);
        addActivityIcon.setImageResource(R.drawable.ic_running);
        SubActionButton addActivity = itemBuilder.setContentView(addActivityIcon)
                .setLayoutParams(new SubActionButton.LayoutParams(100,100))
                .build();


        FloatingActionMenu fabMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(addActivity)
                .addSubActionView(addMeal)
                .addSubActionView(addWater)
                .setRadius(200)
                .attachTo(fab)
                .build();
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        // Navigation view header
        View navHeader = navigationView.getHeaderView(0);
        TextView txtName = (TextView) navHeader.findViewById(R.id.header_name);
        TextView txtEmail = (TextView) navHeader.findViewById(R.id.header_email);
        ImageView imgProfile = (ImageView) navHeader.findViewById(R.id.header_imageprofile);
        String userJsonData = Get_UserData();
        if (Objects.equals(userJsonData, "null"))
        {
            logout();
        }
        else
        {
            try{
                JSONObject jsonObject = new JSONObject(userJsonData);
                String headerName = jsonObject.getString("U_name");
                String headerEmail = jsonObject.getString("Email");
                //String Email = jsonObject.getString("Email");
                txtName.setText(headerName);
                txtEmail.setText(headerEmail);
            }catch (Exception e){

            }

        }

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    void Set_EmailData() {
        try {
            SharedPreferences.Editor sharedPref = getSharedPreferences("UserData", MODE_PRIVATE).edit();
            sharedPref.clear();
            sharedPref.commit();
        } catch (Exception e) {
            Log.e(getString(R.string.TAG), "Error: " + e.toString());
        }
    }


    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.container, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        //Closing drawer on item click
        drawer.closeDrawer(GravityCompat.START);

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                GoalFragment goalFragment = new GoalFragment();
                return goalFragment;
            case 2:
                // movies fragment
                NutritionFragment nutritionFragment = new NutritionFragment();
                return nutritionFragment;
            case 3:
                // notifications fragment
               ToolsFragment toolsFragment = new ToolsFragment();
                return toolsFragment;

            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {

        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_logout) {
                    logout();
                } else {
                    if (id == R.id.nav_home) {
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                    } else if (id == R.id.nav_goal) {
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_GOAL;
                    } else if (id == R.id.nav_nutrition) {
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_NUTRITION;
                    } else if (id == R.id.nav_tools) {
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_TOOLS;
                    } else if (id == R.id.nav_settings) {
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                    }
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    loadHomeFragment();
                }  //Checking if the item is in checked state or not, if not make it in checked state
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                item.setChecked(true);

                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void logout() {
        try {
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            StringRequest tokenRequest = new StringRequest(Request.Method.POST,
                    getString(R.string.Resource_Address) + getString(R.string.UpdateToken_Address),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (Objects.equals(response, "Done")) {
                                Set_EmailData();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            } else {

                                Log.e(getString(R.string.TAG), "Error: response =" + response);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    //do nothing
                    Log.e(getString(R.string.TAG), "Error: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", Get_Email());
                    params.put("token", "1");
                    return params;
                }

            };
            // Adding request to request queue
            queue.add(tokenRequest);

        } catch (Exception e) {

            Log.e(getString(R.string.TAG), " Exception = " + String.valueOf(e));
            }

    }
    String Get_UserData() {
        SharedPreferences sharedPref = getSharedPreferences("UserData", MODE_PRIVATE);
        String restoredText = sharedPref.getString("JsonUserData", null);
        if (restoredText != null) {
//            String Name = sharedPref.getString("name", "No name defined");//"No name defined" is the default value.
//            String Email = sharedPref.getString("name", "No name defined");//"No name defined" is the default value.
            return restoredText;
        }
        return "null";
    }
    String Get_Email() {
        SharedPreferences sharedPref = getSharedPreferences("UserData", MODE_PRIVATE);
        String Email = sharedPref.getString("Email", null);
        if (Email != null) {
            return Email;
        }
        return "";
    }

}

