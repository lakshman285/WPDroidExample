package com.ikvaesolutions.android.view.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.ikvaesolutions.android.BuildConfig;
import com.ikvaesolutions.android.R;
import android.support.v4.widget.SwipeRefreshLayout;
import com.ikvaesolutions.android.adapters.RecentArticlesAdapter;
import com.ikvaesolutions.android.app.AppController;
import com.ikvaesolutions.android.constants.ApiConstants;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.listeners.SearchQueryListener;
import com.ikvaesolutions.android.listeners.VolleyJSONArrayRequestListener;
import com.ikvaesolutions.android.models.recyclerviews.RecentArticlesModel;
import com.ikvaesolutions.android.models.sqlite.BookmarksModel;
import com.ikvaesolutions.android.utils.CommonUtils;
import com.ikvaesolutions.android.utils.DatabaseHandlerUtils;
import com.ikvaesolutions.android.utils.VolleyUtils;
import com.ikvaesolutions.android.view.activity.CategoriesActivity;
import com.ikvaesolutions.android.view.activity.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticlesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    final String TAG = this.getClass().getSimpleName();
    Context mContext;
    Activity mActivity;
    Bundle bundle;

    public RecyclerView recentArticleRecyclerView;
    public RecentArticlesAdapter recentArticlesAdapter;
    List<RecentArticlesModel> recentArticlesModels;
    List<BookmarksModel> searchBookmarks;
    List<BookmarksModel> bookmarksModel;

    RelativeLayout noInternetConnectionRelativeLayout;
    LinearLayoutManager mLayoutManager;

    SwipeRefreshLayout swipeRefreshLayout;

    AppCompatImageView messageImageView;
    TextView messageTextView;
    Button tryAgainButton;

    public ProgressBar loadMoreProgressBar;

    int offset = 0;
    private boolean userScrolled = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    String realIncomingSource, layoutStyle;
    String incomingSource = AppConstants.COMING_FROM_RECENT_ARTICLES;
    String categoryId;
    String apiCallURL;
    String searchQuery;

    boolean isArticleComingFromCategories;
    boolean isInviteFriendsExistsHandler = false;
    boolean isInviteFriendsNotExistsHandler = false;

    private ArrayList<Integer> articlesShown = new ArrayList<>();
    private ArrayList<String> bookmarkedArticlesTitles = new ArrayList<>();

    DatabaseHandlerUtils databaseHandlerUtils;

    SkeletonScreen skeletonScreen;
    boolean skeletonShown = false;
    boolean skeletonHidden = false;
    boolean initialSearchCompleted = false;

    int excludedPostsCount = 0;
    String postsLoopLimit;
    String loopLimit;
    int skeletonLayout = R.layout.loading_top_image;

    public ArticlesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mContext = getContext();
        mActivity = getActivity();

        bundle = getArguments();
        incomingSource = bundle.getString(AppConstants.INCOMING_SOURCE);
        isArticleComingFromCategories = bundle.getBoolean(AppConstants.ACTIVITY_SOURCE);

        categoryId = bundle.getString(AppConstants.CATEGORY);

        if(null == incomingSource) incomingSource = AppConstants.COMING_FROM_RECENT_ARTICLES;
        realIncomingSource = incomingSource;

        apiCallURL = apiCallUrl(incomingSource);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.colorPrimary), mContext.getResources().getColor(R.color.colorPrimaryDark));

        recentArticleRecyclerView = (RecyclerView) view.findViewById(R.id.recentArticles);
        noInternetConnectionRelativeLayout = (RelativeLayout) view.findViewById(R.id.category_no_internet_layout);
        loadMoreProgressBar = (ProgressBar) view.findViewById(R.id.loadMore);
        tryAgainButton = (Button) view.findViewById(R.id.category_no_internet_try_again_button);
        messageImageView = (AppCompatImageView) view.findViewById(R.id.category_no_internet_image);
        messageTextView = (TextView) view.findViewById(R.id.category_no_internet_message);

        postsLoopLimit = CommonUtils.getPostsLoopLimit(mContext);

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTryAgain();
            }
        });
        hideProgressbar();

        databaseHandlerUtils = new DatabaseHandlerUtils(mContext);
        recentArticlesModels = new ArrayList<>();
        recentArticlesAdapter = new RecentArticlesAdapter(mContext, recentArticlesModels, incomingSource, ArticlesFragment.this, databaseHandlerUtils);

        mLayoutManager = new GridLayoutManager(mContext,1);
        recentArticleRecyclerView.setLayoutManager(mLayoutManager);
        recentArticleRecyclerView.setItemAnimator(new DefaultItemAnimator());
        recentArticleRecyclerView.setAdapter(recentArticlesAdapter);

        swipeRefreshLayout.post(new Runnable() {
            @Override
                public void run() {
                        addRecyclerViewLayout();
            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(loadMoreProgressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            loadMoreProgressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            loadMoreProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }
            getSearchQuery(isArticleComingFromCategories);
    }
    private String apiCallUrl(String incomingSource) {
        if(incomingSource.equals(AppConstants.COMING_FROM_RECENT_ARTICLES)) {
            return ApiConstants.GET_RECENT_POSTS;
        } else if (incomingSource.equals(AppConstants.COMING_FROM_CATEGORIES)) {
            return ApiConstants.GET_CATEGORY_WISE_POSTS + categoryId;
        } else {
            return ApiConstants.GET_RECENT_POSTS;
        }
    }

    private void getSearchQuery(boolean isArticleFromCategories) {   //TODO
        if(isArticleFromCategories){
            ((CategoriesActivity) getActivity()).sendQuery(new SearchQueryListener() {
                @Override
                public void sendSearchQuery(String query) {
                    if(!query.equals("")) {
                        AppController.getInstance().cancelPendingRequests(AppConstants.SEARCH_REQUEST_TYPE);
                        apiCallURL = getApiCallUrl(realIncomingSource, query);
                        incomingSource = AppConstants.COMING_FROM_SEARCH;
                        searchQuery = query;
                        if(realIncomingSource.equals(AppConstants.COMING_FROM_BOOKMARKS)) {
                            clearLists();
                            searchBookmarks(query);
                        } else {
                            skeletonHidden = false;
                            skeletonShown = false;
                            if(initialSearchCompleted) {
                                clearLists();
                                showRecentArticles(recentArticlesModels.size(), apiCallURL, AppConstants.SEARCH_REQUEST_TYPE);
                            }
                            initialSearchCompleted = true;
                        }
                    } else {
                        incomingSource = realIncomingSource;
                        apiCallURL = apiCallUrl(incomingSource);
                        ((CategoriesActivity) mContext).setQueryHint(getSearchQueryHint(incomingSource));
                        if(realIncomingSource.equals(AppConstants.COMING_FROM_BOOKMARKS)) {
                            clearLists();
                            showBookmarks();
                        } else {
                            skeletonHidden = false;
                            skeletonShown = false;
                            if(initialSearchCompleted) {
                                clearLists();
                                showRecentArticles(recentArticlesModels.size(), apiCallURL, incomingSource);
                            }
                            initialSearchCompleted = false;
                        }
                    }
                }
            });
        }else {
            ((HomeActivity) getActivity()).sendQuery(new SearchQueryListener() {
                @Override
                public void sendSearchQuery(String query) {
                    if(!query.equals("")) {
                        AppController.getInstance().cancelPendingRequests(AppConstants.SEARCH_REQUEST_TYPE);
                        apiCallURL = getApiCallUrl(realIncomingSource, query);
                        incomingSource = AppConstants.COMING_FROM_SEARCH;
                        searchQuery = query;
                        if(realIncomingSource.equals(AppConstants.COMING_FROM_BOOKMARKS)) {
                            clearLists();
                            searchBookmarks(query);
                        } else {
                            skeletonHidden = false;
                            skeletonShown = false;
                            if(initialSearchCompleted) {
                                clearLists();
                                showRecentArticles(recentArticlesModels.size(), apiCallURL, AppConstants.SEARCH_REQUEST_TYPE);
                            }
                            initialSearchCompleted = true;
                        }
                    } else {
                        incomingSource = realIncomingSource;
                        apiCallURL = apiCallUrl(incomingSource);
                        ((HomeActivity) mContext).setQueryHint(getSearchQueryHint(incomingSource));
                        if(realIncomingSource.equals(AppConstants.COMING_FROM_BOOKMARKS)) {
                            clearLists();
                            showBookmarks();
                        } else {
                            skeletonHidden = false;
                            skeletonShown = false;
                            if(initialSearchCompleted) {
                                clearLists();
                                showRecentArticles(recentArticlesModels.size(), apiCallURL, incomingSource);
                            }
                            initialSearchCompleted = false;
                        }
                    }
                }
            });
        }
    }

    private void clearLists() {
        if(!recentArticlesModels.isEmpty()) {
            recentArticlesModels.clear();
            recentArticlesAdapter.notifyDataSetChanged();
        }
        if(!articlesShown.isEmpty()) {
            articlesShown.clear();
        }
    }

    private String getSearchQueryHint(String source) {
        switch (source) {
            case AppConstants.COMING_FROM_RECENT_ARTICLES:
                return mContext.getResources().getString(R.string.search_website, mContext.getResources().getString(R.string.app_name));
            case AppConstants.COMING_FROM_BOOKMARKS:
                return mContext.getResources().getString(R.string.search_bookmarks);
            case AppConstants.COMING_FROM_CATEGORIES:
                return getCategoryName();
            default:
                return mContext.getResources().getString(R.string.search_website, mContext.getResources().getString(R.string.app_name));
        }
    }

    private String getCategoryName() {
        return "Search";
    }

    private String getApiCallUrl(String incomingSource, String query) {
        String apiCallCommonUrl = ApiConstants.GET_SEARCH_RESULTS + "\"" + query + "\"";
        switch (incomingSource) {
            case AppConstants.COMING_FROM_RECENT_ARTICLES:
                return apiCallCommonUrl;
            case AppConstants.COMING_FROM_CATEGORIES:
                return apiCallCommonUrl + "&categories="+categoryId;
            case AppConstants.COMING_FROM_BOOKMARKS:
                return "bookmarks";
            case AppConstants.COMING_FROM_SEARCH:
                CommonUtils.writeLog(AppConstants.ERROR_LOG,TAG, "Something is fishy, check me");
                return apiCallCommonUrl;
            default:
                return apiCallCommonUrl;
        }
    }

    private void addRecyclerViewLayout() {
        if(!recentArticlesModels.isEmpty()) {
            recentArticlesModels.clear();
            recentArticlesAdapter.notifyDataSetChanged();
        }
        if(!articlesShown.isEmpty()) {
            articlesShown.clear();
        }

        switch (incomingSource) {
            case AppConstants.COMING_FROM_RECENT_ARTICLES:
                showRecentArticles(recentArticlesModels.size(), apiCallURL, AppConstants.RECENT_ARTICLES_REQUEST_TYPE);
                addOnScrollListener();
                break;
            case AppConstants.COMING_FROM_BOOKMARKS:
                showBookmarks();
                break;
            case AppConstants.COMING_FROM_CATEGORIES:
                showRecentArticles(recentArticlesModels.size(), apiCallURL, AppConstants.CATEGORIES_REQUEST_TYPE);
                addOnScrollListener();
                break;
            case AppConstants.COMING_FROM_SEARCH:
                showRecentArticles(recentArticlesModels.size(), apiCallURL, AppConstants.SEARCH_REQUEST_TYPE);
                addOnScrollListener();
                break;
            default:
                showRecentArticles(recentArticlesModels.size(), apiCallURL, AppConstants.RECENT_ARTICLES_REQUEST_TYPE);
                addOnScrollListener();
                break;
        }

    }

    private void addOnScrollListener() {
        recentArticleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                if (userScrolled
                        && (visibleItemCount + pastVisiblesItems) == totalItemCount) {
                    userScrolled = false;
                    String requestType = getRequestType();
                    showRecentArticles(recentArticlesModels.size(), apiCallURL, requestType);
                }
            }
        });
    }

    private String getRequestType() {
        switch (incomingSource) {
            case AppConstants.COMING_FROM_RECENT_ARTICLES:
                return AppConstants.RECENT_ARTICLES_REQUEST_TYPE;
            case AppConstants.COMING_FROM_CATEGORIES:
                return AppConstants.CATEGORIES_REQUEST_TYPE;
            case AppConstants.COMING_FROM_SEARCH:
                return AppConstants.SEARCH_REQUEST_TYPE;
            default:
                return AppConstants.COMING_FROM_RECENT_ARTICLES;
        }
    }

    private void showRecentArticles(final int startingArticle, final String url, String requestType) {

        showSwipeDownToRefresh();

        if(!articlesShown.contains(recentArticlesModels.size())) {
            articlesShown.add(recentArticlesModels.size());
        } else {
            hideSwiperDownToRefresh();
            return;
        }
        showProgressbar();
        if(recentArticlesModels.isEmpty()) loadingArticlesMessage();
        else hideNoInternetConnectionMessage();
        loopLimit = postsLoopLimit;
        if(mContext.getResources().getBoolean(R.bool.isTablet)) {
            if(Integer.valueOf(loopLimit) < 10) {
                loopLimit = "10";
            }
        }
        VolleyUtils.makeJSONArrayRequest(null, String.format(url, loopLimit) + "&offset="+ (startingArticle + excludedPostsCount) , AppConstants.METHOD_GET, requestType, new VolleyJSONArrayRequestListener() {
                @Override
                public void onError(String error, String statusCode) {
                    showNoInternetConnectionMessage();
                    CommonUtils.writeLog(AppConstants.ERROR_LOG,TAG, "Status Code: " + statusCode + " Error: " + error);
                    hideProgressbar();
                    RecentArticlesModel article = new RecentArticlesModel("", "", -1,"","","");
                    recentArticlesModels.add(article);
                    hideSwiperDownToRefresh();
                }

                @Override
                public void onResponse(JSONArray response) {
                    CommonUtils.writeLog(AppConstants.INFO_LOG,TAG, response.toString());
                    try {
                        if(BuildConfig.DEBUG) response = new JSONArray(response.toString().replace("127.0.0.1", AppConstants.LOCAL_HOST));
                        if(incomingSource.equals(AppConstants.COMING_FROM_SEARCH)) {
                            if(response.toString().equals("[]") && recentArticlesModels.isEmpty()) {
                                noSearchResults();
                            } else {
                                searchResultsFound();
                            }
                        } else {
                            searchResultsFound();
                        }
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject recentPosts = (JSONObject) response.get(i);
                            int postId = 1;
                            String featuredImage = AppConstants.HIDE_VIEW;
                            String postTitle = "";
                            String authorName = AppConstants.HIDE_VIEW;
                            String postTime = AppConstants.HIDE_VIEW;
                            boolean isPostExcluded = false;
                            layoutStyle = AppConstants.LAYOUT_STYLE_TOP_IMAGE;
                            if(recentPosts.has(ApiConstants.KEY_POST_ID)) {
                                postId = recentPosts.getInt(ApiConstants.KEY_POST_ID);
                            }
                            if(recentPosts.has(ApiConstants.KEY_POST_TITLE)) {
                                JSONObject title = recentPosts.getJSONObject(ApiConstants.KEY_POST_TITLE);
                                postTitle = title.getString(ApiConstants.RENDERED);
                            }

                            if(recentPosts.has(ApiConstants.WP_DROID_OBJECT)) {
                                JSONObject wpDroidJsonObject = recentPosts.getJSONObject(ApiConstants.WP_DROID_OBJECT);
                                if(wpDroidJsonObject.has(ApiConstants.KEY_POST_FEATURED_IMAGE)) {
                                    featuredImage = wpDroidJsonObject.getString(ApiConstants.KEY_POST_FEATURED_IMAGE);
                                }
                                if(wpDroidJsonObject.has(ApiConstants.KEY_IS_POST_EXCLUDED)) {
                                    isPostExcluded = wpDroidJsonObject.getBoolean(ApiConstants.KEY_IS_POST_EXCLUDED);
                                }

                                if(wpDroidJsonObject.has(ApiConstants.KEY_POSTS_LOOP_LIMIT)) {
                                    String limit = wpDroidJsonObject.getString(ApiConstants.KEY_POSTS_LOOP_LIMIT);
                                    if(!limit.equals(postsLoopLimit)) {
                                        postsLoopLimit = limit;
                                        loopLimit = limit;
                                        CommonUtils.setPostsLoopLimit(limit, mContext);
                                    }
                                }

                                if(wpDroidJsonObject.has(ApiConstants.KEY_RECENT_ARTICLE_LAYOUT_STYLE)) {
                                    layoutStyle = wpDroidJsonObject.getString(ApiConstants.KEY_RECENT_ARTICLE_LAYOUT_STYLE);
                                }

                                if(wpDroidJsonObject.has(ApiConstants.KEY_AUTHOR_NAME)) {
                                    if(layoutStyle.equals(AppConstants.LAYOUT_STYLE_TOP_IMAGE)){
                                        authorName = getString(R.string.article_written_by) + " " +wpDroidJsonObject.getString(ApiConstants.KEY_AUTHOR_NAME);
                                    }else {
                                        authorName = wpDroidJsonObject.getString(ApiConstants.KEY_AUTHOR_NAME);
                                    }
                                }

                                if(wpDroidJsonObject.has(ApiConstants.KEY_DATE_TIME)) {
                                    if(layoutStyle.equals(AppConstants.LAYOUT_STYLE_TOP_IMAGE)){
                                        postTime = getString(R.string.article_written_on) + " " +wpDroidJsonObject.getString(ApiConstants.KEY_DATE_TIME);
                                    }else {
                                        postTime = wpDroidJsonObject.getString(ApiConstants.KEY_DATE_TIME);
                                    }
                                }

                                if(wpDroidJsonObject.has(ApiConstants.KEY_INVITE_FRIENDS)){
                                   String isFriendsInviteLink = wpDroidJsonObject.getString(ApiConstants.KEY_INVITE_FRIENDS);
                                   if(!isInviteFriendsExistsHandler){
                                       if(incomingSource.equalsIgnoreCase(AppConstants.COMING_FROM_RECENT_ARTICLES)){
                                           ((HomeActivity) getActivity()).inviteFriendsShowingStatus(true, isFriendsInviteLink);
                                       }
                                       isInviteFriendsExistsHandler = true;
                                   }
                                }else {
                                    if(!isInviteFriendsNotExistsHandler){
                                        if(incomingSource.equalsIgnoreCase(AppConstants.COMING_FROM_RECENT_ARTICLES)){
                                            ((HomeActivity) getActivity()).inviteFriendsShowingStatus(false, AppConstants.HIDE_VIEW);
                                        }
                                        isInviteFriendsNotExistsHandler = true;
                                    }
                                }

                            }

                            if(isPostExcluded) {
                                excludedPostsCount++;
                            } else {

                                RecentArticlesModel article = new RecentArticlesModel(
                                        postTitle,
                                        featuredImage,
                                        postId,
                                        authorName,
                                        postTime,
                                        layoutStyle
                                );

                                recentArticlesModels.add(article);
                                recentArticlesAdapter.notifyItemInserted(recentArticlesModels.size());
                            }
                        }

                    } catch (JSONException e) {
                        Log.e("Error", e.getMessage());
                        CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
                        showNoInternetConnectionMessage();
                    }

                    hideProgressbar();
                    hideSkeleton();
                    hideSwiperDownToRefresh();
                }
            });
    }

    private void handleTryAgain() {
        addRecyclerViewLayout();
    }

    private void showNoInternetConnectionMessage() {
        recentArticleRecyclerView.setVisibility(View.GONE);
        noInternetConnectionRelativeLayout.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.VISIBLE);
        messageTextView.setText(mContext.getResources().getString(R.string.no_internet_connection_message));
        messageImageView.setImageResource(R.drawable.ic_img_no_connection);
        if(isArticleComingFromCategories){
            ((CategoriesActivity) mContext).expandToolbar();
        }else {
            ((HomeActivity) mContext).expandToolbar();
        }
    }

    //TODO - ACTUAL ONE - SHOWING
//    private void loadingArticlesMessage() {
//        recentArticleRecyclerView.setVisibility(View.GONE);
//        noInternetConnectionRelativeLayout.setVisibility(View.VISIBLE);
//        tryAgainButton.setVisibility(View.GONE);
//        noInternetMessageTextView.setText(mContext.getResources().getString(R.string.loading_articles_message));
//        messageImageView.setImageResource(R.drawable.img_splash_screen_logo);
//    }

    //TODO - NEW ONE - SHOWING

    private void loadingArticlesMessage() {
        recentArticleRecyclerView.setVisibility(View.VISIBLE);
        noInternetConnectionRelativeLayout.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
        showSkeleton();
//        noInternetMessageTextView.setText(mContext.getResources().getString(R.string.loading_articles_message));
//        messageImageView.setImageResource(R.drawable.img_splash_screen_logo);
    }

    private void hideNoInternetConnectionMessage() {
        recentArticleRecyclerView.setVisibility(View.VISIBLE);
        noInternetConnectionRelativeLayout.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
    }



    private void showProgressbar() {
        loadMoreProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressbar() {
        loadMoreProgressBar.setVisibility(View.GONE);
    }

    private void showSkeleton() {
        if(!skeletonShown) {
            skeletonScreen = Skeleton.bind(recentArticleRecyclerView)
                    .adapter(recentArticlesAdapter)
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

    @Override
    public void onRefresh() {
        skeletonShown = false;
        skeletonHidden = false;
        addRecyclerViewLayout();
    }

     public void noSearchResults() {
        noInternetConnectionRelativeLayout.setVisibility(View.VISIBLE);
        recentArticleRecyclerView.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
        if(incomingSource.equals(AppConstants.COMING_FROM_BOOKMARKS)) {
            messageTextView.setText(mContext.getResources().getString(R.string.no_bookmarks));
            messageImageView.setImageResource(R.drawable.ic_img_no_search_results);
            ((HomeActivity) mContext).expandToolbar();
        } else {
            messageTextView.setText(String.format("%s%s\"", getString(R.string.no_results_found_for), searchQuery));
            messageImageView.setImageResource(R.drawable.ic_img_no_search_results);
        }
    }

    private void searchResultsFound() {
        noInternetConnectionRelativeLayout.setVisibility(View.GONE);
        recentArticleRecyclerView.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.GONE);
    }

    private void showBookmarks() {
        searchResultsFound();
        if(!bookmarkedArticlesTitles.isEmpty()) bookmarkedArticlesTitles.clear();
        if(!recentArticlesModels.isEmpty()) recentArticlesModels.clear();
        DatabaseHandlerUtils databaseHandlerUtils = new DatabaseHandlerUtils(mContext);
        bookmarksModel = databaseHandlerUtils.getBookmarks();

        for (BookmarksModel bookmarks : bookmarksModel) {
            RecentArticlesModel bookmark = new RecentArticlesModel(
                    bookmarks.getTitle(),
                    bookmarks.getImage(),
                    Integer.valueOf(bookmarks.getId()),
                    bookmarks.getAuthorName(),
                    bookmarks.getPostTime(),
                    layoutStyle
            );
            if(!bookmarkedArticlesTitles.contains(bookmarks.getTitle())) {
                bookmarkedArticlesTitles.add(bookmark.getTitle());
                recentArticlesModels.add(bookmark);
                recentArticlesAdapter.notifyItemInserted(recentArticlesModels.size());
            } else {
                CommonUtils.writeLog(AppConstants.ERROR_LOG,TAG,"DUPLICATES FOUND: "+bookmark.getTitle());
            }
        }
        databaseHandlerUtils.closeDatabase(databaseHandlerUtils);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if(bookmarksModel.isEmpty()) {
            noSearchResults();
        }
    }

    public void searchBookmarks(String query) {
        clearLists();
        if(!bookmarkedArticlesTitles.isEmpty()) bookmarkedArticlesTitles.clear();
        for (BookmarksModel bookmarks : bookmarksModel) {
            if (bookmarks.getTitle().toLowerCase().contains(query.toLowerCase())) {

                RecentArticlesModel bookmark = new RecentArticlesModel(
                        bookmarks.getTitle(),
                        bookmarks.getImage(),
                        Integer.valueOf(bookmarks.getId()),
                        bookmarks.getAuthorName(),
                        bookmarks.getPostTime(),
                        layoutStyle
                );
                if(!bookmarkedArticlesTitles.contains(bookmarks.getTitle().toLowerCase())) {
                    bookmarkedArticlesTitles.add(bookmark.getTitle().toLowerCase());
                    recentArticlesModels.add(bookmark);
                    recentArticlesAdapter.notifyItemInserted(recentArticlesModels.size());
                } else {
                    CommonUtils.writeLog(AppConstants.ERROR_LOG,TAG,"DUPLICATES FOUND: "+bookmark.getTitle());
                }
            }
        }

        if(recentArticlesModels.isEmpty()) {
            noSearchResults();
        } else {
            searchResultsFound();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHandlerUtils.closeDatabase(databaseHandlerUtils);
    }

}
