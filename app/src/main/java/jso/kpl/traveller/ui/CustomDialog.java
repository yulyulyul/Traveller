package jso.kpl.traveller.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.FragmentCustomDialogBinding;
import jso.kpl.traveller.viewmodel.CustomDialogViewModel;
import jso.kpl.traveller.interfaces.DialogYNInterface;

public class CustomDialog extends DialogFragment {

    DialogYNInterface dialogYNInterface;

    public void setDialogYNInterface(DialogYNInterface dialogYNInterface) {
        this.dialogYNInterface = dialogYNInterface;
    }

    public CustomDialog() {
        // Required empty public constructor
    }

    //초기화 시 프래그먼트는 빈 생성자를 가져야하기에 데이터 생성자 역할을 대신한다.
    public static CustomDialog getInstance(String title) {

        CustomDialog customDialog = new CustomDialog();

        Bundle args = new Bundle();
        args.putString("title", title);

        customDialog.setArguments(args);

        return customDialog;
    }

    //다이얼로그의 타이틀 값을 반환
    public String getShownData() {
        return getArguments().getString("title", "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return DataBindingUtil.inflate(inflater, R.layout.fragment_custom_dialog, container, false).getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentCustomDialogBinding binding
                = DataBindingUtil.getBinding(getView());

        binding.setCuVm(new CustomDialogViewModel(getActivity()));

        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        binding.getCuVm().dialogTitle.setValue(getShownData());

        binding.getCuVm().setDialogYNInterface(new DialogYNInterface() {
            @Override
            public void positiveBtn() {
                dialogYNInterface.positiveBtn();
            }

            @Override
            public void negativeBtn() {
                dialogYNInterface.negativeBtn();
            }
        });

    }

    /*
    사용방법
    public void onDialogLoading(String title){

        1. Bundle을 사용해서 다이얼로그의 텍스트를 준비한다.
        Bundle bundle = new Bundle();
        bundle.putString("title", title);

        2. 커스텀다이얼로그를 객체화한다.
        CustomDialog customDialog = new CustomDialog();

        3. 저장해둔 텍스트를 커스텀다이얼로그에 넘겨준다.
        customDialog.setArguments(bundle);

        4. 다이얼로그를 부른다.
        customDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Dialog_TAG");

        5. 다이얼로그에 만들어둔 인터페이스를 호출 이제 각각 버튼의 이벤트를 넣으면 된다.
         customDialog.setDialogYNInterface(new DialogYNInterface() {
                @Override
                public void positiveBtn() {
                    isAuth.setValue(true);
                }

                @Override
                public void negativeBtn() {
                    isAuth.setValue(false);
                }
            });
    }


     */



}
