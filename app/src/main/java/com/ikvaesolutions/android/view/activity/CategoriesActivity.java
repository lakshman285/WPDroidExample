package com.ikvaesolutions.android.view.activity;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.listeners.SearchQueryListener;
import com.ikvaesolutions.android.view.fragment.ArticlesFragment;

public class CategoriesActivity extends AppCompatActivity {

    String TAG = this.getClass().getSimpleName();
    Context mContext;
    private Toolbar toolbar;
    SearchView searchView;
    AppBarLayout appBarLayout;
    SearchQueryListener searchQueryListener;
    boolean isCategoriesFrom = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        mContext = getApplicationContext();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appBarLayout = (AppBarLayout) findViewById(R.id.category_appBarLayout);
        String categoryName = getIntent().getStringExtra(AppConstants.CATEGORY_NAME);
        String inComingSource = getIntent().getStringExtra(AppConstants.INCOMING_SOURCE);
        String categoryCount = getIntent().getStringExtra(AppConstants.CATEGORY_COUNT);
        int categoryId = getIntent().getIntExtra(AppConstants.CATEGORY_ID, 0);
        getSupportActionBar().setTitle(categoryName);
        getSupportActionBar().setSubtitle(categoryCount);
        loadHomeFragment(inComingSource, new ArticlesFragment(), categoryId, isCategoriesFrom);
    }

    private void loadHomeFragment(String inComingSource, Fragment fragment, int categoryId, boolean isCategoryFrom) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.INCOMING_SOURCE, inComingSource);
        bundle.putString(AppConstants.CATEGORY, String.valueOf(categoryId));
        bundle.putBoolean(AppConstants.ACTIVITY_SOURCE, isCategoryFrom);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction  = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.category_fragment, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.recent_articles_search_bar, menu);
        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint(mContext.getResources().getString(R.string.search_website));
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
}
