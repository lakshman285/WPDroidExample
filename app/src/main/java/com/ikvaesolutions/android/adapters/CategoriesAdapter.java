package com.ikvaesolutions.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.models.recyclerviews.CategoriesModel;
import com.ikvaesolutions.android.view.activity.CategoriesActivity;
import java.util.List;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    private Context context;
    private List<CategoriesModel> categoriesModelList;

    public CategoriesAdapter(Context mContext, List<CategoriesModel> categoriesModelList) {
        this.context = mContext;
        this.categoriesModelList = categoriesModelList;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout categoryItemLayout;
        TextView categoryName, categoryCount, categoryImageText;
        MyViewHolder(View itemView) {
            super(itemView);
            categoryItemLayout = itemView.findViewById(R.id.category_item_layout);
            categoryImageText = itemView.findViewById(R.id.category_image_text);
            categoryCount = itemView.findViewById(R.id.category_count);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_layout, parent, false);
        return new CategoriesAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final CategoriesModel categoriesModel = categoriesModelList.get(position);
        String finalCategoryName = categoriesModel.getCategoryName();
        holder.categoryName.setText(finalCategoryName);
        String categoryFirstChar = finalCategoryName.substring(0, 1);
        holder.categoryImageText.setText(categoryFirstChar);
        holder.categoryCount.setText(categoriesModel.getCategoryCount());
        holder.categoryItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(context, CategoriesActivity.class);
                categoryIntent.putExtra(AppConstants.CATEGORY_NAME, categoriesModel.getCategoryName());
                categoryIntent.putExtra(AppConstants.CATEGORY_COUNT, categoriesModel.getCategoryCount());
                categoryIntent.putExtra(AppConstants.CATEGORY_ID, categoriesModel.getCategoryId());
                categoryIntent.putExtra(AppConstants.INCOMING_SOURCE, AppConstants.COMING_FROM_CATEGORIES);
                context.startActivity(categoryIntent);
                }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesModelList.size();
    }

}
