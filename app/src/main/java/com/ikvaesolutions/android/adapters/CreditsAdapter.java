package com.ikvaesolutions.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.models.recyclerviews.CreditsModel;
import com.ikvaesolutions.android.utils.CommonUtils;
import java.util.List;

/**
 * Created by amar on 10/2/17.
 */

public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.MyViewHolder> {

    private Context mContext;
    Activity mActivity;
    private List<CreditsModel> creditsModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        LinearLayout root;

        private MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.credit_title);
            description = (TextView) view.findViewById(R.id.credit_description);
            root = (LinearLayout) view.findViewById(R.id.credit_root);
        }
    }

    public CreditsAdapter(Context mContext, List<CreditsModel> creditsModelList, Activity mActivity) {
        this.mContext = mContext;
        this.creditsModelList = creditsModelList;
        this.mActivity = mActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.credits_card_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CreditsModel creditsModel = creditsModelList.get(position);
        holder.title.setText(creditsModel.getTitle());
        holder.description.setText(creditsModel.getDescription());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.openChromeCustomTab(mActivity, Uri.parse(creditsModelList.get(holder.getAdapterPosition()).getUrl()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return creditsModelList.size();
    }

}
