package com.ikvaesolutions.android.constants;

/**
 * Created by amarilindra on 08/04/17.
 */

public class AppConstants {


    public static final String HORIZONTAL = "horizontal" ;
    public static final String COMING_FROM_ARTICLE_ACTIVITY = "from_article_activity";
    public static final String POST_TAGS = "tags";
    public static final String BUNDLE_COMMENT_STATUS = "comment_status";
    public static final String NO_COMMENTAR_IMAGE_FOUND = "no_commentar_image_found";
    public static final String NO_COMMENTED_TIME = "no_commented_time";
    public static final String DEFAULT_INVITE_FRIENDS_VALUE = "default_invite_friends_value";

    public AppConstants() {}

    public static final String LOCAL_HOST = "192.168.0.7";
    public static final String WEBSITE_URL = "http://192.168.0.7/geekdashboard-material/";

    //ADMOB
    public static final String PUB_ID = "";
    public static final String PRIVACY_POLICY = "";

    //ANALYTICA
    public static final String ANALYTICS_TRACKING_ID = ""; //UPDATE IN XML RES DIR



    public static final boolean CATEGORIES = true;
    public static final boolean BOOKMARKS = true;
    public static final boolean COMMENTS = false;
    public static final boolean ABOUT = false;
    public static final boolean ABMOB = true;
    public static final boolean ANALYTICS = false;
    public static final boolean PUSH_NOTIFICATIONS = true;



    public static final int DATABASE_VERSION = 1;

    public static final int SPLASH_SCREEN_TIME_OUT = 1500;

    /* LOG TYPES */
    public static final String DEBUG_LOG = "debug";
    public static final String INFO_LOG = "info";
    public static final String ERROR_LOG = "error";

    /* Bundle Constants */

    public static final String BUNGLE_ARTICLE_ID = "article_id";

    public static final int METHOD_GET = 0;
    public static final int METHOD_POST = 1;

    //Toolbar titles
    public static final String BOOKMARKS_TOOLBAR_TITLE = "Bookmarks";
    public static final String CATEGORIES_TOOLBAR_TITLE = "Categories";

    // Activity Referrer
    public static final String INCOMING_SOURCE = "incoming_source";
    public static final String ACTIVITY_SOURCE = "activity_source";
    public static final String COMING_FROM_RECENT_ARTICLES = "recent_articles";
    public static final String COMING_FROM_BOOKMARKS = "bookmarks";
    public static final String COMING_FROM_CATEGORIES = "categories";
    public static final String COMING_FROM_ABOUT = "about";
    public static final String COMING_FROM_ABOUT_CREDITS = "about_credits";
    public static final String COMING_FROM_SEARCH = "search";
    public static final String COMING_FROM_NOTIFICATION = "notification";
    public static final String COMING_FROM_ADAPTER = "adapter";

    public static final String CATEGORY = "category";

    //Request Types
    public static final String FULL_ARTICLE_REQUEST_TYPE = "fullArticleRequestType";
    public static final String SEARCH_REQUEST_TYPE = "searchRequestType";
    public static final String CATEGORIES_REQUEST_TYPE = "categoriesRequestType";
    public static final String ABOUT_REQUEST_TYPE = "aboutRequestType";
    public static final String RECENT_ARTICLES_REQUEST_TYPE = "recentArticlesRequestType";
    public static final String POST_COMMENTS_REQUEST_TYPE = "postCommentsRequestType";

    //Comments
    public static final String BUNDLE_POST_ID = "postid";

    //Notification Constants
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_MESSAGE = "message";
    public static final String NOTIFICATION_IMAGE = "image";
    public static final String NOTIFICATION_POST_URL = "post_url";

    public static final String NOTIFICATION_TYPE = "notification_type";
    public static final String NOTIFICATION_TYPE_ANNOUNCEMENT = "announcement";
    public static final String NOTIFICATION_TYPE_APP_UPDATE = "update";


    public static final String HIDE_VIEW = "wp_droid_hide_view_3%F8##b23b";
    public static final String LAYOUT_STYLE_TOP_IMAGE = "top_image";
    public static final String LAYOUT_STYLE_LEFT_IMAGE = "left_image";

    // Category Constants
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_COUNT = "category_count";
    public static final String CATEGORY_ID = "category_id";

}
