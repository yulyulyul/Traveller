package jso.kpl.traveller.ui.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RevisePwdBinding;
import jso.kpl.traveller.ui.ProfileManagement;
import jso.kpl.traveller.util.JavaUtil;
import jso.kpl.traveller.util.RegexMethod;
import jso.kpl.traveller.viewmodel.ProfileManagementViewModel;

public class RevisePwd extends Fragment {

    RevisePwdBinding binding;
    ProfileManagementViewModel pmVm = new ProfileManagementViewModel();

    public RevisePwd() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.revise_pwd, container, false);
        binding.setPmVm(pmVm);
        binding.setLifecycleOwner(this);


        binding.getPmVm().isRevise.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 1){

                    SharedPreferences sp = App.INSTANCE.getSharedPreferences("auto_login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString("u_pwd", JavaUtil.returnSHA256(binding.getPmVm().updatePwd.getValue()));

                    editor.commit();

                    ((ProfileManagement)getActivity()).removeFragment(RevisePwd.this);
                } else {
                    App.Companion.sendToast("현재 비밀번호를 틀리셨습니다.");
                }
            }
        });

        return binding.getRoot();
    }
}
