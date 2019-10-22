package jso.kpl.traveller.ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.GridTypePostBinding;
import jso.kpl.traveller.viewmodel.RouteListViewModel;

public class GridTypePost extends Fragment {

    //Route List의 격자 타입의 포스트 리스트
    public GridTypePost() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        GridTypePostBinding binding = DataBindingUtil.inflate(inflater, R.layout.grid_type_post, container, false);
        binding.setGridVm(new RouteListViewModel());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

}
