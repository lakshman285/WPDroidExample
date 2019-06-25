package com.ikvaesolutions.android.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.others.GlideApp;
import com.ikvaesolutions.android.utils.CommonUtils;

public class NotificationsActivity extends AppCompatActivity  {

    String TAG = this.getClass().getSimpleName();
    TextView message, textViewdescription;
    ImageView featuredImage;
    Button link;
    Intent i;
    String imageUrl, destinationUrl, title, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        try {

            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(null);

            message = (TextView) findViewById(R.id.textviewDetails);
            textViewdescription = (TextView) findViewById(R.id.textviewMessage);
            featuredImage = (ImageView) findViewById(R.id.imageViewFeaturedImage);
            link = (Button) findViewById(R.id.buttonLink);

            i = getIntent();

            String notificationType = i.getStringExtra(AppConstants.NOTIFICATION_TYPE);
            imageUrl = i.getStringExtra(AppConstants.NOTIFICATION_IMAGE);
            destinationUrl = i.getStringExtra(AppConstants.NOTIFICATION_POST_URL);
            title = i.getStringExtra(AppConstants.NOTIFICATION_TITLE);
            description = i.getStringExtra(AppConstants.NOTIFICATION_MESSAGE);

            GlideApp.with(this).load(imageUrl).placeholder(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.empty_photo)).into(featuredImage);

            switch (notificationType) {
                case AppConstants.NOTIFICATION_TYPE_ANNOUNCEMENT:
                    handleAnnouncement();
                    return;
                case AppConstants.NOTIFICATION_TYPE_APP_UPDATE:
                    handleAppUpdate();
                    return;
                default:
            }

        }catch (Exception e) {
            CommonUtils.writeLog(TAG, AppConstants.ERROR_LOG, e.toString());
            finish();
        }
    }

    private void handleAppUpdate() {
//        getSupportActionBar().setTitle("App Update");
        message.setText("You can update the app directly from play store");
        link.setText("Open Play store");
        textViewdescription.setText(description);
       // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(destinationUrl)));

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(destinationUrl)));
                finish();
            }
        });
    }

    private void handleAnnouncement() {
        message.setText(title);
        textViewdescription.setText(description);
      //  CommonUtils.openChromeCustomTab(this, Uri.parse(destinationUrl));
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.openChromeCustomTab(NotificationsActivity.this, Uri.parse(destinationUrl));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                CommonUtils.writeLog(AppConstants.ERROR_LOG, TAG, " Unknown Menu Item " + menuItem.getItemId() + " Clicked ");
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        finish();
        super.onBackPressed();
    }
}
