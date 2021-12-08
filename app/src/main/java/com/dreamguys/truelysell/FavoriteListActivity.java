package com.dreamguys.truelysell;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dreamguys.truelysell.adapters.FavoriteListAdapter;
import com.dreamguys.truelysell.datamodel.BaseResponse;
import com.dreamguys.truelysell.datamodel.EmptyData;
import com.dreamguys.truelysell.datamodel.FavoriteListResponse;
import com.dreamguys.truelysell.network.ApiClient;
import com.dreamguys.truelysell.network.ApiInterface;
import com.dreamguys.truelysell.utils.AppConstants;
import com.dreamguys.truelysell.utils.AppUtils;
import com.dreamguys.truelysell.utils.PreferenceStorage;
import com.dreamguys.truelysell.utils.ProgressDlg;
import com.dreamguys.truelysell.utils.RetrofitHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class FavoriteListActivity extends BaseActivity implements RetrofitHandler.RetrofitResHandler {

    @BindView(R.id.favRecycler)
    RecyclerView favRecycler;
    FavoriteListAdapter adapter;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.refreshFav)
    SwipeRefreshLayout refreshFav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        ButterKnife.bind(this);

        uiInit();
    }

    private void uiInit() {
        setToolBarTitle("Favorites");
        ivSearch.setVisibility(View.GONE);
        ivUserlogin.setVisibility(View.GONE);

        refreshFav.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFav.setRefreshing(true);
                callFavList();
            }
        });

        callFavList();
    }

    private void callFavList() {
        if (AppUtils.isNetworkAvailable(FavoriteListActivity.this)) {
            ProgressDlg.showProgressDialog(FavoriteListActivity.this, null, null);
            ApiInterface apiService =
                    ApiClient.getClientNoHeader().create(ApiInterface.class);
            try {
                String token = AppConstants.DEFAULTTOKEN;
                if (PreferenceStorage.getKey(AppConstants.USER_TOKEN) != null) {
                    token = PreferenceStorage.getKey(AppConstants.USER_TOKEN);
                }
                Call<FavoriteListResponse> classificationCall = apiService.getFavoriteList(token);
                RetrofitHandler.executeRetrofit(FavoriteListActivity.this, classificationCall,
                        AppConstants.FAVORITELIST,
                        this, false);
            } catch (Exception e) {
                ProgressDlg.dismissProgressDialog();
                e.printStackTrace();
            }

        } else {
            AppUtils.showToast(FavoriteListActivity.this, getString(R.string.txt_enable_internet));
        }
    }

    @Override
    public void onSuccess(Object myRes, boolean isLoadMore, String responseType) {
        switch (responseType) {
            case AppConstants.FAVORITELIST:
                FavoriteListResponse response = (FavoriteListResponse) myRes;
                if (response.getData().getUserfavorites() != null && !response.getData().getUserfavorites().isEmpty()) {
                    favRecycler.setVisibility(View.VISIBLE);
                    refreshFav.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                    adapter = new FavoriteListAdapter(FavoriteListActivity.this, response.getData().getUserfavorites());
                    favRecycler.setAdapter(adapter);
                    refreshFav.setRefreshing(false);
                } else {
                    favRecycler.setVisibility(View.GONE);
                    refreshFav.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                }
                break;

            case AppConstants.FAVORITE:
                BaseResponse res = (BaseResponse) myRes;
                Toast.makeText(FavoriteListActivity.this,
                        res.getResponseHeader().getResponseMessage(), Toast.LENGTH_SHORT).show();
                callFavList();
                break;
        }

    }

    @Override
    public void onResponseFailure(Object myRes, boolean isLoadMore, String responseType) {
        ProgressDlg.dismissProgressDialog();
        favRecycler.setVisibility(View.GONE);
        refreshFav.setVisibility(View.GONE);
        tvNoData.setVisibility(View.VISIBLE);
        refreshFav.setRefreshing(false);
    }

    @Override
    public void onRequestFailure(Object myRes, boolean isLoadMore, String responseType) {
        ProgressDlg.dismissProgressDialog();
        favRecycler.setVisibility(View.GONE);
        refreshFav.setVisibility(View.GONE);
        tvNoData.setVisibility(View.VISIBLE);
        refreshFav.setRefreshing(false);
    }


    public void callFav(String serviceId, String type) {
        if (AppUtils.isNetworkAvailable(FavoriteListActivity.this)) {
            ProgressDlg.showProgressDialog(FavoriteListActivity.this, null, null);
            ApiInterface apiService =
                    ApiClient.getClientNoHeader().create(ApiInterface.class);
            try {
                String token = AppConstants.DEFAULTTOKEN;
                if (PreferenceStorage.getKey(AppConstants.USER_TOKEN) != null) {
                    token = PreferenceStorage.getKey(AppConstants.USER_TOKEN);
                }
                Call<EmptyData> classificationCall = apiService.callFavorite(token, serviceId, type);
                RetrofitHandler.executeRetrofit(FavoriteListActivity.this, classificationCall, AppConstants.FAVORITE,
                        this, false);
            } catch (Exception e) {
                ProgressDlg.dismissProgressDialog();
                e.printStackTrace();
            }

        } else {
            AppUtils.showToast(FavoriteListActivity.this, getString(R.string.txt_enable_internet));
        }
    }
}
