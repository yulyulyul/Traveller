package jso.kpl.traveller.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteListBinding;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.viewmodel.RouteListViewModel;

public class RouteList extends AppCompatActivity {



    String TAG = "Trav.RouteList.";

    MyPageItem item;

    RouteListBinding binding;
    RouteListViewModel routeListVm = new RouteListViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routeListVm.setFm(getSupportFragmentManager());

        binding = DataBindingUtil.setContentView(this, R.layout.route_list);
        binding.setMainListVm(routeListVm);
        binding.setLifecycleOwner(this);

        if(getIntent() != null){
            item = (MyPageItem) getIntent().getSerializableExtra("req");

            binding.getMainListVm().searchByCondition(item);
        }

        if(((MyPageItem) getIntent().getSerializableExtra("req")).getType() == 0){
            binding.categoryHorizontalLayout.setVisibility(View.VISIBLE);
            binding.getMainListVm().addCategoryLayout(this, binding.categoryLayout);
        }else{
            binding.categoryHorizontalLayout.setVisibility(View.GONE);
        }

    }

//    @Override
//    public void onRefresh() {
//        Log.d(TAG, "onRefresh: ");
//
//        binding.getMainListVm().postList.setValue(new ArrayList<ListItem>());
//        binding.getMainListVm().gridAdapter.removeItems();
//        binding.getMainListVm().verticalAdapter.removeItems();
//
//        Log.d(TAG, "onRefresh: " + ((MyPageItem) getIntent().getSerializableExtra("req")).toString());
//        binding.getMainListVm().searchByCondition((MyPageItem) getIntent().getSerializableExtra("req"));
//
//        binding.refreshLayout.setRefreshing(false);
//
//    }
}
