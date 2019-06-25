package com.ikvaesolutions.android.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.view.fragment.AboutFragment;

public class AboutActivity extends AppCompatActivity {

    String TAG = this.getClass().getSimpleName();
    Context mContext;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mContext = getApplicationContext();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadHomeFragment(AppConstants.COMING_FROM_ABOUT, new AboutFragment());

    }

    private void loadHomeFragment(String inComingSource, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.INCOMING_SOURCE, inComingSource);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction  = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.about_fragment, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.about_credits:
                Intent aboutCreditsIntent = new Intent(this, CreditsActivity.class);
                startActivity(aboutCreditsIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}


