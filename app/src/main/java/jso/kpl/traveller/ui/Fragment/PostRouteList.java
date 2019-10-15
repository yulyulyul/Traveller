package jso.kpl.traveller.ui.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.PostListBinding;
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
}
