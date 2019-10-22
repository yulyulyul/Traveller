package jso.kpl.traveller.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteListBinding;
import jso.kpl.traveller.viewmodel.RouteListViewModel;

public class RouteList extends AppCompatActivity {

    String TAG = "Trav.RouteList.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RouteListBinding binding = DataBindingUtil.setContentView(this, R.layout.route_list);
        binding.setMainListVm(new RouteListViewModel());
        binding.setLifecycleOwner(this);

        binding.getMainListVm().setFm(getSupportFragmentManager());
    }
}
