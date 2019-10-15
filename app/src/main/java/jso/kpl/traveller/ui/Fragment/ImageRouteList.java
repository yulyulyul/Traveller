package jso.kpl.traveller.ui.Fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.ImageListBinding;
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



}
