package com.ikvaesolutions.android.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.constants.ApiConstants;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.listeners.VolleyJSONObjectRequestListener;
import com.ikvaesolutions.android.others.GlideApp;
import com.ikvaesolutions.android.utils.CommonUtils;
import com.ikvaesolutions.android.utils.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    final String TAG = this.getClass().getSimpleName();

    Context mContext;
    AppCompatImageView facebook, googlePlus, twitter, linkedin, youtube, instagram, aboutEmailImage, aboutPhoneImage;
    TextView websiteDescription, aboutEmailText, aboutPhoneText;
    Button readMoreAboutWebsite;
    ImageView websiteImage;
    RelativeLayout websiteContactEmail, websiteContactPhone;
    LinearLayout aboutRootLayout, getInTouch;

    RelativeLayout noInternetConnectionRelativeLayout;
    Button tryAgainButton;
    TextView noInternetMessageTextView;
    AppCompatImageView noInternetMessageImageView;

    SwipeRefreshLayout swipeRefreshLayout;
    SkeletonScreen skeletonScreen;
    boolean skeletonShown = false;
    boolean skeletonHidden = false;

    String postsLoopLimit;


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_details, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mContext = getContext();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.colorPrimary), mContext.getResources().getColor(R.color.colorPrimaryDark));

        noInternetConnectionRelativeLayout = (RelativeLayout)view.findViewById(R.id.about_no_internet_layout);
        tryAgainButton = (Button)view.findViewById(R.id.about_no_internet_try_again_button);
        noInternetMessageImageView = (AppCompatImageView)view.findViewById(R.id.about_no_internet_image);
        noInternetMessageTextView = (TextView)view.findViewById(R.id.about_no_internet_message);

        aboutRootLayout = (LinearLayout) view.findViewById(R.id.about_root_layout);
        getInTouch = (LinearLayout)view.findViewById(R.id.getintouch);
        aboutEmailText = (TextView)view.findViewById(R.id.about_email_text);
        aboutPhoneText = (TextView)view.findViewById(R.id.about_phone_text);
        websiteImage = (ImageView)view.findViewById(R.id.website_logo);
        websiteDescription = (TextView)view.findViewById(R.id.website_description);
        readMoreAboutWebsite = (Button)view.findViewById(R.id.website_read_more_button);
        websiteContactEmail = (RelativeLayout)view.findViewById(R.id.website_contact_email);
        websiteContactPhone = (RelativeLayout)view.findViewById(R.id.website_contact_phone);

        aboutEmailImage = (AppCompatImageView)view.findViewById(R.id.about_email_image);
        aboutPhoneImage = (AppCompatImageView)view.findViewById(R.id.about_phone_image);

        facebook = (AppCompatImageView) view.findViewById(R.id.socialicon_facebook);
        googlePlus = (AppCompatImageView) view.findViewById(R.id.socialicon_googleplus);
        twitter = (AppCompatImageView) view.findViewById(R.id.socialicon_twitter);
        linkedin = (AppCompatImageView) view.findViewById(R.id.socialicon_linkedin);
        youtube = (AppCompatImageView) view.findViewById(R.id.socialicon_youtube);
        instagram = (AppCompatImageView) view.findViewById(R.id.socialicon_instagram);
        postsLoopLimit = CommonUtils.getPostsLoopLimit(mContext);

        showSkeleton();

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                addAboutDetails();
            }
        });

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTryAgain();
            }
        });

    }

    private void addAboutDetails() {

        showSwipeDownToRefresh();

        VolleyUtils.makeJSONObjectRequest(null, ApiConstants.GET_ABOUT_WEBSITE, AppConstants.METHOD_GET, AppConstants.ABOUT_REQUEST_TYPE, false, new VolleyJSONObjectRequestListener() {
            @Override
            public void onError(String error, String statusCode) {
                if(CommonUtils.getAboutResponseMessage(mContext).isEmpty()){
                    showNoInternetConnectionMessage();
                    hideSwipeDownToRefresh();
                    hideSkeleton();
                }else {
                    try {
                        JSONObject responseObject = new JSONObject(CommonUtils.getAboutResponseMessage(mContext));
                        checkingResponseDetails(responseObject);
                        loadingAboutMessage();
                        hideSwipeDownToRefresh();
                        hideSkeleton();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponse(JSONObject response) {
                loadingAboutMessage();
                if(!response.toString().equals(CommonUtils.getAboutResponseMessage(mContext))){
                    CommonUtils.setAboutResponseMessage(response.toString(), mContext);
                }
                checkingResponseDetails(response);
                hideSkeleton();
                hideSwipeDownToRefresh();
            }
        });

    }

    private void checkingResponseDetails(final JSONObject response) {

        try {
            if(response.has(ApiConstants.KEY_ABOUT)){
                websiteDescription.setVisibility(View.VISIBLE);
                websiteDescription.setText(response.getString(ApiConstants.KEY_ABOUT));
            }else {
                websiteDescription.setVisibility(View.GONE);
            }
            if(response.has(ApiConstants.KEY_IMAGE)){
                websiteImage.setVisibility(View.VISIBLE);
                GlideApp.with(mContext)
                        .load(response.getString(ApiConstants.KEY_IMAGE))
                        .into(websiteImage);
            }else {
                websiteImage.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.app_icon));
            }

            if(response.has(ApiConstants.KEY_READ_MORE)){
                readMoreAboutWebsite.setVisibility(View.VISIBLE);
                readMoreAboutWebsite.setText(R.string.read_more);
                readMoreAboutWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            CommonUtils.openChromeDetais(mContext, response.getString(ApiConstants.KEY_READ_MORE));
                        } catch (JSONException e) {
                            Log.e("Error", e.getMessage());
                            CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());                        }
                    }
                });
            }else {
                readMoreAboutWebsite.setVisibility(View.GONE);
            }

            if(response.has(ApiConstants.KEY_EMAIL)){
                websiteContactEmail.setVisibility(View.VISIBLE);
                aboutEmailImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_gmail));
                aboutEmailText.setText(response.getString(ApiConstants.KEY_EMAIL));
                websiteContactEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            CommonUtils.openGmailDetails(mContext, response.getString(ApiConstants.KEY_EMAIL));
                        } catch (JSONException e) {
                            Log.e("Error", e.getMessage());
                            CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());                         }
                    }
                });
            }else {
                websiteContactEmail.setVisibility(View.GONE);
            }

            if(response.has(ApiConstants.KEY_PHONE)){
                websiteContactPhone.setVisibility(View.VISIBLE);
                aboutPhoneImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_phone));
                aboutPhoneText.setText(response.getString(ApiConstants.KEY_PHONE));
                websiteContactPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            CommonUtils.openChromeDetais(mContext, response.getString(ApiConstants.KEY_PHONE));
                        } catch (JSONException e) {
                            Log.e("Error", e.getMessage());
                            CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
                        }
                    }
                });
            }else {
                websiteContactPhone.setVisibility(View.GONE);
            }

            if(response.has(ApiConstants.KEY_FACEBOOK)){
                    facebook.setVisibility(View.VISIBLE);
                    facebook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                CommonUtils.openChromeDetais(mContext, response.getString(ApiConstants.KEY_FACEBOOK));
                            } catch (JSONException e) {
                                Log.e("Error", e.getMessage());
                                CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
                            }                        }
                    });
            }else {
                facebook.setVisibility(View.GONE);
            }
            if(response.has(ApiConstants.KEY_GOOGLE_PLUS)){
                    googlePlus.setVisibility(View.VISIBLE);
                    googlePlus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                CommonUtils.openChromeDetais(mContext, response.getString(ApiConstants.KEY_GOOGLE_PLUS));
                            } catch (JSONException e) {
                                Log.e("Error", e.getMessage());
                                CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
                            }                           }
                    });
            }else {
                googlePlus.setVisibility(View.GONE);
            }
            if(response.has(ApiConstants.KEY_TWITTER)){
                    twitter.setVisibility(View.VISIBLE);
                    twitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                CommonUtils.openChromeDetais(mContext, response.getString(ApiConstants.KEY_TWITTER));
                            } catch (JSONException e) {
                                Log.e("Error", e.getMessage());
                                CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
                            }                           }
                    });
            }else {
                twitter.setVisibility(View.GONE);
            }
            if(response.has(ApiConstants.KEY_LINKEDIN)){
                    linkedin.setVisibility(View.VISIBLE);
                    linkedin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                CommonUtils.openChromeDetais(mContext, response.getString(ApiConstants.KEY_LINKEDIN));
                            } catch (JSONException e) {
                                Log.e("Error", e.getMessage());
                                CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
                            }
                        }
                    });
            }else {
                linkedin.setVisibility(View.GONE);
            }
            if(response.has(ApiConstants.KEY_YOUTUBE)){
                youtube.setVisibility(View.VISIBLE);
                {
                    youtube.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                CommonUtils.openChromeDetais(mContext, response.getString(ApiConstants.KEY_YOUTUBE));
                            } catch (JSONException e) {
                                Log.e("Error", e.getMessage());
                                CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
                            }                           }
                    });
                }
            }else {
                youtube.setVisibility(View.GONE);
            }
            if(response.has(ApiConstants.KEY_INSTAGRAM)){
                    instagram.setVisibility(View.VISIBLE);
                    instagram.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                CommonUtils.openChromeDetais(mContext, response.getString(ApiConstants.KEY_INSTAGRAM));
                            } catch (JSONException e) {
                                Log.e("Error", e.getMessage());
                                CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
                            }                           }
                    });
            }else {
                instagram.setVisibility(View.GONE);
            }

            if(response.has(ApiConstants.KEY_FACEBOOK)){
                getInTouch.setVisibility(View.VISIBLE);
            }else if(response.has(ApiConstants.KEY_GOOGLE_PLUS)){
                getInTouch.setVisibility(View.VISIBLE);
            }else if(response.has(ApiConstants.KEY_TWITTER)){
                getInTouch.setVisibility(View.VISIBLE);
            }else if(response.has(ApiConstants.KEY_LINKEDIN)){
                getInTouch.setVisibility(View.VISIBLE);
            }else if(response.has(ApiConstants.KEY_YOUTUBE)){
                getInTouch.setVisibility(View.VISIBLE);
            }else if(response.has(ApiConstants.KEY_INSTAGRAM)){
                getInTouch.setVisibility(View.VISIBLE);
            }else {
                getInTouch.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
            CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
            showNoInternetConnectionMessage();
        }

    }

    private void handleTryAgain() {
        skeletonHidden = false;
        addAboutDetails();
    }

    private void loadingAboutMessage() {
        aboutRootLayout.setVisibility(View.VISIBLE);
        noInternetConnectionRelativeLayout.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
    }

    private void showSkeleton() {
        if(!skeletonShown) {
            skeletonScreen = Skeleton.bind(aboutRootLayout)
                    .load(R.layout.loading_about_full_content)
                    .show();
            skeletonShown = true;
        }
    }

    private void hideSkeleton() {
        if(!skeletonHidden) {
            skeletonScreen.hide();
            skeletonHidden = true;
        }
    }

    private void showSwipeDownToRefresh() {
        if(skeletonHidden) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    private void hideSwipeDownToRefresh() {
        if(skeletonHidden) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void showNoInternetConnectionMessage() {
        aboutRootLayout.setVisibility(View.GONE);
        noInternetConnectionRelativeLayout.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.VISIBLE);
        noInternetMessageTextView.setText(mContext.getResources().getString(R.string.no_internet_connection_message));
        noInternetMessageImageView.setImageResource(R.drawable.ic_img_no_connection);
    }

    @Override
    public void onRefresh() {
        skeletonShown = false;
        skeletonHidden = false;
        addAboutDetails();
    }
}
