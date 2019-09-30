package jso.kpl.traveller.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

import jso.kpl.traveller.interfaces.DialogYNInterface;

public class CustomDialogViewModel extends BaseObservable {

    Context context;

    DialogYNInterface dialogYNInterface;

    public void setDialogYNInterface(DialogYNInterface dialogYNInterface){
        this.dialogYNInterface = dialogYNInterface;
    }

    public MutableLiveData<String> dialogTitle = new MutableLiveData<>();

    public CustomDialogViewModel(Context context) {

        this.context = context;

//(예정) 디바이스의 크기에 따라 다이얼로그의 사이즈를 변경
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//
//       DEVISE_WIDTH = displayMetrics.widthPixels/2;
//        DEVISE_HEIGHT = displayMetrics.heightPixels/4;
    }

    public void onNegativeBtnClicked(){
        dialogYNInterface.negativeBtn();
    }

    public void onPositiveBtnClicked(){
        dialogYNInterface.positiveBtn();
    }
}
