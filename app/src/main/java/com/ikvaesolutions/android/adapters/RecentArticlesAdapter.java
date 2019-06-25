package com.ikvaesolutions.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.models.recyclerviews.RecentArticlesModel;
import com.ikvaesolutions.android.models.sqlite.BookmarksModel;
import com.ikvaesolutions.android.others.GlideApp;
import com.ikvaesolutions.android.utils.CommonUtils;
import com.ikvaesolutions.android.utils.DatabaseHandlerUtils;
import com.ikvaesolutions.android.view.activity.ArticleActivity;
import com.ikvaesolutions.android.view.fragment.ArticlesFragment;

import java.util.List;


/**
 * Created by amar on 10/2/17.
 */

public class RecentArticlesAdapter extends RecyclerView.Adapter<RecentArticlesAdapter.MyViewHolder> {

    String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private List<RecentArticlesModel> recentArticlesModelList;
    private DatabaseHandlerUtils databaseHandlerUtils;
    private String incomingSource, relatedPostsScrollDirection;
    private ArticlesFragment articlesFragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, postMeta, postMetaDate;
        ImageView thumbnail;
        AppCompatImageView bookmark;
        RelativeLayout articleLayout;
        LinearLayout postMainLayout;
        RelativeLayout bookmarksLayout;

        private MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            postMeta = (TextView) view.findViewById(R.id.post_meta);
            postMetaDate = (TextView) view.findViewById(R.id.post_meta_date);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            bookmark = (AppCompatImageView) view.findViewById(R.id.bookmark);
            articleLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
            postMainLayout = (LinearLayout)view.findViewById(R.id.root);
            bookmarksLayout = (RelativeLayout) view.findViewById(R.id.bookmarkLayout);

            if(incomingSource.equals(AppConstants.COMING_FROM_ARTICLE_ACTIVITY)){
                if(relatedPostsScrollDirection.equals(AppConstants.HORIZONTAL)){
                    postMainLayout.setLayoutParams(new LinearLayout.LayoutParams(600, LinearLayout.LayoutParams.WRAP_CONTENT));
                }else {
                    postMainLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }
            }else {
                postMainLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            if(AppConstants.BOOKMARKS) {
               bookmark.setVisibility(View.VISIBLE);
            } else {
                bookmark.setVisibility(View.GONE);
            }
        }
    }

    public RecentArticlesAdapter(Context mContext, List<RecentArticlesModel> recentArticlesModelList, String incomingSource, ArticlesFragment articlesFragment, DatabaseHandlerUtils databaseHandlerUtils) {
        this.mContext = mContext;
        this.recentArticlesModelList = recentArticlesModelList;
        this.databaseHandlerUtils = databaseHandlerUtils;
        this.incomingSource = incomingSource;
        this.articlesFragment = articlesFragment;
    }

    public RecentArticlesAdapter(Context mContext, List<RecentArticlesModel> recentArticlesModels, DatabaseHandlerUtils databaseHandlerUtils, String incomingSource, String relatedPostsScrollDirection) {
        this.mContext = mContext;
        this.recentArticlesModelList = recentArticlesModels;
        this.databaseHandlerUtils = databaseHandlerUtils;
        this.incomingSource = incomingSource;
        this.relatedPostsScrollDirection = relatedPostsScrollDirection;
    }

    @Override
    public int getItemViewType(int position) {
        switch (recentArticlesModelList.get(position).getLayoutStyle()) {
            case AppConstants.LAYOUT_STYLE_TOP_IMAGE:
                return 1;
            case AppConstants.LAYOUT_STYLE_LEFT_IMAGE:
                return 2;
            default:return 1;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        switch (viewType) {
            case 1://TOP IMAGE
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_image, parent, false);
                break;
            case 2://LEFT IMAGE
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_right_image_new, parent, false);
                break;
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_image, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final RecentArticlesModel recentArticlesModel = recentArticlesModelList.get(position);
        holder.title.setText(CommonUtils.fromHtml(recentArticlesModel.getTitle()));

        if(recentArticlesModel.getThumbnail().equals(AppConstants.HIDE_VIEW)) {
            holder.thumbnail.setVisibility(View.GONE);
        } else {
            GlideApp.with(mContext).load(recentArticlesModel.getThumbnail())
                    .thumbnail(0.5f)
                    .placeholder(mContext.getResources().getDrawable(R.mipmap.empty_photo))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnail);
        }

        if(databaseHandlerUtils.isBookmarked(String.valueOf(recentArticlesModel.getId()))) {
            holder.bookmark.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_filled_gray));
        } else {
            holder.bookmark.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_outline_gray));
        }

        boolean showAuthorName = !recentArticlesModel.getAuthorName().contains(AppConstants.HIDE_VIEW);
        boolean showDateTime = !recentArticlesModel.getTimestamp().equals(AppConstants.HIDE_VIEW);

        if(showAuthorName && showDateTime) {
            holder.postMeta.setVisibility(View.VISIBLE);
            holder.postMetaDate.setVisibility(View.VISIBLE);
            holder.postMeta.setText(recentArticlesModel.getAuthorName());
            holder.postMetaDate.setText(recentArticlesModel.getTimestamp());
        } else if(!showAuthorName && !showDateTime) {
            holder.postMeta.setVisibility(View.GONE);
            holder.postMetaDate.setVisibility(View.GONE);
        } else if (showAuthorName && !showDateTime) {
            holder.postMeta.setVisibility(View.VISIBLE);
            holder.postMeta.setText(recentArticlesModel.getAuthorName());
            holder.postMetaDate.setVisibility(View.GONE);
        } else {
            holder.postMeta.setVisibility(View.GONE);
            holder.postMetaDate.setVisibility(View.VISIBLE);
            holder.postMetaDate.setText(recentArticlesModel.getTimestamp());
        }

        holder.articleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openArticleActivity(recentArticlesModelList.get(holder.getAdapterPosition()).getId());
            }
        });

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openArticleActivity(recentArticlesModelList.get(holder.getAdapterPosition()).getId());
            }
        });

        holder.bookmarksLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postId = String.valueOf(recentArticlesModelList.get(holder.getAdapterPosition()).getId());
                if(databaseHandlerUtils.isBookmarked(postId)) {
                    databaseHandlerUtils.removeBookmark(postId);
                    holder.bookmark.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_outline_gray));
                    updateLayout(holder.getAdapterPosition());
                    if(!AppConstants.ABMOB) {
                        CommonUtils.showSnackbar(holder.bookmark, TAG, mContext.getResources().getString(R.string.bookmark_removed));
                    } else {
                        Toast toast = Toast.makeText(mContext, mContext.getResources().getString(R.string.bookmark_removed), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                        toast.show();
                    }
                    CommonUtils.logCustomEvent("Home Activity", "Bookmark Removed", recentArticlesModelList.get(holder.getAdapterPosition()).getTitle());
                } else {
                    databaseHandlerUtils.addBookmark(new BookmarksModel(
                            postId,
                            recentArticlesModelList.get(holder.getAdapterPosition()).getTitle(),
                            recentArticlesModelList.get(holder.getAdapterPosition()).getThumbnail(),
                            String.valueOf(SystemClock.currentThreadTimeMillis()),
                            recentArticlesModelList.get(holder.getAdapterPosition()).getAuthorName(),
                            recentArticlesModelList.get(holder.getAdapterPosition()).getTimestamp()
                    ));
                    holder.bookmark.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_filled_gray));

                    if(!AppConstants.ABMOB) {
                        CommonUtils.showSnackbar(holder.bookmark, TAG, mContext.getResources().getString(R.string.bookmark_added));
                    } else {
                        Toast toast = Toast.makeText(mContext, mContext.getResources().getString(R.string.bookmark_added), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                        toast.show();
                    }
                    CommonUtils.logCustomEvent("Home Activity", "Bookmark Added", recentArticlesModelList.get(holder.getAdapterPosition()).getTitle());
                }
            }
        });
    }

    private void openArticleActivity(Integer id) {
        Intent i = new Intent(mContext, ArticleActivity.class);
        i.putExtra(AppConstants.BUNGLE_ARTICLE_ID, id);
        i.putExtra(AppConstants.INCOMING_SOURCE, AppConstants.COMING_FROM_ADAPTER);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    private void updateLayout(int position) {
        if(incomingSource.equals(AppConstants.COMING_FROM_BOOKMARKS)) {
            notifyItemRemoved(position);
            recentArticlesModelList.remove(position);

            if(recentArticlesModelList.isEmpty()) {
                articlesFragment.noSearchResults();
            }
        }
    }

    @Override
    public int getItemCount() {
        return recentArticlesModelList.size();
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

}
