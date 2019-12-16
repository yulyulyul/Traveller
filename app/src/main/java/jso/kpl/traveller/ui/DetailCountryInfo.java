package jso.kpl.traveller.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.DetailCountryInfoBinding;
import jso.kpl.traveller.viewmodel.DetailCountryInfoViewModel;
import retrofit2.Callback;

public class DetailCountryInfo extends AppCompatActivity {

    private DetailCountryInfoBinding binding;
    private DetailCountryInfoViewModel fcInfoVm;

    int ctNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent() !=null){
            ctNo = getIntent().getIntExtra("ct_no", 0);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.detail_country_info);

        fcInfoVm = new DetailCountryInfoViewModel();

        binding.setFvInfoVm(fcInfoVm);
        binding.setLifecycleOwner(this);

        // viewModel 세팅
        binding.getFvInfoVm().init();

        binding.getFvInfoVm().loadCountryInfo(ctNo);

        binding.topActionBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.countryInfo.countryWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.getFvInfoVm().countryWarningClicked();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
