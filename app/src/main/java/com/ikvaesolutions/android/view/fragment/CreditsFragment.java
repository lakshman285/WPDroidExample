package com.ikvaesolutions.android.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.adapters.CreditsAdapter;
import com.ikvaesolutions.android.models.recyclerviews.CreditsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreditsFragment extends Fragment {

    final String TAG = this.getClass().getSimpleName();
    Context mContext;

    RecyclerView creditsRecyclerView;
    CreditsAdapter creditsAdapter;
    List<CreditsModel> creditsModels;
    LinearLayoutManager mLayoutManager;

    public CreditsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credits, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mContext = getContext();
        creditsRecyclerView = (RecyclerView) view.findViewById(R.id.credits);
        creditsModels = new ArrayList<>();
        creditsAdapter = new CreditsAdapter(mContext, creditsModels, getActivity());
        mLayoutManager = new GridLayoutManager(mContext,1);
        creditsRecyclerView.setLayoutManager(mLayoutManager);
        creditsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        creditsRecyclerView.setAdapter(creditsAdapter);
        addCredits();
    }

    private void addCredits() {

        CreditsModel creditOne = new CreditsModel(
                "AppCompact",
                "For supporting older version of Android.",
                "https://github.com/android/platform_frameworks_support/tree/master/v7/appcompat");
        creditsModels.add(creditOne);

        CreditsModel creditTwo = new CreditsModel(
                "Google Play Services",
                "For Google+ login and FCM push notifications.",
                "https://developers.google.com/android/reference/packages");
        creditsModels.add(creditTwo);

        CreditsModel creditThree = new CreditsModel(
                "Glide",
                "For displaying and caching images.",
                "https://github.com/bumptech/glide");
        creditsModels.add(creditThree);

        CreditsModel creditFour = new CreditsModel(
                "Volley",
                "For handling all API calls.",
                "https://github.com/bumptech/glide");
        creditsModels.add(creditFour);

        CreditsModel creditFive = new CreditsModel(
                "Flaticon.com",
                "Most of the icons we used in this app are downloaded from Flaticon",
                "http://www.flaticon.com/");
        creditsModels.add(creditFive);

        CreditsModel creditSix = new CreditsModel(
                "Card View",
                "For displaying articles, bookmarks and credits in card view",
                "https://github.com/googlesamples/android-CardView");
        creditsModels.add(creditSix);

        CreditsModel credit7 = new CreditsModel(
                "Custom Tabs",
                "For opening all external links within the application",
                "https://github.com/GoogleChrome/custom-tabs-client");
        creditsModels.add(credit7);

        CreditsModel credit8 = new CreditsModel(
                "Skeleton",
                "To show articles loading animation",
                "https://github.com/ethanhua/Skeleton");
        creditsModels.add(credit8);

        CreditsModel credit9 = new CreditsModel(
                "Firebase",
                "For sending notifications",
                "https://github.com/firebase/quickstart-android/tree/master/messaging");
        creditsModels.add(credit9);

        CreditsModel credit10 = new CreditsModel(
                "crashlytics",
                "For crash reporting and other statistics",
                "https://try.crashlytics.com/");
        creditsModels.add(credit10);
        creditsAdapter.notifyDataSetChanged();
    }

}
