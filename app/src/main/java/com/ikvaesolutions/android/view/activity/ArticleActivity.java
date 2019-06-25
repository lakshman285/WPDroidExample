package com.ikvaesolutions.android.view.activity;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.ads.consent.ConsentInformation;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ikvaesolutions.android.BuildConfig;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.adapters.RecentArticlesAdapter;
import com.ikvaesolutions.android.constants.ApiConstants;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.helper.AdMobHelper;
import com.ikvaesolutions.android.listeners.AdMobConsentInterface;
import com.ikvaesolutions.android.listeners.VolleyJSONObjectRequestListener;
import com.ikvaesolutions.android.models.recyclerviews.RecentArticlesModel;
import com.ikvaesolutions.android.models.sqlite.BookmarksModel;
import com.ikvaesolutions.android.others.GlideApp;
import com.ikvaesolutions.android.utils.CommonUtils;
import com.ikvaesolutions.android.utils.DatabaseHandlerUtils;
import com.ikvaesolutions.android.utils.VolleyUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends AppCompatActivity implements AdMobConsentInterface {

    final String TAG = this.getClass().getSimpleName();
    Context mContext;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    Toolbar mToolbar;
    ImageView imageViewFeaturedImage;
    TextView textViewArticleTitle, articlePublishedDate, relatedPostsCategoryName, articlePostedInText, articlePostedInButton;
    WebView webViewArticleContent;
    String postTitle = "", featuredImage = "", postUrl = "", reportErrorEmail = "";
    Spanned spannableTitle;
    int articleId;
    boolean isSaved = false;
    DatabaseHandlerUtils databaseHandlerUtils;
    View snackBarView = imageViewFeaturedImage;
    String incomingSource, commentStatus;
    public RecyclerView relatedPostsRecyclerView;
    public RecentArticlesAdapter recentArticlesAdapter;
    List<RecentArticlesModel> recentArticlesModels;
    LinearLayoutManager mLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
//    private AdView mAdView;
    SkeletonScreen skeletonScreen;
    LinearLayout rootView;
    String authorName = AppConstants.HIDE_VIEW;
    String postTime = AppConstants.HIDE_VIEW;
    boolean allowInternalLinks = false;
    CoordinatorLayout root;
    LinearLayout relatedPostsLayout;
    RelativeLayout articlePostedInLayout;
    boolean isCopyToClipboardEnabled, isOpenInBrowser, isCommentsEnabled, isReportErrorShown, isRelatedArticlesShown;

    private AdView mAdView;
    AdMobHelper adMobHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        init();
        CommonUtils.logScreenActivity("Full Article Activity");
    }

    private void init() {
        mContext = getApplicationContext();
        databaseHandlerUtils = new DatabaseHandlerUtils(mContext);

        root = (CoordinatorLayout) findViewById(R.id.root);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(null);

        relatedPostsRecyclerView = (RecyclerView) findViewById(R.id.related_posts_recycler_view);
        relatedPostsCategoryName = (TextView) findViewById(R.id.related_posts_category_name);
        relatedPostsLayout = (LinearLayout) findViewById(R.id.related_posts_layout);

        articlePostedInLayout = (RelativeLayout)findViewById(R.id.article_posted_in_layout);
        articlePostedInButton = (Button)findViewById(R.id.article_posted_in_button);
        articlePostedInText = (TextView)findViewById(R.id.article_posted_in_text);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.colorPrimary), mContext.getResources().getColor(R.color.colorPrimaryDark));

        recentArticlesModels = new ArrayList<>();

        rootView = (LinearLayout) findViewById(R.id.rootView);
        imageViewFeaturedImage = (ImageView) findViewById(R.id.imageViewFeaturedImage);
        textViewArticleTitle = (TextView) findViewById(R.id.textViewArticleTitle);
        articlePublishedDate = (TextView) findViewById(R.id.published_date);
        webViewArticleContent = (WebView) findViewById(R.id.webViewContent);
        WebSettings webSettings = webViewArticleContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultFontSize(10);


        Intent previousIntent = getIntent();
        articleId = previousIntent.getIntExtra(AppConstants.BUNGLE_ARTICLE_ID, 1);
        incomingSource = previousIntent.getStringExtra(AppConstants.INCOMING_SOURCE);
        setIsSaved(databaseHandlerUtils.isBookmarked(String.valueOf(articleId)));
        getArticleContent(articleId, AppConstants.FULL_ARTICLE_REQUEST_TYPE);

        actionBar.show();
        skeletonScreen = Skeleton.bind(rootView)
                .load(R.layout.loading_full_content)
                .show();

        webViewLinkClinks();
        whiteNotificationBar(root);

        if(AppConstants.ABMOB) {
            loadBannerAd();
        }

    }

    private void loadBannerAd() {
        mAdView = findViewById(R.id.adView);
        adMobHelper = new AdMobHelper(this, mContext, this, TAG);
        boolean isFromEU = ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown();
        if(isFromEU) {
            adMobHelper.checkConsentStatus();
            CommonUtils.logCustomEvent("Full Article Activity", "AdMob", "EUU User");
        } else {
            adMobHelper.setAdsLoaded(true);
            loadBottomAd();
            CommonUtils.logCustomEvent("Full Article Activity", "AdMob", "NON-EUU User");
        }
    }

    private void webViewLinkClinks() {
        webViewArticleContent.setWebViewClient(new WebViewClient(){
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                openInternalLink(url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                openInternalLink(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                skeletonScreen.hide();
            }

        });
    }

    public boolean getIsSaved() {return isSaved;}
    public void setIsSaved(boolean saved) {isSaved = saved;}

    private void getArticleContent(final int articleId, String requestType) {

//        Log.e(TAG, ApiConstants.GET_ARTICLE_CONTENT + articleId +"?wpdroid=true");

        VolleyUtils.makeJSONObjectRequest(null, ApiConstants.GET_ARTICLE_CONTENT + articleId +"?wpdroid=true", AppConstants.METHOD_GET, requestType, false, new VolleyJSONObjectRequestListener() {
            @Override
            public void onError(String error, String statusCode) {
                CommonUtils.writeLog(AppConstants.ERROR_LOG, TAG, "Error: "+ error + " Status: " + statusCode);
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(BuildConfig.DEBUG) response = new JSONObject(response.toString().replace("127.0.0.1",AppConstants.LOCAL_HOST));
                    String appCSS = "";
                    CommonUtils.writeLog(AppConstants.INFO_LOG, TAG, response.toString());
                    JSONObject title = response.getJSONObject(ApiConstants.KEY_POST_TITLE);
                    postTitle = title.getString(ApiConstants.RENDERED);
                    spannableTitle = CommonUtils.fromHtml(postTitle);
                    commentStatus = response.getString(ApiConstants.KEY_COMMENT_STATUS);

                    if(response.has(ApiConstants.WP_DROID_OBJECT)) {
                        JSONObject wpDroidJsonObject = response.getJSONObject(ApiConstants.WP_DROID_OBJECT);
                        if(wpDroidJsonObject.has(ApiConstants.APP_CSS)) {
                            appCSS = "<style>" + wpDroidJsonObject.getString(ApiConstants.APP_CSS) + "</style>";
                        }
                        if(wpDroidJsonObject.has(ApiConstants.INTERNAL_LINKS)) {
                            allowInternalLinks = wpDroidJsonObject.getBoolean(ApiConstants.INTERNAL_LINKS);
                        }

                        /*FEATURED IMAGE CONDITIONS*/
                        openArticleFeaturedImageConditions(wpDroidJsonObject);

                        /*COMMENTS ENABLED CONDITIONS*/
                        isCommentsEnabled = wpDroidJsonObject.has(ApiConstants.RELATED_POST_COMMENTS) && wpDroidJsonObject.getBoolean(ApiConstants.RELATED_POST_COMMENTS);
                        invalidateOptionsMenu();

                         /*CATEGORY ENABLED CONDITIONS*/
                         openArticleCategoryEnabledConditions(wpDroidJsonObject);

                         /*SHOW DATE AND TIME CONDITIONS*/
                         openArticleAuthorDateEnabledConditions(wpDroidJsonObject);

                         /*REPORT ERROR CONDITIONS*/
                        isReportErrorShown = wpDroidJsonObject.has(ApiConstants.REPORT_EMAIL_ERROR);
                        if(isReportErrorShown){
                            invalidateOptionsMenu();
                            reportErrorEmail = wpDroidJsonObject.getString(ApiConstants.REPORT_EMAIL_ERROR);
                        }else {
                            invalidateOptionsMenu();
                        }

                        /*COPY TO CLIPBOARD CONDITIONS*/
                        isCopyToClipboardEnabled = wpDroidJsonObject.has(ApiConstants.RELATED_POSTS_COPY_TO_CLIPBOARD) && wpDroidJsonObject.getBoolean(ApiConstants.RELATED_POSTS_COPY_TO_CLIPBOARD);
                        invalidateOptionsMenu();

                         /*OPEN IN BROWSER CONDITIONS*/
                        isOpenInBrowser = wpDroidJsonObject.has(ApiConstants.RELATED_POSTS_OPEN_BROWSER) && wpDroidJsonObject.getBoolean(ApiConstants.RELATED_POSTS_OPEN_BROWSER);
                        invalidateOptionsMenu();

                         /*RELATED POSTS CONDITIONS*/
                         openArticleRelatedPostsConditions(wpDroidJsonObject);
                    }

                    JSONObject content = response.getJSONObject(ApiConstants.CONTENT);
                    String articleContent = content.getString(ApiConstants.RENDERED);

                    textViewArticleTitle.setText(CommonUtils.fromHtml(postTitle));

                    String head = "<html><head>" + appCSS + "</head>";
                    String body = "<body>"+ articleContent + "</body></html>";
                    webViewArticleContent.loadData(head + body, "text/html; charset=utf-8", "UTF-8");

                    postUrl = response.getString(ApiConstants.POST_LINK);

                }catch (JSONException e) {
                    CommonUtils.writeLog(AppConstants.ERROR_LOG, TAG, e.toString());
                }
                hideSwiperDownToRefresh();
            }

        });
    }

    private void openArticleRelatedPostsConditions(JSONObject wpDroidJsonObject) throws JSONException {
        if(wpDroidJsonObject.has(ApiConstants.KEY_RELATED_POSTS)) {
            relatedPostsLayout.setVisibility(View.VISIBLE);
            JSONObject relatedPostsObject = wpDroidJsonObject.getJSONObject(ApiConstants.KEY_RELATED_POSTS);
            isRelatedArticlesShown = true;
            String scrollDirection = relatedPostsObject.getString(ApiConstants.KEY_SCROLL_DIRECTION);
            String relatedPostsBasedOn = relatedPostsObject.getString(ApiConstants.RELATED_POSTS_BASED_ON);
            /*POSTS BASED ON CONDITIONS*/
            if(relatedPostsBasedOn.equals(AppConstants.POST_TAGS)){
                relatedPostsCategoryName.setVisibility(View.GONE);
            }else {
                relatedPostsCategoryName.setVisibility(View.VISIBLE);
                relatedPostsCategoryName.setText(relatedPostsBasedOn);
            }
            /*POSTS STYLE CONDITIONS*/
            if (scrollDirection.equals(AppConstants.HORIZONTAL)) {
                relatedPostsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            } else {
                mLayoutManager = new GridLayoutManager(mContext, 1);
                relatedPostsRecyclerView.setLayoutManager(mLayoutManager);
            }
            recentArticlesAdapter = new RecentArticlesAdapter(mContext, recentArticlesModels, databaseHandlerUtils, AppConstants.COMING_FROM_ARTICLE_ACTIVITY, scrollDirection);
            relatedPostsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            relatedPostsRecyclerView.setNestedScrollingEnabled(false);
            relatedPostsRecyclerView.setAdapter(recentArticlesAdapter);
            String relatedPostStyle = relatedPostsObject.getString(ApiConstants.RELATED_POSTS_STYLE);
            String relatedPostFeaturedImage, relatedPostDate, relatedPostAuthorName = "";
            JSONArray relatedPosts = relatedPostsObject.getJSONArray(ApiConstants.KEY_POSTS);
            for (int i = 0; i < relatedPosts.length(); i++) {
                JSONObject posts = relatedPosts.getJSONObject(i);
                String relatedPostTitle = posts.getString(ApiConstants.KEY_POST_TITLE);
                /*RELATED POST FEATURED IMAGE CONDITIONS*/
                if(posts.has(ApiConstants.KEY_POST_FEATURED_IMAGE))
                    relatedPostFeaturedImage = posts.getString(ApiConstants.KEY_POST_FEATURED_IMAGE);
                else relatedPostFeaturedImage = AppConstants.HIDE_VIEW;
                int relatedPostId = posts.getInt(ApiConstants.RELATED_POST_ID);
                /*RELATED POST AUTHOR NAME CONDITIONS*/
                if (posts.has(ApiConstants.RELATED_POST_AUTHOR_NAME))
                    relatedPostAuthorName = getResources().getString(R.string.article_written_by)+posts.getString(ApiConstants.RELATED_POST_AUTHOR_NAME);
                else relatedPostAuthorName = AppConstants.HIDE_VIEW;
                /*RELATED POST DATE AND TIME CONDITIONS*/
                if (posts.has(ApiConstants.KEY_DATE_TIME))
                    relatedPostDate = getResources().getString(R.string.article_written_on) +posts.getString(ApiConstants.KEY_DATE_TIME);
                else relatedPostDate = AppConstants.HIDE_VIEW;
                RecentArticlesModel relatedArticles = new RecentArticlesModel(relatedPostTitle, relatedPostFeaturedImage, relatedPostId, relatedPostAuthorName, relatedPostDate, relatedPostStyle);
                recentArticlesModels.add(relatedArticles);
                recentArticlesAdapter.notifyItemInserted(recentArticlesModels.size());
            }

        }else {
            relatedPostsLayout.setVisibility(View.GONE);
            isRelatedArticlesShown = false;
        }
    }

    private void openArticleAuthorDateEnabledConditions(JSONObject wpDroidJsonObject) throws JSONException {

        boolean showAuthorName = wpDroidJsonObject.has(ApiConstants.KEY_AUTHOR_NAME);
        boolean showDateTime = wpDroidJsonObject.has(ApiConstants.KEY_DATE_TIME);
        /*AUTHOR NAME ENABLED CONDITIONS*/
        authorName = showAuthorName ? wpDroidJsonObject.getString(ApiConstants.KEY_AUTHOR_NAME) : AppConstants.HIDE_VIEW;
        postTime = showDateTime ? wpDroidJsonObject.getString(ApiConstants.KEY_DATE_TIME) : AppConstants.HIDE_VIEW;
        if(showAuthorName && showDateTime){
            articlePublishedDate.setVisibility(View.VISIBLE);
            articlePublishedDate.setText(String.format("by %s on %s", authorName, postTime));
        }else if(!showAuthorName && !showDateTime){
            articlePublishedDate.setVisibility(View.GONE);
        }else if(showAuthorName && !showDateTime){
            articlePublishedDate.setVisibility(View.VISIBLE);
            articlePublishedDate.setText(String.format("by %s", authorName));
        }else {
            articlePublishedDate.setVisibility(View.VISIBLE);
            articlePublishedDate.setText(String.format("on %s", postTime));
        }

    }

    private void openArticleCategoryEnabledConditions(JSONObject wpDroidJsonObject) throws JSONException {

        if(wpDroidJsonObject.has(ApiConstants.KEY_CATEGORY)){
            articlePostedInLayout.setVisibility(View.VISIBLE);
            final JSONObject categoryObject = wpDroidJsonObject.getJSONObject(ApiConstants.KEY_CATEGORY);
            articlePostedInText.setText(mContext.getResources().getString(R.string.posted_in));
            final String buttonText = categoryObject.getString(ApiConstants.KEY_CATEGORY_NAME);
            articlePostedInButton.setText(buttonText);

            articlePostedInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent categoryIntent = new Intent(mContext, CategoriesActivity.class);
                    categoryIntent.putExtra(AppConstants.CATEGORY_NAME, buttonText);
                    try {
                        categoryIntent.putExtra(AppConstants.CATEGORY_COUNT, categoryObject.getInt(ApiConstants.KEY_CATEGORY_COUNT));
                        categoryIntent.putExtra(AppConstants.CATEGORY_ID, categoryObject.getInt(ApiConstants.KEY_CATEGORY_ID));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    categoryIntent.putExtra(AppConstants.INCOMING_SOURCE, AppConstants.COMING_FROM_CATEGORIES);
                    startActivity(categoryIntent);
                }
            });
        }else {
            articlePostedInLayout.setVisibility(View.GONE);
        }

    }
    private void openArticleFeaturedImageConditions(JSONObject wpDroidJsonObject) throws JSONException {
        if(wpDroidJsonObject.has(ApiConstants.KEY_POST_FEATURED_IMAGE)) {
            featuredImage = wpDroidJsonObject.getString(ApiConstants.KEY_POST_FEATURED_IMAGE);
            GlideApp.with(mContext).load(featuredImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewFeaturedImage);
        } else {
            imageViewFeaturedImage.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_article_activity, menu);
        if(AppConstants.BOOKMARKS) {
            if(getIsSaved()) {
                mToolbar.getMenu().findItem(R.id.menuBookmark).setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_filled_white));
            } else {
                mToolbar.getMenu().findItem(R.id.menuBookmark).setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_outline_white));
            }
        } else {
            mToolbar.getMenu().findItem(R.id.menuBookmark).setVisible(false);
        }
        if(isReportErrorShown) mToolbar.getMenu().findItem(R.id.menuFeedback).setVisible(true);
        else mToolbar.getMenu().findItem(R.id.menuFeedback).setVisible(false);
        if(isCommentsEnabled) mToolbar.getMenu().findItem(R.id.menuComment).setVisible(true);
        else mToolbar.getMenu().findItem(R.id.menuComment).setVisible(false);
        if(isCopyToClipboardEnabled) mToolbar.getMenu().findItem(R.id.menuCopyToClipboard).setVisible(true);
        else mToolbar.getMenu().findItem(R.id.menuCopyToClipboard).setVisible(false);
        if(isOpenInBrowser) mToolbar.getMenu().findItem(R.id.menuBrowser).setVisible(true);
        else mToolbar.getMenu().findItem(R.id.menuBrowser).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    private void handleBookmark(){
        if(getIsSaved()) {
            databaseHandlerUtils.removeBookmark(String.valueOf(articleId));
            setIsSaved(false);
            if(!AppConstants.ABMOB) {
                CommonUtils.showSnackbar(imageViewFeaturedImage, TAG, getString(R.string.bookmark_removed));
            } else {
                Toast toast = Toast.makeText(mContext, mContext.getResources().getString(R.string.bookmark_removed), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                toast.show();
            }
            CommonUtils.logCustomEvent("Full Article Activity", "Bookmark Removed", postTitle);
        } else {
            databaseHandlerUtils.addBookmark(new BookmarksModel(
                    String.valueOf(articleId),
                    postTitle,
                    featuredImage,
                    String.valueOf(System.currentTimeMillis()),
                    authorName,
                    postTime
            ));
            setIsSaved(true);
            if(!AppConstants.ABMOB) {
                CommonUtils.showSnackbar(imageViewFeaturedImage, TAG, mContext.getResources().getString(R.string.bookmark_added));
            } else {
                Toast toast = Toast.makeText(mContext, mContext.getResources().getString(R.string.bookmark_added), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                toast.show();
            }
            CommonUtils.logCustomEvent("Full Article Activity", "Bookmark Added", postTitle);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menuShare:
                handleShare();
                CommonUtils.logCustomEvent("Full Article Activity", "Shared", postTitle);
                break;
            case R.id.menuComment:
                startActivity(new Intent(this, CommentsActivity.class).putExtra(AppConstants.BUNDLE_POST_ID, articleId)
                .putExtra(AppConstants.BUNDLE_COMMENT_STATUS, commentStatus));
                break;
            case R.id.menuBookmark:
                handleBookmark();
                break;
            case R.id.menuBrowser:
                handleBrowser();
                CommonUtils.logCustomEvent("Full Article Activity", "Opened in Browser", postTitle);
                break;
            case R.id.menuFeedback:
                handleFeedback();
                CommonUtils.logCustomEvent("Full Article Activity", "Feedback", postTitle);
                break;
            case R.id.menuCopyToClipboard:
                handleCopyToClipboard();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                CommonUtils.writeLog(AppConstants.ERROR_LOG, TAG, " Unknown Menu Item " + menuItem.getItemId() + " Clicked ");
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void handleCopyToClipboard() {
        ClipData myClip = ClipData.newPlainText("post_url_link", postUrl);
        ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(getApplicationContext(), R.string.article_link_copied_to_clipboard, Toast.LENGTH_SHORT).show();
    }

    private void handleFeedback() {
        try
        {
            String autoGeneratedMessage = "Post Url: " + postUrl + "\n\n Article Title: "+ postTitle + "\n\n OS: Android" + "\n\n App Version: " + BuildConfig.VERSION_NAME;
            Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:"+ reportErrorEmail));
            intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.email_subject));
            intent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.email_message) + autoGeneratedMessage);
            startActivity(intent);
        }
        catch(Exception e)
        {
            CommonUtils.showSnackbar(snackBarView, TAG, mContext.getResources().getString(R.string.no_mailapp_found));
            e.printStackTrace();
        }
    }

    private void handleBrowser() {
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl)));
        }catch (Exception e){
            CommonUtils.showSnackbar(snackBarView, TAG, mContext.getResources().getString(R.string.no_browser_installed));
        }
    }

    private void handleShare() {
        Intent iShare = new Intent(Intent.ACTION_SEND);
        iShare.setType("text/plain");
        iShare.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.article_share) + ": \n'" + postTitle + "' \n\n" + postUrl);
        startActivity(Intent.createChooser(iShare, getResources().getString(R.string.share_to)));
    }

    private void openInternalLink(String url) {
        if(url.contains(ApiConstants.WEBSITE_URL) && allowInternalLinks) {
            getInternalLinkId(url);
            CommonUtils.logCustomEvent("Full Article Activity", "Clicked", "Internal Link");
        } else {
            CommonUtils.openChromeCustomTab(this, Uri.parse(url));
            CommonUtils.logCustomEvent("Full Article Activity", "Clicked", "External Link");
        }
    }

    private void getInternalLinkId(final String url) {
        showSwipeDownToRefresh();
        Log.e(TAG, ApiConstants.GET_INTERNAL_LINK_ID + url);

        VolleyUtils.makeJSONObjectRequest(null, ApiConstants.GET_INTERNAL_LINK_ID, AppConstants.METHOD_GET, url, true, new VolleyJSONObjectRequestListener() {
            @Override
            public void onError(String error, String statusCode) {
                CommonUtils.openChromeCustomTab(ArticleActivity.this, Uri.parse(url));
            }
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i(TAG, response.toString());
                    int status = response.getInt(ApiConstants.KEY_STATUS);
                    if(status == 0){
                        CommonUtils.openChromeCustomTab(ArticleActivity.this, Uri.parse(url));
                    } else {
                        int postId = response.getInt(ApiConstants.KEY_POST_ID);
                        Intent i = new Intent(mContext, ArticleActivity.class);
                        i.putExtra(AppConstants.BUNGLE_ARTICLE_ID, postId);
                        i.putExtra(AppConstants.INCOMING_SOURCE, AppConstants.COMING_FROM_ADAPTER);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(i);
                    }
                }catch (JSONException e) {
                    CommonUtils.openChromeCustomTab(ArticleActivity.this, Uri.parse(url));
                    CommonUtils.writeLog(AppConstants.ERROR_LOG, TAG, e.toString());
                }
                hideSwiperDownToRefresh();
            }

        });
    }

    @Override
    public void onBackPressed() {
        switch (incomingSource) {
            case AppConstants.COMING_FROM_ADAPTER:
                super.onBackPressed();
                return;
            case AppConstants.COMING_FROM_NOTIFICATION:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return;
            default:
                super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppConstants.ABMOB && adMobHelper.isAdsLoaded()) {
            if (mAdView != null) {
                mAdView.resume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(AppConstants.ABMOB && adMobHelper.isAdsLoaded()) {
            if (mAdView != null) {
                mAdView.pause();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHandlerUtils.closeDatabase(databaseHandlerUtils);
        if(AppConstants.ABMOB && adMobHelper.isAdsLoaded()) {
            if (mAdView != null) {
                mAdView.destroy();
            }
        }
    }
    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void setupAds() {
        mAdView.loadAd(adMobHelper.getRequestBuilder());
        adViewListener();
    }

    private void loadBottomAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        adViewListener();
    }

    private void adViewListener() {
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if(isRelatedArticlesShown){
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) relatedPostsLayout.getLayoutParams();
                    p.bottomMargin = (int) CommonUtils.pxFromDp(mContext, (float) 90);
                    relatedPostsLayout.setLayoutParams(p);
                    mAdView.setVisibility(View.VISIBLE);
                }else {
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) articlePostedInLayout.getLayoutParams();
                    p.bottomMargin = (int) CommonUtils.pxFromDp(mContext, (float) 90);
                    articlePostedInLayout.setLayoutParams(p);
                    mAdView.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {}
            @Override
            public void onAdOpened() {}
            @Override
            public void onAdLeftApplication() {}
            @Override
            public void onAdClosed() {}

        });
    }

    private void showSwipeDownToRefresh() {
            swipeRefreshLayout.setRefreshing(true);
    }

    private void hideSwiperDownToRefresh() {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
        }
    }

}
