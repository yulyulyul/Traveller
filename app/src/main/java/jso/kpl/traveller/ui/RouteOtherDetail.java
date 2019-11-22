package jso.kpl.traveller.ui;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteOtherDetailBinding;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.viewmodel.RouteOtherDetail_VM;


public class RouteOtherDetail extends AppCompatActivity {

    private RouteOtherDetailBinding binding;
    private RouteOtherDetail_VM VM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.route_other_detail);

        VM = new RouteOtherDetail_VM(getIntent(), this);

        binding.setVM(VM);
        binding.setLifecycleOwner(this);

        binding.getVM().setRouteNodeAdapter(new RouteNodeAdapter(this, binding.routeRel));

        // viewModel 세팅
        binding.getVM().init();
    }
}
