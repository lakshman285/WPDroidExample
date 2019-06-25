package com.ikvaesolutions.android.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.ImageView;

import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.app.AppController;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.customviews.FancyAlertDialog;
import com.ikvaesolutions.android.listeners.PopupButtonListener;
import com.ikvaesolutions.android.utils.CommonUtils;

public class SplashScreenActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();
        findviewbyid();
        splashScreenLogic();
        CommonUtils.logScreenActivity("Splash Screen");
    }

    private void init() {
        mContext = this;
        try {
            ((AppCompatActivity) this).getSupportActionBar().hide();
        }catch (Exception e){}
    }

    private void splashScreenLogic() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updatesCheck();
            }
        }, AppConstants.SPLASH_SCREEN_TIME_OUT);
    }

    private void updatesCheck() {
        LoginCheck();
    }



    private void LoginCheck() {
        openHomeActivity();
    }

    private void findviewbyid() {
        logo = (ImageView) findViewById(R.id.splah_screen_logo);
    }

    private void openHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

}
