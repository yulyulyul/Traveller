package jso.kpl.traveller.ui.Fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.ImageListBinding;
import jso.kpl.traveller.ui.adapters.ImageRouteListAdapter;
import jso.kpl.traveller.viewmodel.MainRouteListViewModel;

public class ImageRouteList extends Fragment {

    String TAG = "Trav.ImageRouteList.";

    public ImageRouteList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG+"onCreateView", "시작");

        ImageListBinding binding = DataBindingUtil.inflate(inflater, R.layout.image_list, container,false);

        binding.setImageVm(new MainRouteListViewModel(getActivity(), getActivity().getSupportFragmentManager()));
        binding.setLifecycleOwner(this);


        return binding.getRoot();
    }

    @BindingAdapter({"setImgAdapter"})
    public static void onBindRvAdapter(RecyclerView recyclerView, ImageRouteListAdapter adapter){

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 0);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        staggeredGridLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        recyclerView.setAdapter(adapter);

    }

    @BindingAdapter({"imgLoad"})
    public static void imgLoad(ImageView iv, String imgUri) {

        Glide.with(iv.getContext())
                .load(imgUri)
                .placeholder(R.drawable.i_blank_person_icon)
                .error(R.drawable.i_blank_person_icon)
                .into(iv);
    }
}
