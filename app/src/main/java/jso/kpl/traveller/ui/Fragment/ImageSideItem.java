package jso.kpl.traveller.ui.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.DetailCtPostBinding;
import jso.kpl.traveller.databinding.ImageSideItemBinding;
import jso.kpl.traveller.model.PostSideItem;
import jso.kpl.traveller.ui.DetailPost;

public class ImageSideItem extends Fragment {

    public ViewDataBinding binding;

    public MutableLiveData<View.OnClickListener> onPostClickListener = new MutableLiveData<>();

    public ImageSideItem() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            Bundle args = getArguments();

            int type = args.getInt("type", 0);

            if (type == 0) {

                binding = (ImageSideItemBinding) DataBindingUtil.inflate(inflater, R.layout.image_side_item, container, false);

                ((ImageSideItemBinding) binding).setImgItem(args.getString("img"));
                Log.d("Trav. 1", "onCreateView: ");
            } else {
                binding = (DetailCtPostBinding) DataBindingUtil.inflate(inflater, R.layout.detail_ct_post, container, false);

                final PostSideItem postSideItem = (PostSideItem) args.getSerializable("item");

                Log.d("Trav. 2", "onCreateView: ");
                ((DetailCtPostBinding) binding).setItem(postSideItem);

                onPostClickListener.observe(getActivity(), new Observer<View.OnClickListener>() {
                    @Override
                    public void onChanged(View.OnClickListener listener) {
                        ((DetailCtPostBinding) binding).setOnDetailPostClickListener(listener);
                    }
                });

            }
        }

        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }
}
