package jso.kpl.traveller.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteListBinding;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.viewmodel.MainRouteListViewModel;

public class MainRouteList extends AppCompatActivity {

    String TAG = "Trav.MainRouteList.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RouteListBinding binding = DataBindingUtil.setContentView(this, R.layout.route_list);
        binding.setMainListVm(new MainRouteListViewModel(this, getSupportFragmentManager()));
        binding.setLifecycleOwner(this);

        MyPageItem myPageItem;

        //RouteSearch에서 받아 온 값 - > ViewModel
        if(getIntent() != null){
            myPageItem = (MyPageItem) getIntent().getSerializableExtra("req");

            Log.d(TAG, "받은 값이 있음" + myPageItem.getType());

            binding.getMainListVm().myPageItem.setValue(myPageItem);
        }



    }
}
