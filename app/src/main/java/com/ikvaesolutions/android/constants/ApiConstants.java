package com.ikvaesolutions.android.constants;

/**
 * Created by amarilindra on 25/04/17.
 */

public class ApiConstants {

    public static final String WEBSITE_URL = AppConstants.WEBSITE_URL;


    /* WP REST API */

    private static final String API_SLUG = "wp-json/wp/v2/";
    public static final String BASE_API = WEBSITE_URL + API_SLUG;

    //Common
    public static final String KEY_POST_ID = "id";
    public static final String RENDERED = "rendered";
    public static final String KEY_POST_TITLE = "title";
    public static final String DATE = "date";

    public static final String WP_DROID_OBJECT = "wp_droid";
    public static final String KEY_POST_FEATURED_IMAGE = "featured_image";
    public static final String RELATED_POST_AUTHOR_NAME = "author_name";
    public static final String RELATED_POST_ID = "post_id";
    public static final String KEY_IS_POST_EXCLUDED = "is_post_excluded";
    public static final String APP_CSS = "app_css";
    public static final String INTERNAL_LINKS = "internal_links";
    public static final String KEY_POSTS_LOOP_LIMIT = "posts_limit";

    // RECENT POSTS
    public static final String GET_RECENT_POSTS = BASE_API + "posts?wpdroid=true&per_page=%s";
    public static final String KEY_RECENT_ARTICLE_LAYOUT_STYLE = "layout_style";


    // CATEGORY WISE POSTS
    public static final String GET_SEARCH_RESULTS = BASE_API + "posts?wpdroid=true&per_page=%s&search=";

    // SEARCH RESULTS
    public static final String GET_CATEGORY_WISE_POSTS = BASE_API + "posts?wpdroid=true&per_page=%s&categories=";

    //Article Categories
    public static final String GET_CATEGORY_ARTICLES = BASE_API + "categories";

//    public static final String GET_ABOUT_WEBSITE = BASE_API + "about";
    public static final String GET_ABOUT_WEBSITE = AppConstants.WEBSITE_URL + "wp-json/wp-droid/about";

    //Article Content
    public static final String GET_ARTICLE_CONTENT = BASE_API + "posts/";
    public static final String CONTENT = "content";
    public static final String POST_LINK = "link";

    //Comments
    public static final String GET_POST_COMMENTS = BASE_API + "comments?wpdroid=true&per_page=20&post=";
    public static final String POST_NEW_COMMENT = BASE_API + "comments?wpdroid=true&post=";
    public static final String COMMENT_ID = "id";
    public static final String PARENT_COMMENT_ID = "parent";
    public static final String KEY_AUTHOR_NAME = "author_name";
    public static final String AUTHOR_ID = "author";
    public static final String AVATAR_URLS = "author_avatar_urls";
    public static final String AVATAR_URL_SIZE = "48";

    //Internal Links
    public static final String GET_INTERNAL_LINK_ID = WEBSITE_URL + "wp-json/wp-droid/url-to-id";
    public static final String GET_INTERNAL_LINK_HEADER_KEY = "url";
    public static final String KEY_STATUS = "status";

    //Categories Keys
    public static final String KEY_CATEGORY_ID = "id";
    public static final String KEY_CATEGORY_COUNT = "count";
    public static final String KEY_CATEGORY_NAME = "name";
    public static final String KEY_EXCLUDE_CATEGORY = "exclude_category";

    //About Keys

    public static final String KEY_ABOUT = "about";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_READ_MORE = "read_more";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_LINKEDIN = "linkedin";
    public static final String KEY_INSTAGRAM = "instagram";
    public static final String KEY_YOUTUBE = "youtube";
    public static final String KEY_GOOGLE_PLUS = "google_plus";
    public static final String KEY_TWITTER = "twitter";
    public static final String KEY_FACEBOOK = "facebook";

    //Related Posts
    public static final String KEY_RELATED_POSTS = "related_posts";
    public static final String RELATED_POSTS_STYLE = "related_posts_style";
    public static final String RELATED_POSTS_BASED_ON = "related_posts_based_on";
    public static final String RELATED_POSTS_COPY_TO_CLIPBOARD = "copy_to_clipboard";
    public static final String RELATED_POSTS_OPEN_BROWSER = "open_in_browser";
    public static final String RELATED_POST_COMMENTS = "comments";
    public static final String KEY_POSTS = "posts";
    public static final String KEY_SCROLL_DIRECTION = "scroll_direction";
    public static final String REPORT_EMAIL_ERROR = "report_error_email";
    public static final String KEY_DATE_TIME = "date_time";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_COMMENT_STATUS = "comment_status";
    public static final String KEY_COMMENTAR_IMAGE = "commenter_image";
    public static final String KEY_INVITE_FRIENDS = "invite_friends";
}
