package jso.kpl.traveller.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.SmallPostBinding;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.ui.Fragment.ImageSideItem;
import jso.kpl.traveller.ui.adapters.ImageSideVpAdapter;
import jso.kpl.traveller.viewmodel.DetailPostViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmallPostDialog extends DialogFragment {

    SmallPostBinding smallPostBinding;
    DetailPostViewModel detailPostVm = new DetailPostViewModel();

    public SmallPostDialog() {
    }

    public static SmallPostDialog getInstance(int sp_id) {

        SmallPostDialog spDialog = new SmallPostDialog();

        Bundle args = new Bundle();
        args.putInt("sp_id", sp_id);

        spDialog.setArguments(args);

        return spDialog;
    }

    public int getShownData() {
        return getArguments().getInt("sp_id", 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        smallPostBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.small_post, null, false);

        detailPostVm.setPostImgAdapter(new ImageSideVpAdapter(getChildFragmentManager(), 5));
        smallPostBinding.setSmallPostVm(detailPostVm);

        smallPostBinding.setLifecycleOwner(this);

        smallPostBinding.getSmallPostVm().smallPostCall(getShownData());

        smallPostBinding.smallPostCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return smallPostBinding.getRoot();
    }



}
