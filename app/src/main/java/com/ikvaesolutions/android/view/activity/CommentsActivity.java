package com.ikvaesolutions.android.view.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.utils.CommonUtils;
import com.ikvaesolutions.android.view.fragment.CommentsFragment;

public class CommentsActivity extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mContext = getApplicationContext();

        getSupportActionBar().setTitle(CommonUtils.setCustomFont(getString(R.string.comments), mContext));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Fragment fragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.BUNDLE_POST_ID, String.valueOf(getIntent().getIntExtra(AppConstants.BUNDLE_POST_ID,0)));
        bundle.putString(AppConstants.BUNDLE_COMMENT_STATUS, getIntent().getStringExtra(AppConstants.BUNDLE_COMMENT_STATUS));
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.framenew, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
