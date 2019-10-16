package jso.kpl.traveller.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteOtherDetailBinding;
import jso.kpl.traveller.viewmodel.RouteOtherDetail_VM;


public class RouteOtherDetail extends AppCompatActivity {

    private RouteOtherDetailBinding binding;
    private RouteOtherDetail_VM VM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.route_other_detail);

        VM = new RouteOtherDetail_VM(this);

        binding.setVM(VM);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        // viewModel 세팅
        binding.getVM().init();
    }
}
