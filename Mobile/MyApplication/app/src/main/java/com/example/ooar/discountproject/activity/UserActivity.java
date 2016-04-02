package com.example.ooar.discountproject.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.fragment.ChoisesFragment;
import com.example.ooar.discountproject.fragment.NotificationDetailFragment;
import com.example.ooar.discountproject.fragment.NotificationSettings;
import com.example.ooar.discountproject.fragment.NotificationsFragment;
import com.example.ooar.discountproject.fragment.ProfileFragment;
import com.example.ooar.discountproject.fragment.UserProductList;
import com.example.ooar.discountproject.fragment.UserTabsFragment;
import com.example.ooar.discountproject.util.ErrorHandler;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.ImageCache;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;
import com.google.android.gms.location.LocationClient;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class UserActivity extends AppCompatActivity implements FragmentChangeListener {
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    public static CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    int notificationId = 0;
    boolean isNotificationId = false;
    int check = 0;
    public static UserTabsFragment userTabsFragment;
    public static boolean isOpen = false;//uygulamanın açık olup olmadığını tutan değişken
    public static boolean reload = false;//sayfanın yenılenmek istenipistenmediğini tutan değişken
    public static ImageCache imageCache;

    public UserActivity() {
        userTabsFragment = new UserTabsFragment();
        imageCache = new ImageCache(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        Bundle extras = getIntent().getExtras();
        Util.setProgressDialog(this);
        if (extras != null) {
            notificationId = extras.getInt("notificationId");
        }
        if (notificationId > 0) {
            isNotificationId = true;
        }
        if (isNotificationId) {
            replaceFragment(userTabsFragment, "userTabs");
            Bundle bundle = new Bundle();
            bundle.putInt("notificationId", notificationId);
            Fragment notificationDetailFragment = new NotificationDetailFragment();
            notificationDetailFragment.setArguments(bundle);
            replaceFragment(notificationDetailFragment, "notificationDetail");
        } else {
            replaceFragment(userTabsFragment, "userTabs");//usertabsfragment basılıyor
        }

        mTitle = "Menü";

        mPlanetTitles = new String[]{"Profil", "Bildirim Ayarları", "Alışveriş Listem", "Oturumu Kapat"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                // R.mipmap.ic_launcher,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Menü");
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        Fragment fragment = null;
        check = position;
        switch (position) {
            case 0:
                fragment = new ProfileFragment();
                replaceFragment(fragment, "profileFragment");
                break;
            case 1:
                fragment = new NotificationSettings();
                replaceFragment(fragment, "notificationSettings");
                break;
            case 2:
                fragment = new UserProductList();
                replaceFragment(fragment, "userProductList");
                break;
            case 3:
                closeSession();
                break;
            default:
                break;
        }

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void replaceFragment(Fragment fragment, String tagName) {//fragment değişimlerini yöneten fonksiyon
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if (tagName != null) {
            fragmentTransaction.replace(R.id.userFragments, fragment, tagName).addToBackStack(tagName);
        } else {
            fragmentTransaction.replace(R.id.userFragments, fragment).addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

            switch (tag) {
                case "profileFragment":
                    replaceFragment(userTabsFragment, "userTabs");
                    switch (UserTabsFragment.mTabHost.getCurrentTab()) {
                        case 0:
                            getSupportActionBar().setTitle("Bildirimler");
                            break;
                        case 1:
                            getSupportActionBar().setTitle("Tercihler");
                            break;
                        default:
                            break;
                    }
                    break;
                case "branchFragment":
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("notificationDetail");
                    replaceFragment(fragment, "notificationDetail");
                    getSupportActionBar().setTitle("İndirim Detayları");
                    break;
                case "notificationSettings":
                case "notificationDetail":
                case "userProductList":
                    replaceFragment(userTabsFragment, "userTabs");
                    getSupportActionBar().setTitle("Bildirimler");
                    break;
                case "userTabs":
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startMain);
                    break;
                case "googleMapsFragment":
                    Fragment branchFragment = getSupportFragmentManager().findFragmentByTag("branchFragment");
                    replaceFragment(branchFragment, "branchFragment");
                    break;
            }
        }
    }

    public void closeSession() {//outurum kapatma isteği yapan fonksiyon

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.create().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        builder.setPositiveButton("Oturumu Kapat", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                String tokenKey = getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");

                Callback callback = new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        Util.stopProgressDialog();
                        SharedPreferences.Editor editor = getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
                        editor.remove("phoneNumber");
                        editor.remove("tokenKey");
                        editor.commit();
                        dialog.dismiss();

                        Intent intent = new Intent(UserActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ErrorHandler.handleError(UserActivity.this, error);
                    }
                };
                RetrofitConfiguration.getRetrofitService(true).deleteSession(tokenKey, callback);
            }
        }).setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onStart() {
        isOpen = true;//uygulama açık
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent newIntent) {//activity nin intenti değiştiğinde gerekli fragmente yönlendirme işlemi yapan fonksiyon
        super.onNewIntent(newIntent);
        Bundle extras = newIntent.getExtras();
        if (extras != null) {
            String fragmentName = extras.getString("fragmentName");
            Integer notificationId = extras.getInt("notificationId");
            if (fragmentName != null || notificationId != null) {
                NotificationsFragment.userNotificationList = null;
                if (fragmentName.equals("notificationDetailFragment")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("notificationId", notificationId);
                    Fragment notificationDetailFragment = new NotificationDetailFragment();
                    notificationDetailFragment.setArguments(bundle);
                    replaceFragment(notificationDetailFragment, "notificationDetail");
                } else if (fragmentName.equals("notificationsFragment")) {
                    replaceFragment(userTabsFragment, "userTabs");
                    UserTabsFragment.mTabHost.setCurrentTab(0);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpen = false;//ugulama kapalı
        if (!reload) {//yenileme isteği yoksa cache bosaltılıyo
            removeAllCache();
        }
        reload = false;
    }

    public static void removeAllCache() {//hafızayı bosaltan fonksiyon
        UserTabsFragment.mTabHost = null;
        ChoisesFragment.categoryList = null;
        ChoisesFragment.companyList = null;
        NotificationsFragment.userNotificationList = null;
        ProfileFragment.thisUser = null;
    }

}
