package jso.kpl.traveller.ui.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.GridTypePostBinding;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.ui.RouteList;
import jso.kpl.traveller.viewmodel.RouteListViewModel;
import jso.kpl.traveller.viewmodel.RouteSearchViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GridTypePost extends Fragment {

    GridTypePostBinding binding;
    RouteListViewModel routeListVm;

    String TAG = "Trav.GridView.";

    //Route List의 격자 타입의 포스트 리스트
    public GridTypePost(RouteListViewModel routeListVm) {
        // Required empty public constructor

        this.routeListVm = routeListVm;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.grid_type_post, container, false);
        binding.setGridVm(routeListVm);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

}
