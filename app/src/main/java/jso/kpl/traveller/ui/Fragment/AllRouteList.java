package jso.kpl.traveller.ui.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.AllRouteListBinding;

import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.viewmodel.RouteListViewModel;

public class AllRouteList extends Fragment {

    String TAG = "Trav.AllRouteList.";

    MyPageItem item;

    AllRouteListBinding binding;
    RouteListViewModel routeListVm = new RouteListViewModel();

    public Fragment newInstance(MyPageItem item) {

        AllRouteList allRouteList = new AllRouteList();

        Bundle args = new Bundle();
        args.putSerializable("req", item);

        allRouteList.setArguments(args);

        return allRouteList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        routeListVm.setFm(getActivity().getSupportFragmentManager());

        binding = DataBindingUtil.inflate(inflater, R.layout.all_route_list, container, false);
        binding.setMainListVm(routeListVm);
        binding.setLifecycleOwner(this);

        if (getArguments() != null) {
            item = (MyPageItem) getArguments().getSerializable("req");
            binding.getMainListVm().setItem(item);
            binding.getMainListVm().searchByCondition(item);
        }

        binding.getMainListVm().addCategoryLayout(getActivity(), binding.categoryLayout);

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

        return binding.getRoot();
    }

}
