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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.PostListBinding;
import jso.kpl.traveller.ui.adapters.PostRouteListAdapter;
import jso.kpl.traveller.viewmodel.MainRouteListViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostRouteList extends Fragment {


    public PostRouteList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PostListBinding binding = DataBindingUtil.inflate(inflater, R.layout.post_list, container,false);

        binding.setPostVm(new MainRouteListViewModel(getActivity(), getActivity().getSupportFragmentManager()));
        return binding.getRoot();
    }

    @BindingAdapter({"setPostAdapter"})
    public static void onBindRvAdapter(RecyclerView recyclerView, PostRouteListAdapter adapter){

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter({"circleImgLoad"})
    public static void circleImgLoad(ImageView iv, String imgUri) {

        RequestOptions options
                = RequestOptions.bitmapTransform(new CircleCrop()).error(R.drawable.i_blank_person_icon);

        Glide.with(iv.getContext())
                .load(imgUri)
                .apply(options)
                .into(iv);
    }
}
