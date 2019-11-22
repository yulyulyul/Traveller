package jso.kpl.traveller.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.FavoriteCountryInfoBinding;
import jso.kpl.traveller.viewmodel.FavoriteCountryInfo_VM;

public class FavoriteCountryInfo extends AppCompatActivity {

    private FavoriteCountryInfoBinding binding;
    private FavoriteCountryInfo_VM FCInfo_VM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.favorite_country_info);

        FCInfo_VM = new FavoriteCountryInfo_VM(App.INSTANCE, getIntent());

        binding.setVM(FCInfo_VM);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        // viewModel 세팅
        binding.getVM().init();
    }
}
