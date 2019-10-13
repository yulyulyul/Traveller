package jso.kpl.traveller.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteListBinding;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.viewmodel.MainRouteListViewModel;

public class MainRouteList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RouteListBinding binding = DataBindingUtil.setContentView(this, R.layout.route_list);
        binding.setMainListVm(new MainRouteListViewModel(this, getSupportFragmentManager()));
        binding.setLifecycleOwner(this);

        //RouteSearch에서 받아 온 값 - > ViewModel
        if(getIntent() != null)
            binding.getMainListVm().setSearchReq((SearchReq) getIntent().getSerializableExtra("search"));

    }
}
