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
    }
}
