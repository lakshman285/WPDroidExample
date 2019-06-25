package com.ikvaesolutions.android.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.models.recyclerviews.CommentsModel;
import com.ikvaesolutions.android.others.GlideApp;
import com.ikvaesolutions.android.utils.CommonUtils;
import java.util.List;
import static android.view.View.GONE;


/**
 * Created by amar on 10/2/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private Context mContext;
    private List<CommentsModel> commentsModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView authorName, commentedDate, comment;
        ImageView authorProfilePicture;

        private MyViewHolder(View view) {
            super(view);
            authorName = (TextView) view.findViewById(R.id.author_name);
            comment = (TextView) view.findViewById(R.id.comment);
            commentedDate = (TextView) view.findViewById(R.id.commented_date);
            authorProfilePicture = (ImageView) view.findViewById(R.id.author_profile_picture);
        }
    }

    public CommentsAdapter(Context mContext, List<CommentsModel> commentsModelList) {
        this.mContext = mContext;
        this.commentsModelList = commentsModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CommentsModel commentsModel = commentsModelList.get(position);
        holder.authorName.setText(commentsModel.getAuthorName());
        if(commentsModel.getCommentedTime().equalsIgnoreCase(AppConstants.NO_COMMENTED_TIME)){
         holder.commentedDate.setVisibility(GONE);
        }else {
            holder.commentedDate.setVisibility(View.VISIBLE);
            holder.commentedDate.setText(commentsModel.getCommentedTime());
        }
        holder.commentedDate.setText(commentsModel.getCommentedTime());
        holder.comment.setText(CommonUtils.fromHtml(commentsModel.getComment()));
        if(commentsModel.getAuthorProfilePicture().equalsIgnoreCase(AppConstants.NO_COMMENTAR_IMAGE_FOUND)){
            holder.authorProfilePicture.setVisibility(GONE);
        }else {
            holder.authorProfilePicture.setVisibility(View.VISIBLE);
            GlideApp.with(mContext)
                    .load(commentsModel.getAuthorProfilePicture())
                    .placeholder(ContextCompat.getDrawable(mContext, R.mipmap.app_icon))
                    .into(holder.authorProfilePicture);
        }
    }

    @Override
    public int getItemCount() {
        return commentsModelList.size();
    }

}
