package com.ikvaesolutions.android.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.ads.consent.AdProvider;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.ikvaesolutions.android.BuildConfig;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.listeners.AdMobConsentInterface;
import com.ikvaesolutions.android.utils.CommonUtils;

import java.util.List;

public class AdMobHelper {

    private Activity activity;
    private Context context;

    private String TAG = AdMobHelper.class.getSimpleName();
    private boolean isConsentCheckCompleted = false;
    private boolean isConsentFetchError = false;
    private boolean isNonPersonalized = false;
    private boolean isAdsLoaded = false;

    private AdMobConsentInterface adMobConsentInterface;

    public AdMobHelper(Activity activity, Context context, AdMobConsentInterface adMobConsentInterface, String TAG) {
        this.TAG = TAG;
        this.activity = activity;
        this.context = context;
        this.adMobConsentInterface = adMobConsentInterface;
    }


    public AdMobHelper(Activity activity, String TAG) {
        this.TAG = TAG;
        this.activity = activity;
    }


    public boolean isConsentCheckCompleted() {
        return isConsentCheckCompleted;
    }

    public void setConsentCheckCompleted(boolean consentCheckCompleted) {
        isConsentCheckCompleted = consentCheckCompleted;
    }

    public boolean isConsentFetchError() {
        return isConsentFetchError;
    }

    public void setConsentFetchError(boolean consentFetchError) {
        isConsentFetchError = consentFetchError;
    }

    public boolean isNonPersonalized() {
        return isNonPersonalized;
    }

    public void setNonPersonalized(boolean nonPersonalized) {
        isNonPersonalized = nonPersonalized;
    }

    public boolean isAdsLoaded() {
        return isAdsLoaded;
    }

    public void setAdsLoaded(boolean adsLoaded) {
        isAdsLoaded = adsLoaded;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }


    public void checkConsentStatus() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(activity);
        String[] publisherIds = {AppConstants.PUB_ID};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus status) {
                // User's consent status successfully updated.

                    if(!CommonUtils.getEUCanShowAds(context)) {
                        setConsentCheckCompleted(true);
                        setAdsLoaded(false);
                    } else {

                        if (status.equals(ConsentStatus.UNKNOWN)) {
                            setConsentCheckCompleted(true);
                            showConsent();
                            CommonUtils.logCustomEvent(getTAG(), "Status", "UNKNOWN");
                        } else if (status.equals(ConsentStatus.NON_PERSONALIZED)) {
                            setNonPersonalized(true);
                            setConsentCheckCompleted(true);
                            adMobConsentInterface.setupAds();
                            setAdsLoaded(true);
                            CommonUtils.logCustomEvent(getTAG(), "Status", "NON_PERSONALIZED");
                        }
                    }
                    CommonUtils.logCustomEvent(getTAG(), "Message", "EU User");

                CommonUtils.logCustomEvent(getTAG(), "Message", "onConsentInfoUpdated");
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
                boolean isFromEU = ConsentInformation.getInstance(getActivity()).isRequestLocationInEeaOrUnknown();
                if(!isFromEU) {
                    adMobConsentInterface.setupAds();
                    setAdsLoaded(true);
                }
                setConsentCheckCompleted(true);
                setConsentFetchError(true);
                CommonUtils.logCustomEvent(getTAG(), "Error", "onFailedToUpdateConsentInfo: "+errorDescription);
            }
        });

    }


    public void showConsent() {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View eu_consent_dialog = inflater.inflate(R.layout.eu_consent, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setView(eu_consent_dialog);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        dialog.show();


        Button btnNonPersonalized = eu_consent_dialog.findViewById(R.id.btn_eu_consent_no);
        Button btnRemoveAds = eu_consent_dialog.findViewById(R.id.btn_eu_consent_remove_ads);
        TextView learnHow = eu_consent_dialog.findViewById(R.id.learn_how);


        btnNonPersonalized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                ConsentInformation.getInstance(getActivity()).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                isNonPersonalized = true;
                adMobConsentInterface.setupAds();
                setAdsLoaded(true);
                CommonUtils.logCustomEvent(getTAG(), "Clicked", "NP 1");
            }
        });

        btnRemoveAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.setEUCanShowAds(false, context);
                CommonUtils.logCustomEvent(getTAG(), "Clicked", "No Ads");
                dialog.cancel();
            }
        });

        learnHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                euMoreInfoDialog(getActivity());
            }
        });

    }


    private void euMoreInfoDialog(Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ScrollView sv = new ScrollView(activity);
        sv.setPadding(40,40,40,40);
        LinearLayout ll = new LinearLayout(activity);
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tv_my_privacy_policy = new TextView(activity);
        String link = "<a href="+AppConstants.PRIVACY_POLICY+">"+activity.getResources().getString(R.string.app_name)+"</a>";
        tv_my_privacy_policy.setText(Html.fromHtml(link));
        tv_my_privacy_policy.setMovementMethod(LinkMovementMethod.getInstance());
        tv_my_privacy_policy.setPadding(40,40,40,40);
        ViewGroup.LayoutParams wdf = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tv_my_privacy_policy.setLayoutParams(wdf);

        TypedValue outValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);


        tv_my_privacy_policy.setBackgroundResource(outValue.resourceId);


        ll.addView(tv_my_privacy_policy, params);

        TextView tv_google_partners = new TextView(activity);
        tv_google_partners.setText(Html.fromHtml("<strong>"+ activity.getResources().getString(R.string.google_partners) +"<strong>"));
        tv_google_partners.setPadding(40,40,40,40);
        tv_google_partners.setTextColor(activity.getResources().getColor(R.color.colorMaterialBlack));
        tv_google_partners.setAllCaps(true);
        ll.addView(tv_google_partners);

        List<AdProvider> adProviders = ConsentInformation.getInstance(activity).getAdProviders();
        for (AdProvider adProvider : adProviders) {
            link = "<a href="+adProvider.getPrivacyPolicyUrlString()+">"+adProvider.getName()+"</a>";
            TextView tv_adprovider = new TextView(activity);
            tv_adprovider.setText(Html.fromHtml(link));
            tv_adprovider.setMovementMethod(LinkMovementMethod.getInstance());
            tv_adprovider.setPadding(40,40,40,20);

            ViewGroup.LayoutParams txtview = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            tv_adprovider.setLayoutParams(txtview);

            tv_adprovider.setBackgroundResource(outValue.resourceId);

            ll.addView(tv_adprovider, params);
        }
        sv.addView(ll);

        builder.setTitle(activity.getResources().getString(R.string.privacy_policy_partners))
                .setView(sv)
                .setPositiveButton(activity.getResources().getString(R.string.close), null);

        final AlertDialog createDialog = builder.create();
        createDialog.show();
        CommonUtils.logCustomEvent(getTAG(), "Showing", "Partners list");
    }

    public AdRequest getRequestBuilder() {

        AdRequest adRequest;

        if(isNonPersonalized()) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
        } else {
            adRequest = new AdRequest.Builder()
                    .build();
        }

        return adRequest;
    }


    public void showAdsChoice() {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View eu_consent_dialog = inflater.inflate(R.layout.eu_consent, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setView(eu_consent_dialog);
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();
        dialog.show();


        Button btnNonPersonalized = eu_consent_dialog.findViewById(R.id.btn_eu_consent_no);
        Button btnRemoveAds = eu_consent_dialog.findViewById(R.id.btn_eu_consent_remove_ads);
        TextView learnHow = eu_consent_dialog.findViewById(R.id.learn_how);


        btnNonPersonalized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                ConsentInformation.getInstance(getActivity()).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                CommonUtils.logCustomEvent(getTAG(), "Clicked", "NP 1");
            }
        });

        btnRemoveAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.setEUCanShowAds(false, context);
                CommonUtils.logCustomEvent(getTAG(), "Clicked", "No Ads");
            }
        });

        learnHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                euMoreInfoDialog(getActivity());
            }
        });

    }

}
