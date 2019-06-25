package com.ikvaesolutions.android.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.adapters.CategoriesAdapter;
import com.ikvaesolutions.android.constants.ApiConstants;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.listeners.VolleyJSONArrayRequestListener;
import com.ikvaesolutions.android.models.recyclerviews.CategoriesModel;
import com.ikvaesolutions.android.utils.CommonUtils;
import com.ikvaesolutions.android.utils.VolleyUtils;
import com.ikvaesolutions.android.view.activity.HomeActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    final String TAG = this.getClass().getSimpleName();
    Context mContext;
    public RecyclerView categoryRecyclerView;
    RelativeLayout noInternetConnectionRelativeLayout;
    Button tryAgainButton;
    TextView noInternetMessageTextView;
    AppCompatImageView noInternetMessageImageView;
    public CategoriesAdapter categoriesAdapter;
    List<CategoriesModel> categoriesModelList;
    LinearLayoutManager mLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    SkeletonScreen skeletonScreen;
    boolean skeletonShown = false;
    boolean skeletonHidden = false;
    String postsLoopLimit, categoryFinalCount;
    int skeletonLayout = R.layout.item_categories_content;

    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_categories, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mContext = getContext();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.colorPrimary), mContext.getResources().getColor(R.color.colorPrimaryDark));

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        noInternetConnectionRelativeLayout = view.findViewById(R.id.category_no_internet_layout);
        tryAgainButton = view.findViewById(R.id.category_no_internet_try_again_button);
        noInternetMessageTextView = view.findViewById(R.id.category_no_internet_message);
        noInternetMessageImageView = view.findViewById(R.id.category_no_internet_image);

        categoriesModelList = new ArrayList<>();

        categoriesAdapter = new CategoriesAdapter(mContext, categoriesModelList);
        mLayoutManager = new GridLayoutManager(mContext,1);
        categoryRecyclerView.setLayoutManager(mLayoutManager);
        categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        categoryRecyclerView.setAdapter(categoriesAdapter);

        postsLoopLimit = CommonUtils.getPostsLoopLimit(mContext);

        swipeRefreshLayout.post(new Runnable() {
            @Override
                public void run() {
                    addRecyclerViewLayout();
            }
        });

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTryAgain();
            }
        });
    }

    private void handleTryAgain() {
        addRecyclerViewLayout();
    }

    private void addRecyclerViewLayout() {
        showSwipeDownToRefresh();
        skeletonLayout = R.layout.loading_full_categories_content;
        if(!categoriesModelList.isEmpty()) {
            categoriesModelList.clear();
            categoriesAdapter.notifyDataSetChanged();
        }
        if(categoriesModelList.isEmpty()) loadingArticlesMessage();
        else hideNoInternetConnectionMessage();

        Log.e(TAG, ApiConstants.GET_CATEGORY_ARTICLES);
        VolleyUtils.makeJSONArrayRequest(null, ApiConstants.GET_CATEGORY_ARTICLES, AppConstants.METHOD_GET, AppConstants.CATEGORIES_REQUEST_TYPE, new VolleyJSONArrayRequestListener() {
            @Override
            public void onError(String error, String statusCode) {
                showNoInternetConnectionMessage();
                hideSwiperDownToRefresh();
            }
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject categoryObject = (JSONObject) response.get(i);
                        int categoryId = categoryObject.getInt(ApiConstants.KEY_CATEGORY_ID);
                        int categoryCount = categoryObject.getInt(ApiConstants.KEY_CATEGORY_COUNT);
                        if(categoryCount == 1){
                            categoryFinalCount = String.valueOf(categoryCount) + " " + mContext.getResources().getString(R.string.article);
                        }else {
                            categoryFinalCount = String.valueOf(categoryCount) + " " + mContext.getResources().getString(R.string.articles);
                        }
                        String categoryName = categoryObject.getString(ApiConstants.KEY_CATEGORY_NAME);
                        JSONObject wpDroidObject = categoryObject.getJSONObject(ApiConstants.WP_DROID_OBJECT);
                        boolean excludeCategory = wpDroidObject.getBoolean(ApiConstants.KEY_EXCLUDE_CATEGORY);
                        if(!excludeCategory){
                            CategoriesModel categoriesModel = new CategoriesModel(categoryId, categoryFinalCount , categoryName);
                            categoriesModelList.add(categoriesModel);
                            categoriesAdapter.notifyItemInserted(categoriesModelList.size());
                        }

                    } catch (JSONException e) {
                        Log.e("Error", e.getMessage());
                        CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
                        showNoInternetConnectionMessage();
                    }
                    hideSkeleton();
                    hideSwiperDownToRefresh();

                }

            }
        });
    }

    private void loadingArticlesMessage() {
        categoryRecyclerView.setVisibility(View.VISIBLE);
        noInternetConnectionRelativeLayout.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
        showSkeleton();
    }

    private void hideNoInternetConnectionMessage() {
        categoryRecyclerView.setVisibility(View.VISIBLE);
        noInternetConnectionRelativeLayout.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
    }

    private void showSkeleton() {
        if(!skeletonShown) {
            skeletonScreen = Skeleton.bind(categoryRecyclerView)
                    .adapter(categoriesAdapter)
                    .load(skeletonLayout)
                    .count(Integer.valueOf(postsLoopLimit))
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

    private void hideSwiperDownToRefresh() {
        if(skeletonHidden) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void showNoInternetConnectionMessage() {
        categoryRecyclerView.setVisibility(View.GONE);
        noInternetConnectionRelativeLayout.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.VISIBLE);
        noInternetMessageTextView.setText(mContext.getResources().getString(R.string.no_internet_connection_message));
        noInternetMessageImageView.setImageResource(R.drawable.ic_img_no_connection);
        ((HomeActivity) mContext).expandToolbar();//TODO
    }

    @Override
    public void onRefresh() {
        skeletonShown = false;
        skeletonHidden = false;
        addRecyclerViewLayout();
    }
}
