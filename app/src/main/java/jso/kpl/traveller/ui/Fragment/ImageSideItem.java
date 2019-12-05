package jso.kpl.traveller.ui.Fragment;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.ImageSideItemBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageSideItem extends Fragment {

    ImageSideItemBinding binding;

    public ImageSideItem() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.image_side_item, container, false);

        if(getArguments() != null){
            Bundle args = getArguments();
            binding.setImgItem(args.getString("img"));
        }

        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

}
