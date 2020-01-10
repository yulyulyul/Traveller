package jso.kpl.traveller.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yongbeom.aircalendar.AirCalendarDatePickerActivity;

import java.util.ArrayList;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.DetailCountryInfoBinding;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.ui.adapters.ImageSideVpAdapter;
import jso.kpl.traveller.viewmodel.DetailCountryInfoViewModel;

public class DetailCountryInfo extends AppCompatActivity implements OnMapReadyCallback {

    String TAG = "Trav.DetailCtInfo.";

    Context context;

    GoogleMap map;

    private DetailCountryInfoBinding binding;
    private DetailCountryInfoViewModel fcInfoVm;

    int ctNo = 0;

    final int SELECT_POST_PERIOD = 33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        if(getIntent() !=null){
            ctNo = getIntent().getIntExtra("ct_no", 0);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.detail_country_info);

        fcInfoVm = new DetailCountryInfoViewModel();

        binding.setFvInfoVm(fcInfoVm);
        binding.setLifecycleOwner(this);

        binding.getFvInfoVm().loadCountryInfo(ctNo);

        binding.topActionBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.getFvInfoVm().setSideVpAdapter(new ImageSideVpAdapter(getSupportFragmentManager(), 5));

        binding.getFvInfoVm().onCalendarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(binding.getFvInfoVm().onCalendar(binding.getFvInfoVm().travelPeriod.getValue()), SELECT_POST_PERIOD);
            }
        };

        binding.getFvInfoVm().setOnAirlineSearchClickListener(new DetailCountryInfoViewModel.airlineSearchClickListener() {
            @Override
            public ArrayList<String> onClick() {
                ArrayList<String> arr = new ArrayList<>();
                if (binding.departureAirport.getText().toString().equals("")) {
                    App.Companion.sendToast("출발 공항을 입력하세요.");
                } else if (binding.entranceAirport.getText().toString().equals("")) {
                    App.Companion.sendToast("도착 공항을 입력하세요.");
                } else if (binding.textCalendar.getText().toString().equals("")) {
                    App.Companion.sendToast("여행 날짜를 입력하세요.");
                } else {
                    arr.add(binding.departureAirport.getText().toString().toUpperCase());
                    arr.add(binding.entranceAirport.getText().toString().toUpperCase());
                    arr.add(binding.textCalendar.getText().toString().split("~")[0].trim());
                    arr.add(binding.textCalendar.getText().toString().split("~")[1].trim());
                }

                return arr;
            }

            @Override
            public void onSearching() {
                binding.airlineBtn.setText("검색 중");
                binding.getFvInfoVm().isSearching.setValue(true);
            }

            @Override
            public void onResetClick() {
                if (binding.getFvInfoVm().isAirlineResult.getValue()) {
                    binding.airlineBtn.setText("돌아가기");
                } else {
                    binding.airlineBtn.setText("검색");
                }
            }
        });

        binding.getFvInfoVm().airlineItem.observe(this, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject jsonObject) {
                if (jsonObject.size() != 0) {
                    JsonObject departure = (JsonObject) jsonObject.get("departure");
                    binding.departureAirline.setText(departure.get("airline").getAsString());
                    JsonObject d_start = (JsonObject) departure.get("start");
                    binding.departureStartAirport.setText(d_start.get("airport").getAsString());
                    binding.departureStartTime.setText(d_start.get("time").getAsString());
                    JsonObject d_arrive = (JsonObject) departure.get("arrive");
                    binding.departureArriveAirport.setText(d_arrive.get("airport").getAsString());
                    binding.departureArriveTime.setText(d_arrive.get("time").getAsString());
                    binding.departureDuration.setText(departure.get("duration").getAsString());
                    binding.departureVia.setText(departure.get("via").getAsString());

                    JsonObject entrance = (JsonObject) jsonObject.get("entrance");
                    binding.entranceAirline.setText(entrance.get("airline").getAsString());
                    JsonObject e_start = (JsonObject) entrance.get("start");
                    binding.entranceStartAirport.setText(e_start.get("airport").getAsString());
                    binding.entranceStartTime.setText(e_start.get("time").getAsString());
                    JsonObject e_arrive = (JsonObject) entrance.get("arrive");
                    binding.entranceArriveAirport.setText(e_arrive.get("airport").getAsString());
                    binding.entranceArriveTime.setText(e_arrive.get("time").getAsString());
                    binding.entranceDuration.setText(entrance.get("duration").getAsString());
                    binding.entranceVia.setText(entrance.get("via").getAsString());

                    binding.price.setText(jsonObject.get("price").getAsString() + "원");
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("is_favorite", binding.getFvInfoVm().isFavorite.getValue());

        setResult(RESULT_OK, intent);
        map.clear();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map  = googleMap;

        binding.getFvInfoVm().countryItem.observe(DetailCountryInfo.this, new Observer<Country>() {
            @Override
            public void onChanged(Country country) {
                String str = binding.getFvInfoVm().countryItem.getValue().getCt_latlng().replace(" ", "");

                String[] latLngStr = str.split(",");

                final LatLng latLng = new LatLng(Double.parseDouble(latLngStr[0]), Double.parseDouble(latLngStr[1]));

                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);
                markerOptions.title(binding.getFvInfoVm().countryItem.getValue().getCt_name());
                map.addMarker(markerOptions);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 3));

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                        return false;
                    }
                });

                map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                });
            }
        });

        binding.getFvInfoVm().POST_ID.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                Log.d(TAG, "onChanged: " + integer);
                Intent intent = new Intent(context, DetailPost.class);
                intent.putExtra("p_id", integer);
                startActivityForResult(intent, 44);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;

        Log.d(TAG, "onActivityResult: " + requestCode);
        if(requestCode == 44){
            binding.getFvInfoVm().relationPostCall();

            binding.getFvInfoVm().isReload.observe(DetailCountryInfo.this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if(aBoolean){
                        binding.spVp.setCurrentItem(binding.getFvInfoVm().CURRENT_POS, false);
                        binding.getFvInfoVm().isReload.setValue(false);
                    }

                }
            });

        }

        if (requestCode == SELECT_POST_PERIOD) {
            if (data != null) {

                String startDate = data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_START_DATE);
                String endDate = data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_END_DATE);

                if (binding.getFvInfoVm().travelPeriod.getValue() != null) {
                    Log.d(TAG, "onActivityResult: Not Null");

                    if (!startDate.equals("") && !startDate.equals(binding.getFvInfoVm().travelPeriod.getValue()[0])) {
                        binding.getFvInfoVm().travelPeriod.getValue()[0] = startDate;

                        binding.getFvInfoVm().travelResult.setValue(startDate + " ~ " + endDate);

                    }

                    if (!endDate.equals("") && !endDate.equals(binding.getFvInfoVm().travelPeriod.getValue()[1])) {
                        binding.getFvInfoVm().travelPeriod.getValue()[1] = endDate;

                        binding.getFvInfoVm().travelResult.setValue(startDate + " ~ " + endDate);

                    }
                } else {
                    binding.getFvInfoVm().travelPeriod.setValue(new String[]{startDate, endDate});
                    binding.getFvInfoVm().travelResult.setValue(startDate + " ~ " + endDate);
                }
            }
        }
    }
}
