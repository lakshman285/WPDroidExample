package com.ikvaesolutions.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ikvaesolutions.android.BuildConfig;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.app.AppController;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.helper.CustomTabsHelper;
import com.ikvaesolutions.android.others.TypeFace;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by amarilindra on 08/04/17.
 */

public class CommonUtils {

    public CommonUtils() {
    }

    public static void writeLog(String type, String TAG, String msg) {
        switch (type) {
            case AppConstants.DEBUG_LOG:
                Log.d(TAG,msg);
                break;
            case AppConstants.ERROR_LOG:
                Log.e(TAG,msg);
                break;
            case AppConstants.INFO_LOG:
                Log.i(TAG,msg);
                break;
        }
    }

    public static boolean isNetworkAvailable(Context context, String TAG) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }catch (Exception e) {
            writeLog(AppConstants.ERROR_LOG, TAG, e.getMessage());
            return false;
        }
    }

    public static void showSnackbar(View v, String TAG, String message) {
        try{
            Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
        }catch (Exception e) {
            writeLog(AppConstants.ERROR_LOG, TAG, e.getMessage());
        }
    }

    public static String getPackageName() {
        return BuildConfig.APPLICATION_ID;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static SpannableString setCustomFont(String text, Context context) {
        SpannableString title = new SpannableString(text);
        title.setSpan(new TypeFace(context, "latobold.ttf"), 0, title.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return title;
    }

    public static String getFacebookPageURL(Context context, String FACEBOOK_URL, String FACEBOOK_PAGE_ID) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else {
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL;
        }
    }

    public static void openChromeCustomTab(Activity activity, Uri uri) {
        String packageName = CustomTabsHelper.getPackageNameToUse(activity);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        builder.setSecondaryToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimaryLight));
        builder.addDefaultShareMenuItem();
        builder.enableUrlBarHiding();
        builder.setInstantAppsEnabled(true);
        if ( packageName != null ) {
            builder.build().intent.setPackage(packageName);
        }
        builder.build().launchUrl(activity, uri);
    }

    public static void logCustomEvent(String category, String label, String action) {
        if(AppConstants.ANALYTICS) {
            AppController.getDefaultTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setLabel(label)
                    .setAction(action)
                    .build());
        }
    }
    public static void logScreenActivity(String screenName) {
        if(AppConstants.ANALYTICS) {
            Tracker t = AppController.getDefaultTracker();
            t.setScreenName(screenName);
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public static void setEUCanShowAds(boolean status, Context deniedContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(deniedContext);
        prefs.edit().putBoolean("canShowAds", status).apply();
    }

    public static boolean getEUCanShowAds(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("canShowAds", true);
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static String getPostsLoopLimit(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("postsLoopLimit", "5");
    }


    public static void setPostsLoopLimit(String limit, Context deniedContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(deniedContext);
        prefs.edit().putString("postsLoopLimit", limit).apply();
    }


    public static String getAboutResponseMessage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("bookmarksStyle", "");
    }

    public static void setAboutResponseMessage(String aboutResponseMessage, Context deniedContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(deniedContext);
        prefs.edit().putString("bookmarksStyle", aboutResponseMessage).apply();
    }

    public static String getCommentPersonName(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("commentPersonName", "");
    }

    public static void setCommentPersonName(String commentPersonName, Context deniedContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(deniedContext);
        prefs.edit().putString("commentPersonName", commentPersonName).apply();
    }

    public static String getCommentPersonEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("commentPersonEmail", "");
    }

    public static void setCommentPersonEmail(String commentPersonEmail, Context deniedContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(deniedContext);
        prefs.edit().putString("commentPersonEmail", commentPersonEmail).apply();
    }

    public static int getLoadingLayout(String style) {
        switch (style) {
            case AppConstants.LAYOUT_STYLE_TOP_IMAGE:
                return R.layout.loading_top_image;
            case AppConstants.LAYOUT_STYLE_LEFT_IMAGE:
                return R.layout.loading_right_image_new;
            default:
                return R.layout.loading_top_image;
        }
    }


    public static void openChromeDetais(Context mContext, String readMoreUrl) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(readMoreUrl)));
    }

    public static void openGmailDetails(Context mContext, String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",email, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Re:subject");
        intent.putExtra(Intent.EXTRA_TEXT, "Text");
        mContext.startActivity(Intent.createChooser(intent, "Create Choosing Email"));
    }
}
