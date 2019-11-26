package jso.kpl.traveller.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteListBinding;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.viewmodel.RouteListViewModel;

public class RouteList extends AppCompatActivity {

    String TAG = "Trav.RouteList.";

    RouteListBinding binding;
    RouteListViewModel routeListVm = new RouteListViewModel();

    SearchReq searchReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routeListVm.setFm(getSupportFragmentManager());

        binding = DataBindingUtil.setContentView(this, R.layout.route_list);
        binding.setMainListVm(routeListVm);
        binding.setLifecycleOwner(this);

        if(getIntent() != null){

            MyPageItem item = (MyPageItem) getIntent().getSerializableExtra("req");

            if(item.getType() == 0){
                searchReq = (SearchReq) item.getO();
                routeListVm.setSearchReq(searchReq);

                Log.d(TAG, "검색 조건: " + searchReq.toString());

                binding.getMainListVm().searchByCondition();
            } else{
                // 이후 More list 조건을 맞춰야함
            }

        }


        binding.executePendingBindings();

        Log.d(TAG, "테스트: " + binding.getMainListVm().getSearchReq().toString());
    }
}
