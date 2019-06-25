package com.ikvaesolutions.android.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.google.ads.consent.ConsentInformation;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.helper.AdMobHelper;
import com.ikvaesolutions.android.listeners.AdMobConsentInterface;
import com.ikvaesolutions.android.listeners.SearchQueryListener;
import com.ikvaesolutions.android.utils.CommonUtils;
import com.ikvaesolutions.android.view.fragment.ArticlesFragment;
import android.support.v7.widget.SearchView;
import com.ikvaesolutions.android.view.fragment.CategoriesFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdMobConsentInterface {
    private Context mContext;
    private String TAG = this.getClass().getSimpleName();
    Toolbar toolbar;
    NavigationView navigationView;
    private View navHeader;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    String INCOMING_SOURCE, categoryId;
    boolean isCategory = false;
    boolean isArticleFromCategories = false;
    boolean isHome;
    SearchView searchView;
    SearchQueryListener searchQueryListener;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    Fragment fragment;
    String isFriendsInviteLink = AppConstants.HIDE_VIEW;
    private AdView mAdView;
    Menu nav_Menu;
    AdMobHelper adMobHelper;
    boolean adLoadedBackPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getApplicationContext();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);

        INCOMING_SOURCE = AppConstants.COMING_FROM_RECENT_ARTICLES;
        fragment = new ArticlesFragment();
        isCategory = false;
        isHome = true;
        loadHomeFragment(INCOMING_SOURCE, fragment, isCategory, isArticleFromCategories);

        collapsingToolbarLayout.setTitleEnabled(false);
        setToolbarTitle(mContext.getResources().getString(R.string.app_name));

       nav_Menu = navigationView.getMenu();

        if(AppConstants.BOOKMARKS) {
            nav_Menu.findItem(R.id.nav_bookmarks).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.nav_bookmarks).setVisible(false);
        }

        if(AppConstants.CATEGORIES){
            nav_Menu.findItem(R.id.nav_categories).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.nav_categories).setVisible(false);
        }

        if(AppConstants.ABMOB) {
            loadBannerAd();
        }

    }

    private void loadBannerAd() {
        mAdView = findViewById(R.id.adView);
        adMobHelper = new AdMobHelper(this, mContext, this, TAG);
        boolean isFromEU = ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown();
        if(isFromEU) {
            adMobHelper.checkConsentStatus();
        } else {
            adMobHelper.setAdsLoaded(true);
            loadBottomAd();
        }
    }

    private void setToolbarTitle(String title) {
        toolbar.setTitle(CommonUtils.setCustomFont(title, mContext));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!isHome) {
            INCOMING_SOURCE = AppConstants.COMING_FROM_RECENT_ARTICLES;
            isCategory = false;
            isArticleFromCategories = false;
            isHome = true;
            fragment = new ArticlesFragment();
            setToolbarTitle(mContext.getResources().getString(R.string.app_name));
            setQueryHint(mContext.getResources().getString(R.string.search_website, mContext.getResources().getString(R.string.app_name)));
            loadHomeFragment(INCOMING_SOURCE, fragment, isCategory, isArticleFromCategories);
        } else {

            if(!AppConstants.ABMOB) {
                showBackPressedSnackbar();
            } else {
                if(adLoadedBackPress) {
                    super.onBackPressed();
                } else {
                    showBackPressedSnackbar();
                }
            }

        }
    }

    private void showBackPressedSnackbar() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            CommonUtils.showSnackbar(navigationView, TAG, mContext.getResources().getString(R.string.click_back_again) + " " + mContext.getResources().getString(R.string.app_name));
        }
        mBackPressed = System.currentTimeMillis();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_latest_updates) {
            INCOMING_SOURCE = AppConstants.COMING_FROM_RECENT_ARTICLES;
            isCategory = false;
            isArticleFromCategories = false;
            isHome = true;
            fragment = new ArticlesFragment();
            invalidateOptionsMenu();
            setToolbarTitle(mContext.getResources().getString(R.string.app_name));
            setQueryHint(mContext.getResources().getString(R.string.search_website));
            CommonUtils.logScreenActivity("Articles Activity");
        }  else if (id == R.id.nav_bookmarks) {
            INCOMING_SOURCE = AppConstants.COMING_FROM_BOOKMARKS;
            isCategory = false;
            isArticleFromCategories = false;
            isHome = false;
            fragment = new ArticlesFragment();
            invalidateOptionsMenu();
            setToolbarTitle(AppConstants.BOOKMARKS_TOOLBAR_TITLE);
            setQueryHint(mContext.getResources().getString(R.string.search_bookmarks));
            CommonUtils.logScreenActivity("Bookmarks Activity");
        }  else if(id == R.id.nav_categories) {
            fragment = new CategoriesFragment();
            INCOMING_SOURCE = AppConstants.COMING_FROM_CATEGORIES;
            isCategory = true;
            isArticleFromCategories = true;
            invalidateOptionsMenu();
            setToolbarTitle(AppConstants.CATEGORIES_TOOLBAR_TITLE);
            setQueryHint(mContext.getResources().getString(R.string.search_categoris));
            CommonUtils.logScreenActivity("Categories Activity");
        }  else if (id == R.id.nav_about_us) {
                openAbout();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                CommonUtils.logScreenActivity("About Activity");
                return true;
            } else if (id == R.id.nav_invite_friends) {
                inviteFriends();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                CommonUtils.logCustomEvent("Home Activity", "Clicked", "Invite Friends");
                return true;
            }  else {
                INCOMING_SOURCE = AppConstants.COMING_FROM_RECENT_ARTICLES;
                isCategory = false;
                isArticleFromCategories = false;
                isHome = true;
                fragment = new ArticlesFragment();
                setToolbarTitle(mContext.getResources().getString(R.string.app_name));
                setQueryHint(mContext.getResources().getString(R.string.search_website));
                CommonUtils.logScreenActivity("Articles Activity");
            }

        loadHomeFragment(INCOMING_SOURCE, fragment, isCategory, isArticleFromCategories);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openAbout() {
        startActivity(new Intent(mContext, AboutActivity.class));
    }

    private void inviteFriends() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message) + " " + isFriendsInviteLink);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
    }


    private void loadHomeFragment(String source, Fragment fragment, boolean isCategory, boolean isCategoryFrom) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.INCOMING_SOURCE, source);
        bundle.putBoolean(AppConstants.ACTIVITY_SOURCE, isCategoryFrom);
        if (isCategory) bundle.putString(AppConstants.CATEGORY, categoryId);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction  = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.recent_articles_search_bar, menu);
        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        if(INCOMING_SOURCE.equalsIgnoreCase(AppConstants.COMING_FROM_CATEGORIES)){
            myActionMenuItem.setVisible(false);
        }else {
            myActionMenuItem.setVisible(true);
        }
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint(mContext.getResources().getString(R.string.search_website, "hh"));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                expandToolbar();
                searchQueryListener.sendSearchQuery(s.trim());
                return false;
            }
        });

        return true;
    }

    public void sendQuery(SearchQueryListener searchQueryListener) {
        this.searchQueryListener = searchQueryListener;
    }

    public void setQueryHint(String hint) {
        searchView.setQueryHint(hint);
    }

    public void expandToolbar() {
        appBarLayout.setExpanded(true, true);
    }


    @Override
    protected void onResume() {
        super.onResume();

        try {
            ((ArticlesFragment) fragment).recentArticlesAdapter.notifyDataSetChanged();
        }catch (Exception e) {
//            Log.e(TAG, "Bookmarks Refresh Exception: " + e.getMessage());
        }

        if(AppConstants.ABMOB && adMobHelper.isAdsLoaded()) {
            if (mAdView != null) {
                mAdView.resume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(AppConstants.ABMOB && adMobHelper.isAdsLoaded()) {
            if (mAdView != null) {
                mAdView.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(AppConstants.ABMOB && adMobHelper.isAdsLoaded()) {
            if (mAdView != null) {
                mAdView.destroy();
            }
        }
    }

    @Override
    public void setupAds() {
        mAdView.loadAd(adMobHelper.getRequestBuilder());
        adViewListener();
    }

    private void loadBottomAd() {

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mAdView.loadAd(adRequest);
        adViewListener();
    }

    private void adViewListener() {
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if(!INCOMING_SOURCE.equalsIgnoreCase(AppConstants.COMING_FROM_CATEGORIES)){
                    ((ArticlesFragment) fragment).loadMoreProgressBar.setPadding(0,0,0, (int) CommonUtils.pxFromDp(mContext, (float) 90));
                }else {
                    ((CategoriesFragment) fragment).categoryRecyclerView.setPadding(0,0,0, (int) CommonUtils.pxFromDp(mContext, (float) 90));
                }
                mAdView.setVisibility(View.VISIBLE);
                adLoadedBackPress = true;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {}

            @Override
            public void onAdOpened() {}

            @Override
            public void onAdLeftApplication() {}

            @Override
            public void onAdClosed() {}

        });
    }

    public void inviteFriendsShowingStatus(boolean friendsInviteLinkStatus, String inviteFriendsLink){
        nav_Menu.findItem(R.id.nav_invite_friends).setVisible(friendsInviteLinkStatus);
        isFriendsInviteLink = inviteFriendsLink;
        invalidateOptionsMenu();
    }
}
