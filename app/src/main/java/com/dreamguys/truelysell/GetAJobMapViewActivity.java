package com.dreamguys.truelysell;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.dreamguys.truelysell.adapters.SearchServicesAdapter;
import com.dreamguys.truelysell.datamodel.Phase3.DAOSearchServices;
import com.dreamguys.truelysell.datamodel.Phase3.DAOViewAllServices;
import com.dreamguys.truelysell.network.ApiClient;
import com.dreamguys.truelysell.network.ApiInterface;
import com.dreamguys.truelysell.utils.AppConstants;
import com.dreamguys.truelysell.utils.AppUtils;
import com.dreamguys.truelysell.utils.PreferenceStorage;
import com.dreamguys.truelysell.utils.ProgressDlg;
import com.dreamguys.truelysell.utils.RetrofitHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;


public class GetAJobMapViewActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapLoadedCallback , RetrofitHandler.RetrofitResHandler{

        private Context mContext;
        private EditText searchPlaceEt;
        private Button saveLocationBtn;
        private GoogleMap mMap;
        private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;

    private ArrayList<Object> list = new ArrayList<>();

    private String strtype ="", strpage="", strsearchText ="";

    public List<DAOViewAllServices.ServiceList> itemsData = new ArrayList<>();
    public List<DAOSearchServices.Datum> itemsDatasearch = new ArrayList<>();

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_get_a_job_map_view);


            mContext = GetAJobMapViewActivity.this;

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_48);


//            if (getIntent().getStringExtra("type") != null) {
                strpage = getIntent().getStringExtra("page");
                strtype = getIntent().getStringExtra("type");
                strsearchText = getIntent().getStringExtra("value");
//            } else {
//                strtype = "new";
//            }


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //Location Permission already granted
                    buildGoogleApiClient();

                    if(mMap != null)
                        mMap.setMyLocationEnabled(true);
                } else {
                    //Request Location Permission
                    ///checkLocationPermission();
                }
            } else {
                buildGoogleApiClient();
                if(mMap != null)
                    mMap.setMyLocationEnabled(true);
            }

            if (strpage.equals("viewallservices")){
                getViewAllServices(strtype);
            } else {
                getSearchList(strsearchText);
            }


        }

        private void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(LocationServices.API)
                    .enableAutoManage(this, this)
                    .build();
        }



    public void getViewAllServices(String type) {
        if (AppUtils.isNetworkAvailable(GetAJobMapViewActivity.this)) {
            ProgressDlg.showProgressDialog(this, null, null);
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            try {
                String token = AppConstants.DEFAULTTOKEN;
                if (PreferenceStorage.getKey(AppConstants.USER_TOKEN) != null) {
                    token = PreferenceStorage.getKey(AppConstants.USER_TOKEN);
                }
                Call<DAOViewAllServices> classificationCall = apiService.getViewAllServices(type, token, PreferenceStorage.getKey(AppConstants.MY_LATITUDE), PreferenceStorage.getKey(AppConstants.MY_LONGITUDE));
                RetrofitHandler.executeRetrofit(GetAJobMapViewActivity.this, classificationCall, AppConstants.VIEWALLSERVICES, this, false);
            } catch (Exception e) {
                ProgressDlg.dismissProgressDialog();
                e.printStackTrace();
            }

        } else {
            AppUtils.showToast(GetAJobMapViewActivity.this, getString(R.string.txt_enable_internet));
        }
    }


    private void callCreateMarker() {



        for (int i = 0; i < itemsData.size(); i++) {

           /* mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(list
                    .get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude())))
                    .title(list.get(i).getJobName())); */
            if (!itemsData.get(i).getService_latitude().isEmpty() && !itemsData.get(i).getService_longitude().isEmpty()) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(itemsData
                        .get(i).getService_latitude()), Double.parseDouble(itemsData.get(i).getService_longitude())))
                        .title(itemsData.get(i).getServiceTitle())
//                        .icon(BitmapDescriptorFactory.fromBitmap
//                                (getMarkerBitmapFromView(itemsData.get(i).getUserImage()))))
                        .icon(BitmapDescriptorFactory.fromBitmap
                                (getMarkerBitmapFromView(itemsData.get(i).getServiceImage()))))
                        .setTag(i);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(5), 5000, null);
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(itemsData
//                        .get(i).getService_latitude()), Double.parseDouble(itemsData
//                        .get(i).getService_longitude()))));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(PreferenceStorage.getKey(AppConstants.MY_LATITUDE)), Double.parseDouble(PreferenceStorage.getKey(AppConstants.MY_LONGITUDE)))));

                // getMarkerBitmapFromView(list.get(i).getImageName());

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        int position = (int) (marker.getTag());


                        TextView PriceTv, TitleTv, Categrytv;
                        ImageView cancel, imageserv;
                        Button Openbt;

                        //will create a view of our custom dialog layout
                        View alertCustomdialog = LayoutInflater.from(GetAJobMapViewActivity.this).inflate(R.layout.mapdialog,null);
                        //initialize alert builder.
                        AlertDialog.Builder alert = new AlertDialog.Builder(GetAJobMapViewActivity.this);

                        //set our custom alert dialog to tha alertdialog builder
                        alert.setView(alertCustomdialog);
                        cancel = (ImageView)alertCustomdialog.findViewById(R.id.cancel_button);

                        imageserv = (ImageView)alertCustomdialog.findViewById(R.id.imageidd);




                        PriceTv = (TextView)alertCustomdialog.findViewById(R.id.pricetv);

                        Categrytv = (TextView)alertCustomdialog.findViewById(R.id.categrytv);



                        TitleTv = (TextView)alertCustomdialog.findViewById(R.id.titletv);
                        Openbt = (Button)alertCustomdialog.findViewById(R.id.openbt);

                        TitleTv.setText(itemsData.get(position).getServiceTitle());
                        Categrytv.setText(itemsData.get(position).getCategoryName());
                        PriceTv.setText(Html.fromHtml(itemsData.get(position).getCurrency()) + " " + itemsData.get(position).getServiceAmount());



                        imageserv.setImageDrawable(Drawable.createFromPath(itemsData.get(position).getServiceImage()));


                        final AlertDialog dialog = alert.create();
                        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        //finally show the dialog box in android all
                        dialog.show();
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        Openbt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                        Intent callServiceDetailAct = new Intent(mContext, ActivityServiceDetail.class);
                        callServiceDetailAct.putExtra(AppConstants.FROMPAGE, AppConstants.VIEWALL);
                        callServiceDetailAct.putExtra(AppConstants.SERVICEID, itemsData.get(position).getServiceId());
                        callServiceDetailAct.putExtra(AppConstants.SERVICETITLE, itemsData.get(position).getServiceTitle());
                        mContext.startActivity(callServiceDetailAct);
                            }
                        });


//                        int position = (int) (marker.getTag());
//
//                        Intent callServiceDetailAct = new Intent(mContext, ActivityServiceDetail.class);
//                        callServiceDetailAct.putExtra(AppConstants.FROMPAGE, AppConstants.VIEWALL);
//                        callServiceDetailAct.putExtra(AppConstants.SERVICEID, itemsData.get(position).getServiceId());
//                        callServiceDetailAct.putExtra(AppConstants.SERVICETITLE, itemsData.get(position).getServiceTitle());
//                        mContext.startActivity(callServiceDetailAct);
//
////                        startActivity(new Intent(GetAJobMapViewActivity.this,
////                                GetAJobDetailActivity.class).putExtra("jobId",
////                                list.get(position).getId())
////                                .putExtra("step", "0"));
                        return false;
                    }
                });
            }
        }


    }





    private void callCreateMarkersearch() {



        for (int i = 0; i < itemsDatasearch.size(); i++) {

           /* mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(list
                    .get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude())))
                    .title(list.get(i).getJobName())); */
            if (!itemsDatasearch.get(i).getServiceLatitude().isEmpty() && !itemsDatasearch.get(i).getServiceLongitude().isEmpty()) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(itemsDatasearch
                        .get(i).getServiceLatitude()), Double.parseDouble(itemsDatasearch.get(i).getServiceLongitude())))
                        .title(itemsDatasearch.get(i).getServiceTitle())
//                        .icon(BitmapDescriptorFactory.fromBitmap
//                                (getMarkerBitmapFromView(itemsData.get(i).getUserImage()))))
                        .icon(BitmapDescriptorFactory.fromBitmap
                                (getMarkerBitmapFromView(itemsDatasearch.get(i).getServiceImage()))))
                        .setTag(i);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 5000, null);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(PreferenceStorage.getKey(AppConstants.MY_LATITUDE)), Double.parseDouble(PreferenceStorage.getKey(AppConstants.MY_LONGITUDE)))));
                // getMarkerBitmapFromView(list.get(i).getImageName());

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        int position = (int) (marker.getTag());


                        TextView PriceTv, TitleTv, Categrytv;
                        ImageView cancel, imageserv;
                        Button Openbt;

                        //will create a view of our custom dialog layout
                        View alertCustomdialog = LayoutInflater.from(GetAJobMapViewActivity.this).inflate(R.layout.mapdialog,null);
                        //initialize alert builder.
                        AlertDialog.Builder alert = new AlertDialog.Builder(GetAJobMapViewActivity.this);

                        //set our custom alert dialog to tha alertdialog builder
                        alert.setView(alertCustomdialog);
                        cancel = (ImageView)alertCustomdialog.findViewById(R.id.cancel_button);

                        imageserv = (ImageView)alertCustomdialog.findViewById(R.id.imageidd);




                        PriceTv = (TextView)alertCustomdialog.findViewById(R.id.pricetv);

                        Categrytv = (TextView)alertCustomdialog.findViewById(R.id.categrytv);



                        TitleTv = (TextView)alertCustomdialog.findViewById(R.id.titletv);
                        Openbt = (Button)alertCustomdialog.findViewById(R.id.openbt);

                        TitleTv.setText(itemsDatasearch.get(position).getServiceTitle());
                        Categrytv.setText(itemsDatasearch.get(position).getCategoryName());
                        PriceTv.setText(Html.fromHtml(itemsDatasearch.get(position).getCurrency()) + " " + itemsDatasearch.get(position).getServiceAmount());



                        imageserv.setImageDrawable(Drawable.createFromPath(itemsDatasearch.get(position).getServiceImage()));


                        final AlertDialog dialog = alert.create();
                        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        //finally show the dialog box in android all
                        dialog.show();
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        Openbt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent callServiceDetailAct = new Intent(mContext, ActivityServiceDetail.class);
                                callServiceDetailAct.putExtra(AppConstants.FROMPAGE, AppConstants.VIEWALL);
                                callServiceDetailAct.putExtra(AppConstants.SERVICEID, itemsDatasearch.get(position).getServiceId());
                                callServiceDetailAct.putExtra(AppConstants.SERVICETITLE, itemsDatasearch.get(position).getServiceTitle());
                                mContext.startActivity(callServiceDetailAct);
                            }
                        });


//                        int position = (int) (marker.getTag());
//
//                        Intent callServiceDetailAct = new Intent(mContext, ActivityServiceDetail.class);
//                        callServiceDetailAct.putExtra(AppConstants.FROMPAGE, AppConstants.VIEWALL);
//                        callServiceDetailAct.putExtra(AppConstants.SERVICEID, itemsData.get(position).getServiceId());
//                        callServiceDetailAct.putExtra(AppConstants.SERVICETITLE, itemsData.get(position).getServiceTitle());
//                        mContext.startActivity(callServiceDetailAct);
//
////                        startActivity(new Intent(GetAJobMapViewActivity.this,
////                                GetAJobDetailActivity.class).putExtra("jobId",
////                                list.get(position).getId())
////                                .putExtra("step", "0"));
                        return false;
                    }
                });
            }
        }


    }


    public void getSearchList(String searchText) {
        if (AppUtils.isNetworkAvailable(this)) {
            ProgressDlg.showProgressDialog(this, null, null);
            ApiInterface apiService =
                    ApiClient.getClientNoHeader().create(ApiInterface.class);
            try {
                Call<DAOSearchServices> classificationCall = apiService.searchServices(AppConstants.DEFAULTTOKEN, searchText, PreferenceStorage.getKey(AppConstants.MY_LATITUDE), PreferenceStorage.getKey(AppConstants.MY_LONGITUDE));
                RetrofitHandler.executeRetrofit(this, classificationCall, AppConstants.SEARCHSERVICES, this, false);
            } catch (Exception e) {
                ProgressDlg.dismissProgressDialog();
                e.printStackTrace();
            }
        } else {
            AppUtils.showToast(getApplicationContext(), getString(R.string.txt_enable_internet));
        }
    }

    private Bitmap getMarkerBitmapFromView(String resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.mapImg);
        try {
            Picasso.get().load(AppConstants.BASE_URL+resId)
                    .placeholder(R.drawable.mappinic).into(markerImageView);
        } catch (Exception e) {
            markerImageView.setImageResource(R.drawable.mappinic);
        }

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapLoadedCallback(this);
    }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onSuccess(Object myRes, boolean isLoadMore, String responseType) {

        switch (responseType) {
            case AppConstants.VIEWALLSERVICES:
                DAOViewAllServices daoViewAllServices = (DAOViewAllServices) myRes;

                if (daoViewAllServices.getData() != null) {
                    itemsData.addAll(daoViewAllServices.getData().getServiceList());


                            callCreateMarker();

                }

                break;


            case AppConstants.SEARCHSERVICES:
                DAOSearchServices daoSearchServices = (DAOSearchServices) myRes;

                if (daoSearchServices.getData() != null) {
                    itemsDatasearch.addAll(daoSearchServices.getData());
                    callCreateMarkersearch();

                }


                break;
        }

    }

    @Override
    public void onResponseFailure(Object myRes, boolean isLoadMore, String responseType) {

    }

    @Override
    public void onRequestFailure(Object myRes, boolean isLoadMore, String responseType) {

    }
}

