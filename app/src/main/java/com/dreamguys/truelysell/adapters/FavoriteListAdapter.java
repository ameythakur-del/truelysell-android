package com.dreamguys.truelysell.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dreamguys.truelysell.ActivityServiceDetail;
import com.dreamguys.truelysell.FavoriteListActivity;
import com.dreamguys.truelysell.FavouriteAnimationLib.LikeButton;
import com.dreamguys.truelysell.FavouriteAnimationLib.OnLikeListener;
import com.dreamguys.truelysell.R;
import com.dreamguys.truelysell.datamodel.FavoriteListResponse;
import com.dreamguys.truelysell.utils.AppConstants;
import com.dreamguys.truelysell.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {
    private Context context;
    private List<FavoriteListResponse.Data.Userfavorite> items;

    public FavoriteListAdapter(Context context, List<FavoriteListResponse.Data.Userfavorite> items) {
        this.context = context;
        this.items = items;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorites, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ViewHolder popularViewHolder = (ViewHolder) holder;

        if (AppUtils.isThemeChanged(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popularViewHolder.ivPopularServices.setBackgroundTintList(ColorStateList.valueOf(AppUtils.getPrimaryAppTheme(context)));
                popularViewHolder.tvCategory.setBackgroundTintList(ColorStateList.valueOf(AppUtils.getPrimaryAppTheme(context)));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callServiceDetailAct = new Intent(context, ActivityServiceDetail.class);
                callServiceDetailAct.putExtra(AppConstants.FROMPAGE, AppConstants.HOME);
                callServiceDetailAct.putExtra(AppConstants.SERVICEID, items.get(position).getServiceId());
                callServiceDetailAct.putExtra(AppConstants.SERVICETITLE, items.get(position).getServiceTitle());
                context.startActivity(callServiceDetailAct);
            }
        });

        popularViewHolder.tvCategory.setText(items.get(position).getCategoryName());
        popularViewHolder.tvServiceName.setText(items.get(position).getServiceTitle());

        popularViewHolder.icFav.setLiked(true);

        Glide.with(context)
                .load(AppConstants.BASE_URL + items.get(position).getServiceImage())
                .apply(new RequestOptions().error(R.drawable.ic_service_placeholder).placeholder(R.drawable.ic_service_placeholder).transforms(new CenterCrop(), new RoundedCorners(40)))
                .into(popularViewHolder.ivServiceImage);

        Glide.with(context)
                .load(R.drawable.bg_gradient_color_black)
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(40)))
                .into(popularViewHolder.ivGradient);


        popularViewHolder.icFav.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                //  ((FavoriteListActivity) context).callFav(items.get(position).getServiceId(), "1");
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                ((FavoriteListActivity) context).callFav(items.get(position).getServiceId(), "0");
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_popular_services)
        ImageView ivPopularServices;
        @BindView(R.id.tv_service_name)
        TextView tvServiceName;
        @BindView(R.id.tv_category)
        TextView tvCategory;
        @BindView(R.id.iv_service_image)
        ImageView ivServiceImage;
        @BindView(R.id.iv_gradient)
        ImageView ivGradient;
        @BindView(R.id.icFav)
        LikeButton icFav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
