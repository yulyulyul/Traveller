package jso.kpl.traveller.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import jso.kpl.traveller.R;


public class RouteOtherDetail extends AppCompatActivity {

    //private FavoriteCountryBinding binding;
    //private FavoriteCountry_VM FC_VM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_other_detail);


        /*binding = DataBindingUtil.setContentView(this, R.layout.favorite_country);

        FC_VM = new FavoriteCountry_VM(this);

        binding.setFCVM(FC_VM);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        // viewModel 세팅
        binding.getFCVM().init();*/
    }
}
