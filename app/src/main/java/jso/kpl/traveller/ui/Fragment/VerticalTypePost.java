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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.VerticalTypePostBinding;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.viewmodel.RouteListViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerticalTypePost extends Fragment {

    String TAG = "Trav.VerticalView.";

    VerticalTypePostBinding binding;
    RouteListViewModel routeListVm;
    public VerticalTypePost(RouteListViewModel routeListVm) {
        // Required empty public constructor
        this.routeListVm = routeListVm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.vertical_type_post, container, false);
        binding.setVerticalVm(routeListVm);
        binding.setLifecycleOwner(this);

        binding.getVerticalVm().onVerticalScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                if(itemTotalCount < 12){
                    Log.d(TAG, "onScrollStateChanged: ");
                } else{
                    if(itemTotalCount == lastVisibleItemPosition){

                        binding.getVerticalVm().searchByCondition((MyPageItem) getActivity().getIntent().getSerializableExtra("req"));
                        Log.d(TAG, "onScrollStateChanged: 마지막이야");
                    }
                }

            }
        };

        return binding.getRoot();
    }

}
