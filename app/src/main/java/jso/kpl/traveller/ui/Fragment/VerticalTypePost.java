package jso.kpl.traveller.ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.VerticalTypePostBinding;
import jso.kpl.traveller.viewmodel.RouteListViewModel;

public class VerticalTypePost extends Fragment {


    public VerticalTypePost() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        VerticalTypePostBinding binding = DataBindingUtil.inflate(inflater, R.layout.vertical_type_post, container, false);
        binding.setVerticalVm(new RouteListViewModel());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }




}
