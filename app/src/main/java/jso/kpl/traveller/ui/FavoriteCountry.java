package jso.kpl.traveller.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.FavoriteCountryBinding;
import jso.kpl.traveller.viewmodel.FavoriteCountry_VM;

public class FavoriteCountry extends AppCompatActivity {

    private FavoriteCountryBinding binding;
    private FavoriteCountry_VM FC_VM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.favorite_country);

        FC_VM = new FavoriteCountry_VM(this);

        binding.setFCVM(FC_VM);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        // viewModel 세팅
        binding.getFCVM().init();
    }
}
