package jso.kpl.traveller.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.SmallPostBinding;
import jso.kpl.traveller.viewmodel.RouteOtherDetail_VM;

public class SmallPostDialog extends Dialog {

    private Context context;
    private RouteOtherDetail_VM vm;

    public SmallPostDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void call(RouteOtherDetail_VM vm) {

        // 액티비티의 타이틀바를 숨긴다.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        SmallPostBinding smallPostBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.small_post, null, false);
        setContentView(smallPostBinding.getRoot());
        smallPostBinding.setVM(vm);

        show();

        smallPostBinding.smallPostCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
