package jso.kpl.traveller.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteListBinding;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.ui.adapters.GridTypePostAdapter;
import jso.kpl.traveller.ui.adapters.VerticalTypePostAdapter;
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

        if (getIntent() != null) {
            item = (MyPageItem) getIntent().getSerializableExtra("req");
            binding.getMainListVm().setItem(item);
            binding.getMainListVm().searchByCondition(item);
        }

        if (((MyPageItem) getIntent().getSerializableExtra("req")).getType() == 0) {
            binding.categoryHorizontalLayout.setVisibility(View.VISIBLE);
            binding.getMainListVm().addCategoryLayout(this, binding.categoryLayout);
        } else {
            binding.categoryHorizontalLayout.setVisibility(View.GONE);
        }

        binding.getMainListVm().onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Log.d(TAG, "Route List - onRefresh");

                binding.getMainListVm().gridAdapter.removeItems();
                binding.getMainListVm().verticalAdapter.removeItems();

                binding.getMainListVm().lastPid = 0;

                binding.getMainListVm().postList.setValue(new ArrayList<ListItem>());

                binding.getMainListVm().searchByCondition(item);

                binding.getMainListVm().isRefresh.setValue(false);
            }
        };

        binding.getMainListVm().onGridScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(binding.getMainListVm().gt_post.isVisible()){
                    int lastVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                    if (itemTotalCount > 16) {
                        if (lastVisibleItemPosition  == itemTotalCount ) {
                            Log.d(TAG, "마지막 그리드: ");
                            binding.getMainListVm().searchByCondition(item);
                        }
                    }
                }

            }
        };

        binding.getMainListVm().onVerticalScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(binding.getMainListVm().vt_post != null && binding.getMainListVm().vt_post.isVisible()){
                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                    if (itemTotalCount > 16) {
                        if (lastVisibleItemPosition == itemTotalCount) {
                            Log.d(TAG, "마지막 버티컬: ");
                            binding.getMainListVm().searchByCondition(item);
                        }
                    }
                }


            }
        };
    }

}
