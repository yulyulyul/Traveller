package jso.kpl.traveller.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.FavoriteCountryInfoBinding;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.viewmodel.FavoriteCountryInfoViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteCountryInfo extends AppCompatActivity implements Callback {

    Callback callback;

    private FavoriteCountryInfoBinding binding;
    private FavoriteCountryInfoViewModel fcInfoVm;

    int ctNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callback = this;

        if(getIntent() !=null){
            ctNo = getIntent().getIntExtra("ct_no", 0);
        }


        binding = DataBindingUtil.setContentView(this, R.layout.favorite_country_info);

        fcInfoVm = new FavoriteCountryInfoViewModel();

        binding.setFvInfoVm(fcInfoVm);
        binding.setLifecycleOwner(this);

        // viewModel 세팅
        binding.getFvInfoVm().init();

        binding.getFvInfoVm().loadCountryInfo(ctNo);
    }


    @Override
    public void onResponse(Call call, Response response) {

        if(binding.getFvInfoVm().countryItem.getValue().ct_is_add_ld.getValue()){

            binding.getFvInfoVm().countryItem.getValue().ct_is_add_ld.setValue(false);

            setResult(RESULT_OK);

            App.Companion.sendToast("선호 국가가 취소되었습니다.");

        } else{
            binding.getFvInfoVm().countryItem.getValue().ct_is_add_ld.setValue(true);

            setResult(RESULT_OK);

            App.Companion.sendToast("선호 국가가 추가되었습니다.");
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        App.Companion.sendToast("서버 에러로 인해 로딩에 실패했습니다.");
    }
}
