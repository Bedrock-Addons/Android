package com.mcres.octarus;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mcres.octarus.data.AppConfig;
import com.mcres.octarus.data.Constant;
import com.mcres.octarus.data.GDPR;
import com.mcres.octarus.data.SharedPref;
import com.mcres.octarus.data.ThisApp;
import com.mcres.octarus.fragment.FragmentHome;
import com.mcres.octarus.fragment.FragmentSaved;
import com.mcres.octarus.fragment.FragmentTopic;
import com.mcres.octarus.model.Info;
import com.mcres.octarus.model.User;
import com.mcres.octarus.room.AppDatabase;
import com.mcres.octarus.room.DAO;
import com.mcres.octarus.utils.NetworkCheck;
import com.mcres.octarus.utils.Tools;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class ActivityMain extends AppCompatActivity {

    private ActionBar actionBar;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private View notif_badge = null, notif_badge_menu = null;
    private int notification_count = -1;

    private FragmentHome fragmentHome;
    private FragmentTopic fragmentTopic;
    private FragmentSaved fragmentSaved;

    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private boolean dialog_version_show = false;
    private boolean is_login = false;
    private User user = new User();
    private DAO dao;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }

        sharedPref = new SharedPref(this);
        dao = AppDatabase.getDb(this).getDAO();
        ThisApp.get().registerNetworkListener();
        initToolbar();
        initDrawerMenu();
        prepareAds();

        actionBar.setTitle(getString(R.string.title_menu_home));
        loadFragment(new FragmentHome());
        checkAppVersion();
        Tools.RTLMode(getWindow());
        ThisApp.get().saveClassLogEvent(getClass());

        if (false) {
            startActivity(new Intent(this, ActivityIntro.class));
            sharedPref.setFirstLaunch(false);
        }
    }



    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorTextAction), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.changeOverflowMenuIconColor(toolbar, getResources().getColor(R.color.colorTextAction));
        Tools.setSmartSystemBar(this);
    }

    private void initDrawerMenu() {
        NavigationView nav_view = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        TextView name = nav_view.findViewById(R.id.name);
        View update = nav_view.findViewById(R.id.update);
        TextView login_logout = nav_view.findViewById(R.id.login_logout);
        TextView settings = nav_view.findViewById(R.id.settings);
        if (is_login) {
            login_logout.setText(getString(R.string.logout_title));
            name.setText("Logged in as: " + user.name);
        } else {
            login_logout.setText(getString(R.string.title_activity_login));
            name.setText("Create an Account");
        }

        login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLogout();
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_login) {
                    ActivityRegister.navigate(ActivityMain.this, user);
                } else {
                    ActivityLogin.navigate(ActivityMain.this);
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySettings.navigate(ActivityMain.this);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_login) {
                    Tools.showDialogAbout(ActivityMain.this);
                } else {
                    ActivityLogin.navigate(ActivityMain.this);
                }
            }
        });

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                showInterstitial();
            }
        });
    }

    public void onDrawerMenuClick(View view) {
        Fragment fragment = null;
        String title = actionBar.getTitle().toString();
        int menu_id = view.getId();
        switch (menu_id) {
            case R.id.nav_menu_home:
                if (fragmentHome == null) fragmentHome = new FragmentHome();
                fragment = fragmentHome;
                title = getString(R.string.title_menu_home);
                break;
            case R.id.nav_menu_categories:
                if (fragmentTopic == null) fragmentTopic = new FragmentTopic();
                fragment = fragmentTopic;
                title = getString(R.string.title_menu_topic);
                break;
            case R.id.nav_menu_notif:
                ActivityNotification.navigate(this);
                break;
            case R.id.nav_menu_saved:
                if (fragmentSaved == null) fragmentSaved = new FragmentSaved();
                fragment = fragmentSaved;
                title = getString(R.string.title_menu_saved);
                break;
            case R.id.nav_menu_more_app:
                Tools.openInAppBrowser(this, Constant.HELP, false);
                break;
            case R.id.lyt_rate_this:
                Tools.rateAction(this);
                break;
        }
        actionBar.setTitle(title);
        drawer.closeDrawers();
        if (fragment != null) loadFragment(fragment);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.colorTextAction));

        final MenuItem menuItem = menu.findItem(R.id.action_notification);
        View actionView = MenuItemCompat.getActionView(menuItem);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menu_id = item.getItemId();
        if (menu_id == android.R.id.home) {
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
        } else if (menu_id == R.id.action_search) {
            ActivitySearch.navigate(this, null, null);
        } else if (menu_id == R.id.action_notification) {
            ActivityNotification.navigate(this);
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {
        if (notif_badge == null) return;
        notif_badge.setVisibility(notification_count == 0 ? View.INVISIBLE : View.VISIBLE);
        notif_badge_menu.setVisibility(notification_count == 0 ? View.INVISIBLE : View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            doExitApp();
        }
    }

    static boolean active = false;

    @Override
    protected void onResume() {
        super.onResume();
        is_login = ThisApp.get().isLogin();
        user = ThisApp.get().getUser();
        initDrawerMenu();
        int new_notif_count = dao.getNotificationUnreadCount();
        if (new_notif_count != notification_count) {
            notification_count = new_notif_count;
            invalidateOptionsMenu();
        }
        if (mAdView != null) mAdView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdView != null) mAdView.pause();
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.close_sidebar, Toast.LENGTH_LONG).show();
            exitTime = System.currentTimeMillis();
        }
    }

    public void loginLogout() {
        if (is_login) {
            showDialogLogout();
        } else {
            ActivityLogin.navigate(this);
        }
    }

    private void showDialogLogout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.confirmation);
        dialog.setMessage(R.string.logout_confirmation_text);
        dialog.setNegativeButton(R.string.CANCEL, null);
        dialog.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ThisApp.get().logout();
                onResume();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void checkAppVersion() {
        if (dialog_version_show) return;
        Info info = ThisApp.get().getInfo();
        if (info != null && !info.active) {
            dialogOutDate();
        }
    }

    public void dialogOutDate() {
        dialog_version_show = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.update_message);
        builder.setMessage(R.string.msg_app_out_date);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.UPDATE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog_version_show = false;
                dialog.dismiss();
                Tools.updateAction(ActivityMain.this);
                finish();
            }
        });
        builder.setNegativeButton(R.string.CLOSE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog_version_show = false;
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void prepareAds() {
        if (AppConfig.ENABLE_GDPR) GDPR.updateConsentStatus(this); // init GDPR
        if (!AppConfig.ADS_MAIN_ALL || !NetworkCheck.isConnect(getApplicationContext())) return;

        // banner
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(this)).build();
        if (AppConfig.ADS_MAIN_BANNER) mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        // interstitial ads controller
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        if (AppConfig.ADS_MAIN_INTERS) mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // delay for next ads
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        prepareAds();
                    }
                }, 1000 * AppConfig.ADS_INTERS_MAIN_INTERVAL);
                super.onAdClosed();
            }
        });
    }

    // displays the ads
    public void showInterstitial() {
        // Show the ad if it's ready
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

}