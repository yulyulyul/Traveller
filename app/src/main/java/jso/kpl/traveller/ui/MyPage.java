package jso.kpl.traveller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyPageBinding;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.viewmodel.MyPageViewModel;

public class MyPage extends AppCompatActivity {

    String TAG = "Trav.MyPage.";

    //MyPage의 ViewModel
    MyPageViewModel myPageVM;

    final int EDITING_POST = 22;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPageVM = new MyPageViewModel();

        if(getIntent() != null){

            user = ((User)getIntent().getSerializableExtra("user"));

//            Log.d(TAG, "onCreate: " + user.toString());
        }

        //Editing Post로 이동 후 결과를 반환하는 클릭 이벤트 버튼
        myPageVM.setOnEditingPostClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                startActivityForResult(new Intent(getApplicationContext(), EditingPost.class), EDITING_POST);
            }
        });

        MyPageBinding pageBinding = DataBindingUtil.setContentView(this, R.layout.my_page);
        pageBinding.setMyPageVm(myPageVM);
        pageBinding.setLifecycleOwner(this);

        pageBinding.getMyPageVm().init(user);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
