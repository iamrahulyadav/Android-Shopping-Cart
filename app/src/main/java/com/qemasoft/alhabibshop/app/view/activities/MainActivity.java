package com.qemasoft.alhabibshop.app.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.qemasoft.alhabibshop.app.AppConstants;
import com.qemasoft.alhabibshop.app.Preferences;
import com.qemasoft.alhabibshop.app.R;
import com.qemasoft.alhabibshop.app.Utils;
import com.qemasoft.alhabibshop.app.controller.ExpandableListAdapter;
import com.qemasoft.alhabibshop.app.controller.ExpandableListAdapterRight;
import com.qemasoft.alhabibshop.app.model.MenuCategory;
import com.qemasoft.alhabibshop.app.model.MenuSubCategory;
import com.qemasoft.alhabibshop.app.view.fragments.Dashboard;
import com.qemasoft.alhabibshop.app.view.fragments.FragAboutUs;
import com.qemasoft.alhabibshop.app.view.fragments.FragCartDetail;
import com.qemasoft.alhabibshop.app.view.fragments.FragCategories;
import com.qemasoft.alhabibshop.app.view.fragments.FragChangePassword;
import com.qemasoft.alhabibshop.app.view.fragments.FragContactUs;
import com.qemasoft.alhabibshop.app.view.fragments.FragEditAccount;
import com.qemasoft.alhabibshop.app.view.fragments.FragForgotPass;
import com.qemasoft.alhabibshop.app.view.fragments.FragLogin;
import com.qemasoft.alhabibshop.app.view.fragments.FragOrderDetail;
import com.qemasoft.alhabibshop.app.view.fragments.FragOrderHistory;
import com.qemasoft.alhabibshop.app.view.fragments.FragPrivacyPolicy;
import com.qemasoft.alhabibshop.app.view.fragments.FragProduct;
import com.qemasoft.alhabibshop.app.view.fragments.FragProductDetail;
import com.qemasoft.alhabibshop.app.view.fragments.FragRegister;
import com.qemasoft.alhabibshop.app.view.fragments.FragSlider;
import com.qemasoft.alhabibshop.app.view.fragments.MainFragTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.qemasoft.alhabibshop.app.AppConstants.LOGIN_KEY;
import static com.qemasoft.alhabibshop.app.AppConstants.appContext;
import static com.qemasoft.alhabibshop.app.AppConstants.getApiCallUrl;
import static com.qemasoft.alhabibshop.app.AppConstants.setProductExtra;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_EXTRA = "com.qemasoft.alhabibshop.app" + "getMainScreenExtra";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    ArrayList<Integer> loggedInIconList = new ArrayList<Integer>() {{
        add(R.drawable.ic_dashboard_black);
        add(R.drawable.ic_edit_black);
        add(R.drawable.ic_vpn_key_black);
        add(R.drawable.ic_add_shopping_cart_black);
        add(R.drawable.ic_exit_to_app_black);
        add(R.drawable.ic_language_black);
        add(R.drawable.ic_attach_money_black);
        add(R.drawable.ic_email_black);
        add(R.drawable.ic_more_vert_black);
    }};
    ArrayList<Integer> NotLoggedInIconList = new ArrayList<Integer>() {{
        add(R.drawable.ic_lock_black);
        add(R.drawable.ic_person_add_black);
        add(R.drawable.ic_language_black);
        add(R.drawable.ic_attach_money_black);
        add(R.drawable.ic_email_black);
        add(R.drawable.ic_more_vert_black);
    }};
    //   Toolbar stuff;
    ImageView drawerIconLeft, drawerIconRight, logoIcon;
    RelativeLayout cartLayout;


    private Context context;
    private SearchView searchView;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Utils utils;

    private ExpandableListView listViewExpLeft, listViewExpRight;
    private ExpandableListAdapter listAdapter;
    private ExpandableListAdapterRight listAdapterRight;
    private List<String> headerListRight;
    private HashMap<String, List<MenuSubCategory>> hashMapRight;
    private List<MenuCategory> headerListLeft;
    private HashMap<MenuCategory, List<MenuSubCategory>> hashMapLeft;
    private boolean isLoggedIn = false;

    private RelativeLayout appbarBottom;
    private TextView myAccountTV, cartTV, discountTV, homeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        this.context = this;
        this.utils = new Utils(this);

        setupToolbar(this);
        checkIsLoggedIn();
        changeFragment(0);
        setCompoundDrawable();


        loadData();
//        Log.e("DataLoadingMethodCalled","Success");
//        setupSearchView();

        initData();
        listAdapter = new ExpandableListAdapter(headerListLeft, hashMapLeft,
                false, loggedInIconList);
        listViewExpLeft.setAdapter(listAdapter);
        if (isLoggedIn) {
            listAdapterRight = new ExpandableListAdapterRight(headerListRight, hashMapRight,
                    true, loggedInIconList);
        } else {
            listAdapterRight = new ExpandableListAdapterRight(headerListRight, hashMapRight,
                    true, NotLoggedInIconList);
        }
        listViewExpRight.setAdapter(listAdapterRight);

        enableSingleSelection();
        setExpandableListViewClickListener();
        setExpandableListViewChildClickListener();


//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

//        navigationView2.setNavigationItemSelectedListener(this);
    }

    private void checkIsLoggedIn() {
        isLoggedIn = Preferences.getSharedPreferenceBoolean(appContext, LOGIN_KEY, false);
        Log.e("IsLoggedIn = ", "" + isLoggedIn);

    }

    private void clearLoginSession() {
        Preferences.setSharedPreferenceBoolean(appContext, LOGIN_KEY, false);
        isLoggedIn = Preferences.getSharedPreferenceBoolean(appContext, LOGIN_KEY, false);

    }

    private void setExpandableListViewClickListener() {
        listViewExpRight.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                        long id) {

                Log.e("GroupClicked", " Id = " + id);

//                int count = listAdapter.getChildrenCount(groupPosition);
                int childCount = parent.getExpandableListAdapter().getChildrenCount(groupPosition);
                if (!isLoggedIn) {
                    if (childCount < 1) {
                        if (groupPosition == 0) {
                            changeFragment(101);
                        } else if (groupPosition == 1) {
                            changeFragment(102);
                        } else if (groupPosition == 2) {
                            changeFragment(201);
                        } else if (groupPosition == 3) {
//                            changeFragment(103);
                        } else if (groupPosition == 4) {
                            changeFragment(109);
                        }
                        drawer.closeDrawer(GravityCompat.END);
                    }
                } else {
                    if (childCount < 1) {
                        changeFragment(103 + groupPosition);
                        drawer.closeDrawer(GravityCompat.END);
                    }
                }

                return false;
            }
        });

        listViewExpLeft.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                int childCount = parent.getExpandableListAdapter().getChildrenCount(groupPosition);

                if (childCount < 1) {
                    MenuCategory textChild = (MenuCategory) parent.getExpandableListAdapter()
                            .getGroup(groupPosition);
                    moveToProductFragment(textChild.getMenuCategoryId());
                    Log.e("InsideChildClick", "" + textChild.getMenuCategoryId());

                }

                return false;
            }
        });
    }

    private void setExpandableListViewChildClickListener() {
        listViewExpLeft.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {

                MenuSubCategory textChild = (MenuSubCategory) parent.getExpandableListAdapter()
                        .getChild(groupPosition, childPosition);

                moveToProductFragment(textChild.getMenuSubCategoryId());
                Log.e("InsideChildClick", "" + textChild.getMenuSubCategoryId());

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setupToolbar(Context context) {
        //        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setTitle("Al Habib");

//        toolbar.setTitleTextAppearance();
//        toolbar.setLogo(R.drawable.logo_mobile);

//        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),
//                R.drawable.ic_dots_vertical);
//        toolbar.setOverflowIcon(drawable);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        actionbarToggle();
        drawer.addDrawerListener(mDrawerToggle);

        drawerIconLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                    drawerIconLeft.setScaleX(1);
                    drawerIconLeft.setImageResource(R.drawable.ic_arrow_back_black);
                }
            }
        });
        drawerIconRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });
    }

    private void setupSearchView() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(context, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(context, newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void initViews() {
//        toolbar = (Toolbar) findViewById(toolbar);
        drawerIconLeft = (ImageView) findViewById(R.id.drawer_icon_left);
        drawerIconRight = (ImageView) findViewById(R.id.drawer_icon_right);
        logoIcon = (ImageView) findViewById(R.id.logo_icon);
        cartLayout = (RelativeLayout) findViewById(R.id.cart_layout);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        listViewExpLeft = (ExpandableListView) findViewById(R.id.expandable_lv_left);
        listViewExpRight = (ExpandableListView) findViewById(R.id.expandable_lv_right);

        appbarBottom = (RelativeLayout) findViewById(R.id.appbar_bottom);
        myAccountTV = (TextView) findViewById(R.id.my_account_tv);
        cartTV = (TextView) findViewById(R.id.cart_tv);
        discountTV = (TextView) findViewById(R.id.discount_tv);
        homeTV = (TextView) findViewById(R.id.home_tv);

        searchView = (SearchView) findViewById(R.id.search_view);
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        return false;
//        getMenuInflater().inflate(R.menu.main, menu);
//
//        shoppingCart = menu.findItem(R.id.action_cart);
//
//        // Start Adding Notification counter functionality
//        MenuItemCompat.setActionView(shoppingCart, R.layout.actionbar_badge_layout);
//        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(shoppingCart);
//        TextView tv = notifCount.findViewById(R.id.actionbar_notifcation_textview);
//        tv.setText("10");
//        notifCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
////                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
//            }
//        });
//        // End Notification counter functionality
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            Toast.makeText(context, "Cart Clicked", Toast.LENGTH_LONG).show();

            return true;
        }

        if (id == R.id.action_overflow) {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
            return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        drawer.closeDrawer(GravityCompat.END);

        return super.onOptionsItemSelected(item);
    }

    public void changeFragment(int position) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new Fragment();

        switch (position) {
            case 0:
                fragment = new MainFragTest();
                break;
            case 1:
                fragment = new FragPrivacyPolicy();
                break;
            case 2:
                fragment = new FragProduct();
                break;
            case 3:
                fragment = new FragProductDetail();
                break;
            case 4:
                fragment = new FragCartDetail();
                break;
            case 5:
                fragment = new FragCategories();
                break;
            case 101:
                fragment = new FragLogin();
                break;
            case 102:
                fragment = new FragRegister();
                break;
            case 103:
                fragment = new Dashboard();
                break;
            case 104:
                fragment = new FragEditAccount();
                break;
            case 105:
                fragment = new FragChangePassword();
                break;
            case 106:
                fragment = new FragOrderHistory();
                break;
            case 107:
                // Clear Login Session
                clearLoginSession();
                recreate();
                break;
            case 108:
                fragment = new MainFragTest();
//                appbarBottom.setVisibility(View.GONE);
                break;
            case 109:
                fragment = new MainFragTest();
                break;
            case 110:
                fragment = new FragContactUs();
                break;
            case 111:
                fragment = new FragAboutUs();
                break;
            case 112:
                fragment = new FragOrderDetail();
                break;
            case 201:
                fragment = new FragSlider();
                break;
            case 202:
                fragment = new FragForgotPass();
                break;
        }

        transaction.replace(R.id.flFragments, fragment);
        transaction.commit();

    }

    private void initData() {
        headerListRight = new ArrayList<>();
        hashMapRight = new HashMap<>();

        List<MenuSubCategory> menuSubCategories = new ArrayList<>();
        menuSubCategories.add(new MenuSubCategory("1", "About Us"));
        menuSubCategories.add(new MenuSubCategory("2", "Privacy Policy"));

        String[] notLoggedInMenu = {"Login", "Register",
                "Language", "Currency", "Contact Us", "Information..."};
        String[] loggedInMenu = {"Dashboard", "Edit Account", "Change Password", "Order History", "Logout",
                "Language", "Currency", "Contact Us", "Information..."};

        if (isLoggedIn) {
            for (int i = 0; i < loggedInMenu.length; i++) {
                headerListRight.add(loggedInMenu[i]);
                if (i < loggedInMenu.length - 1) {
                    hashMapRight.put(headerListRight.get(i), new ArrayList<MenuSubCategory>());
                } else {
                    hashMapRight.put(headerListRight.get(i), menuSubCategories);
                }
            }
        } else {
            for (int i = 0; i < notLoggedInMenu.length; i++) {
                headerListRight.add(notLoggedInMenu[i]);
                if (i < notLoggedInMenu.length - 1) {
                    hashMapRight.put(headerListRight.get(i), new ArrayList<MenuSubCategory>());
                } else {
                    hashMapRight.put(headerListRight.get(i), menuSubCategories);
                    Log.e("WorkingSubListRight", menuSubCategories.toString());
                }
            }
        }

//        Log.e("HashMapEntry", "Data Entered Successfully \n"
//                + menuSubCategories.get(0).getMenuSubCategoryName());
    }

    private void enableSingleSelection() {
        listViewExpLeft.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    listViewExpLeft.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
    }

    private void loadData() {
        headerListLeft = new ArrayList<>();
        hashMapLeft = new HashMap<>();
        String responseStr = "";
        if (getIntent().hasExtra(KEY_EXTRA)) {
            responseStr = getIntent().getStringExtra(KEY_EXTRA);
            Log.e("ResponseInMainActivity", responseStr);
            try {
                JSONObject responseObject = new JSONObject(responseStr);
                Log.e("JSON_Response", "" + responseObject);
                boolean success = responseObject.optBoolean("success");
                if (success) {
                    JSONObject homeObject = responseObject.getJSONObject("home");

                    JSONArray menuCategories = homeObject.optJSONArray("menu");
                    Log.e("Categories", menuCategories.toString());
                    for (int i = 0; i < menuCategories.length(); i++) {
                        try {
                            JSONObject menuCategoryObj = menuCategories.getJSONObject(i);
                            JSONArray menuSubCategoryArray = menuCategoryObj.optJSONArray(
                                    "children");

                            List<MenuSubCategory> childMenuList = new ArrayList<>();
                            for (int j = 0; j < menuSubCategoryArray.length(); j++) {
                                JSONObject menuSubCategoryObj = menuSubCategoryArray.getJSONObject(j);
                                MenuSubCategory menuSubCategory = new MenuSubCategory(
                                        menuSubCategoryObj.optString("child_id"),
                                        menuSubCategoryObj.optString("name"));
                                childMenuList.add(menuSubCategory);
                            }
                            MenuCategory menuCategory = new MenuCategory(menuCategoryObj.optString(
                                    "category_id"), menuCategoryObj.optString("name"),
                                    menuCategoryObj.optString("icon"), childMenuList);
                            headerListLeft.add(menuCategory);
                            hashMapLeft.put(headerListLeft.get(i), menuCategory.getMenuSubCategory());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    utils.showErrorDialog("Error Getting Data From Server");
                    Log.e("SuccessFalse", "Within getCategories");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONObjEx_MainAct", responseStr);
            }
        } else {
            Log.e("ResponseExMainActivity", responseStr);
            throw new IllegalArgumentException("Activity cannot find  extras " + KEY_EXTRA);
        }
    }

    private void actionbarToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawer,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                drawerIconLeft.setImageResource(R.drawable.ic_list_black);
                drawerIconLeft.setScaleX(-1);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
    }

    private void moveToProductFragment(String id) {

        if (utils.isNetworkConnected()) {

            AppConstants.setMidFixApi("products/category/" + id);
            AndroidNetworking.post(getApiCallUrl())
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.i("LoadingData", "" + "Successful" + response);
                            boolean success = response.optBoolean("success");
                            if (success) {
                                setProductExtra(response.toString());
                                drawer.closeDrawer(GravityCompat.START);
                                changeFragment(2);
                            } else {
                                utils.showErrorDialog("Server Response is False!");
                                Log.e("SuccessFalse", "Within getCategories");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("Invalid request", "Within onError getCategories");
                            Toast.makeText(context, "ErrorGettingDataFromServer", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            utils.showInternetErrorDialog();
        }
    }

    public void switchFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.flFragments, fragment);
        transaction.commit();
    }

    private void setCompoundDrawable() {
        utils.setCompoundDrawable(myAccountTV, "top", R.drawable.ic_more_horiz_black);
        utils.setCompoundDrawable(cartTV, "top", R.drawable.ic_shopping_cart_black);
        utils.setCompoundDrawable(discountTV, "top", R.drawable.ic_tag_black);
        utils.setCompoundDrawable(homeTV, "top", R.drawable.ic_home_black);
    }
}