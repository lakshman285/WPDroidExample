package com.ikvaesolutions.android.view.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.adapters.CommentsAdapter;
import com.ikvaesolutions.android.constants.ApiConstants;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.listeners.VolleyJSONArrayRequestListener;
import com.ikvaesolutions.android.listeners.VolleyJSONObjectRequestListener;
import com.ikvaesolutions.android.models.recyclerviews.CommentsModel;
import com.ikvaesolutions.android.utils.CommonUtils;
import com.ikvaesolutions.android.utils.VolleyUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import static android.content.Context.WIFI_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    final String TAG = this.getClass().getSimpleName();
    Context mContext;
    RecyclerView commentsRecyclerView;
    CommentsAdapter commentsAdapter;
    List<CommentsModel> commentsModels;
    LinearLayoutManager mLayoutManager;
    ProgressBar addingCommentProgressBar;
    String postId, postCommentStatus,
            registeredAuthorName, registeredAuthorEmail, registeredAuthorProfilePicture, registerAuthorComment,
            commentedTime;
    Bundle bundle;
    private ArrayList<Integer> commentsShown = new ArrayList<>();
    private boolean userScrolled = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    ProgressBar loadMoreProgressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    Dialog addCommentDialogue;
    EditText enteredName, enteredEmail, enteredComment;
    TextInputLayout nameLayout, emailLayout;
    RelativeLayout noInternetConnectionRelativeLayout;
    LinearLayout addNewCommentLayout;
    AppCompatImageView messageImageView;
    TextView messageTextView, addNewCommentText;
    Button tryAgainButton, addCommentButton;

    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mContext = getContext();
        commentsRecyclerView = (RecyclerView) view.findViewById(R.id.comments_recycler_view);
        loadMoreProgressBar = (ProgressBar) view.findViewById(R.id.loadMore);

        addNewCommentLayout = (LinearLayout)view.findViewById(R.id.add_new_comment_layout);
        addNewCommentText = (TextView) view.findViewById(R.id.add_new_comment_text);
        commentsModels = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(mContext, commentsModels);
        mLayoutManager = new GridLayoutManager(mContext,1);
        commentsRecyclerView.setLayoutManager(mLayoutManager);
        commentsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        commentsRecyclerView.setAdapter(commentsAdapter);

        bundle = getArguments();
        postId = bundle.getString(AppConstants.BUNDLE_POST_ID);
        postCommentStatus = bundle.getString(AppConstants.BUNDLE_COMMENT_STATUS);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(loadMoreProgressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            loadMoreProgressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            loadMoreProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }

        noInternetConnectionRelativeLayout = (RelativeLayout) view.findViewById(R.id.category_no_internet_layout);
        messageImageView = (AppCompatImageView) view.findViewById(R.id.category_no_internet_image);
        messageTextView = (TextView) view.findViewById(R.id.category_no_internet_message);
        tryAgainButton = (Button) view.findViewById(R.id.category_no_internet_try_again_button);

        if(postCommentStatus.equals(getString(R.string.comments_open))){
            addNewCommentText.setText(R.string.add_new_comment);
        }else {
            addNewCommentText.setText(R.string.comments_disabled);
        }

        addNewCommentLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(postCommentStatus.equals(getString(R.string.comments_open))){
                    openBottomSheetDialogue();
                }else {
                    Toast.makeText(mContext, R.string.comments_are_disabled_for_this_article, Toast.LENGTH_SHORT).show();
                }
            }
        });

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTryAgain();
            }
        });

        hideProgressbar();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.colorPrimary), mContext.getResources().getColor(R.color.colorPrimaryDark));
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                addRecyclerViewLayout();
            }}
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void openBottomSheetDialogue() {
        View commentView = View.inflate(mContext, R.layout.bottom_sheet_comment_details, null);
        addCommentDialogue = new Dialog(mContext);
        enteredName = commentView.findViewById(R.id.comment_person_name);
        enteredEmail = commentView.findViewById(R.id.comment_person_email);
        enteredComment = commentView.findViewById(R.id.entered_comment);
        nameLayout = commentView.findViewById(R.id.input_layout_name);
        emailLayout = commentView.findViewById(R.id.input_layout_email);
        addCommentButton = commentView.findViewById(R.id.add_comment_button);
        addingCommentProgressBar= commentView.findViewById(R.id.adding_comment_progress_bar);
        enteredName.setBackgroundColor(Color.TRANSPARENT);
        enteredEmail.setBackgroundColor(Color.TRANSPARENT);
        enteredName.setFocusable(true);
        enteredName.setFocusableInTouchMode(true);
        enteredName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(addingCommentProgressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            addingCommentProgressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            addingCommentProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }
        addCommentDialogue.setContentView(commentView);
        addCommentDialogue.setCancelable(true);
        if(!CommonUtils.getCommentPersonName(mContext).isEmpty() &&  !CommonUtils.getCommentPersonEmail(mContext).isEmpty()){
            enteredName.setText(CommonUtils.getCommentPersonName(mContext));
            enteredEmail.setText(CommonUtils.getCommentPersonEmail(mContext));
            enteredComment.getText().clear();
        }
        addCommentDialogue.show();
        Window window = addCommentDialogue.getWindow();
        Objects.requireNonNull(window).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEnteredCommentDetails();
            }
        });
    }

    private void openEnteredCommentDetails() {
        boolean nameError, emailError;
        String name = enteredName.getText().toString().trim();
        String email = enteredEmail.getText().toString().trim();
        String comment = enteredComment.getText().toString().trim();

        registeredAuthorName = name.replaceAll(" ", "%20");
        registeredAuthorEmail = email.replaceAll(" ", "%20");
        registerAuthorComment = comment.replaceAll(" ", "%20");

        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );

        if(registeredAuthorName.isEmpty()){
            nameLayout.setErrorEnabled(true);
            nameLayout.setError(getString(R.string.name_cannot_be_blank));
            if(nameLayout.requestFocus()) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            nameError = true;
        } else {
            nameLayout.setErrorEnabled(false);
            nameError = false;
        }

        if(registeredAuthorEmail.isEmpty()){
            emailLayout.setErrorEnabled(true);
            emailLayout.setError(getString(R.string.email_cannot_be_blank));
            if(emailLayout.requestFocus()) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            emailError = true;
        } else if(EMAIL_ADDRESS_PATTERN.matcher(registeredAuthorEmail).matches()){
            emailLayout.setErrorEnabled(false);
            emailError = false;
        }else {
            emailLayout.setErrorEnabled(true);
            emailLayout.setError(getString(R.string.enter_valid_email_adress));
            if(emailLayout.requestFocus()) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            emailError = true;
        }

        if(!nameError && !emailError){
            CommonUtils.setCommentPersonName(name, mContext);
            CommonUtils.setCommentPersonEmail(email, mContext);
            if(registerAuthorComment.isEmpty()) {
                CommonUtils.showSnackbar(enteredComment, TAG, mContext.getResources().getString(R.string.empty_comment_not_allowed));
            } else if (registerAuthorComment.length() < 10) {
                CommonUtils.showSnackbar(enteredComment, TAG, mContext.getResources().getString(R.string.comment_too_small));
            }else {
                sendCommentToWP();
            }
        }
    }

    private void showCommentBox() {

    }

    private void addRecyclerViewLayout() {
        if(!commentsModels.isEmpty()) {
            commentsModels.clear();
            commentsAdapter.notifyDataSetChanged();
        }
        if(!commentsShown.isEmpty()) {
            commentsShown.clear();
        }
        showComments();
        addOnScrollListener();
    }



    public void showComments() {
        swipeRefreshLayout.setRefreshing(true);
        showProgressbar();
        if(!commentsShown.contains(commentsModels.size())) {
            commentsShown.add(commentsModels.size());
        } else {
            CommonUtils.writeLog(AppConstants.ERROR_LOG,TAG,"DUPLICATES FOUND: "+commentsModels.size());
            if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            hideProgressbar();
            return;
        }
        CommonUtils.writeLog(AppConstants.ERROR_LOG,TAG, ApiConstants.GET_POST_COMMENTS + postId +"&offset="+commentsModels.size());
        VolleyUtils.makeJSONArrayRequest(null, ApiConstants.GET_POST_COMMENTS + postId +"&offset="+commentsModels.size() , AppConstants.METHOD_GET, AppConstants.POST_COMMENTS_REQUEST_TYPE, new VolleyJSONArrayRequestListener() {

            @Override
            public void onError(String error, String statusCode) {
                CommonUtils.writeLog(AppConstants.ERROR_LOG,TAG, "Status Code: " + statusCode + " Error: " + error);
                swipeRefreshLayout.setRefreshing(false);
                hideProgressbar();
                showNoInternetConnectionMessage();
            }

            @Override
            public void onResponse(JSONArray response) {
                CommonUtils.writeLog(AppConstants.INFO_LOG,TAG, response.toString());
                if(response.toString().equals("[]")) {
                    if(commentsModels.isEmpty()) {
                        noCommentsFound();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    hideProgressbar();
                    return;
                } else {
                    commentsFound();
                }
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject postComments = (JSONObject) response.get(i);
                        int commentId = postComments.getInt(ApiConstants.COMMENT_ID);
                        int parentCommentId = postComments.getInt(ApiConstants.PARENT_COMMENT_ID);
                        int authorId = postComments.getInt(ApiConstants.AUTHOR_ID);

                        String authorName = postComments.getString(ApiConstants.KEY_AUTHOR_NAME);
                        String commentDate = postComments.getString(ApiConstants.DATE);

                        JSONObject title = postComments.getJSONObject(ApiConstants.CONTENT);
                        String comment = title.getString(ApiConstants.RENDERED);

                        JSONObject avatarURLs = postComments.getJSONObject(ApiConstants.AVATAR_URLS);
                        String authorImage = avatarURLs.getString(ApiConstants.AVATAR_URL_SIZE);

                        CommentsModel newComment = new CommentsModel(
                                String.valueOf(commentId),
                                String.valueOf(parentCommentId),
                                authorName,
                                String.valueOf(authorId),
                                authorImage,
                                commentDate,
                                comment.replace("\n",""));

                        commentsModels.add(newComment);
                        commentsAdapter.notifyItemInserted(commentsModels.size());
                    }
                    hideNoInternetConnectionMessage();
                } catch (JSONException e) {
                    CommonUtils.writeLog(TAG,AppConstants.ERROR_LOG, e.toString());
                    showNoInternetConnectionMessage();
                }
                swipeRefreshLayout.setRefreshing(false);
                hideProgressbar();
            }
        });
    }

    private void addOnScrollListener() {
        commentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                if (userScrolled && (visibleItemCount + pastVisiblesItems) == totalItemCount) {
                    userScrolled = false;
                    showComments();
                }
            }
        });
    }

    private void showProgressbar() {
        loadMoreProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressbar() {
        loadMoreProgressBar.setVisibility(View.GONE);
    }

    private void sendCommentToWP() {

        if(!CommonUtils.isNetworkAvailable(mContext, TAG)) {
            CommonUtils.showSnackbar(enteredComment, TAG, mContext.getResources().getString(R.string.no_internet_connection));
            return;
        }

        WifiManager wm = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        addingCommentProgressBar.setVisibility(View.VISIBLE);
        addNewCommentLayout.setClickable(false);

        String url = ApiConstants.POST_NEW_COMMENT + postId + "&author_name=" + registeredAuthorName + "&content="+ registerAuthorComment + "&author_email=" + registeredAuthorEmail;

        VolleyUtils.makeJSONObjectRequest(null, url, AppConstants.METHOD_POST, AppConstants.POST_COMMENTS_REQUEST_TYPE, false, new VolleyJSONObjectRequestListener() {
            @Override
            public void onError(String error, String statusCode) {
                CommonUtils.writeLog(AppConstants.ERROR_LOG,TAG, "Error: "+ error + " Status: " + statusCode);
                if(addCommentDialogue.isShowing()){
                    addCommentDialogue.show();
                    enteredComment.setText(registerAuthorComment);
                    addingCommentProgressBar.setVisibility(View.GONE);
                    addNewCommentLayout.setClickable(true);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }else {
                    showNoInternetConnectionMessage();
                }
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    CommonUtils.writeLog(AppConstants.INFO_LOG,TAG, response.toString());
                    String commentId = String.valueOf(response.getInt(ApiConstants.COMMENT_ID));
                    String parentCommentId = String.valueOf(response.getInt(ApiConstants.PARENT_COMMENT_ID));
                    String authorId = String.valueOf(response.getInt(ApiConstants.PARENT_COMMENT_ID));
                    JSONObject title = response.getJSONObject(ApiConstants.CONTENT);
                    String commentPosted = title.getString(ApiConstants.RENDERED);
                    JSONObject wpDroidObject = response.getJSONObject(ApiConstants.WP_DROID_OBJECT);
                    if(wpDroidObject.has(ApiConstants.KEY_COMMENTAR_IMAGE)){
                        if(wpDroidObject.getBoolean(ApiConstants.KEY_COMMENTAR_IMAGE)){
                            JSONObject avatarURLs = response.getJSONObject(ApiConstants.AVATAR_URLS);
                            registeredAuthorProfilePicture = avatarURLs.getString(ApiConstants.AVATAR_URL_SIZE);
                        }else {
                            registeredAuthorProfilePicture = AppConstants.NO_COMMENTAR_IMAGE_FOUND;
                        }
                    }else {
                        registeredAuthorProfilePicture = AppConstants.NO_COMMENTAR_IMAGE_FOUND;
                    }
                    if(wpDroidObject.has(ApiConstants.KEY_DATE_TIME)){
                        commentedTime = wpDroidObject.getString(ApiConstants.KEY_DATE_TIME);
                    }else {
                        commentedTime = AppConstants.NO_COMMENTED_TIME;
                    }
                    CommentsModel comment = new CommentsModel(
                            commentId,
                            parentCommentId,
                            registeredAuthorName,
                            authorId,
                            registeredAuthorProfilePicture,
                            commentedTime,
                            commentPosted);
                    commentsModels.add(0,comment);
                    commentsAdapter.notifyItemInserted(0);
                    commentsRecyclerView.smoothScrollToPosition(0);
                    CommonUtils.showSnackbar(enteredComment, TAG, mContext.getResources().getString(R.string.commenet_added));
                    hideNoInternetConnectionMessage();
                }catch (JSONException e) {
                    CommonUtils.writeLog(AppConstants.ERROR_LOG, TAG, e.toString());
                    showNoInternetConnectionMessage();
                }
                addingCommentProgressBar.setVisibility(View.GONE);
                addCommentDialogue.dismiss();
                closeKeyboard();
                addNewCommentLayout.setClickable(true);
                closeKeyboard();
            }

        });
    }

    private void closeKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void handleTryAgain() {
        addRecyclerViewLayout();
    }

    private void showNoInternetConnectionMessage() {
        commentsRecyclerView.setVisibility(View.INVISIBLE);
        noInternetConnectionRelativeLayout.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.VISIBLE);
        messageTextView.setText(mContext.getResources().getString(R.string.no_internet_connection_message));
        messageImageView.setImageResource(R.drawable.ic_img_no_connection);
    }

    private void hideNoInternetConnectionMessage() {
        commentsRecyclerView.setVisibility(View.VISIBLE);
        noInternetConnectionRelativeLayout.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
    }

    public void noCommentsFound() {
        commentsRecyclerView.setVisibility(View.INVISIBLE);
        noInternetConnectionRelativeLayout.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.GONE);
        if(postCommentStatus.equals("open")){
            messageTextView.setText(R.string.no_comments_yet_initiate_description);
        }else {
            messageTextView.setText(R.string.comments_are_disabled_for_this_article);
        }
        messageImageView.setImageResource(R.drawable.ic_img_no_search_results);
    }

    private void commentsFound() {
        commentsRecyclerView.setVisibility(View.VISIBLE);
        noInternetConnectionRelativeLayout.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        addRecyclerViewLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        showCommentBox();
    }

}
