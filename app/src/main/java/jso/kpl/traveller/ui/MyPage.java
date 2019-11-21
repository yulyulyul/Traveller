package jso.kpl.traveller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

            App.Companion.setUserid(user.getU_userid());
            App.Companion.setUserNickname(user.getU_nick_name());
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
        if (resultCode == 0) {
            System.out.println("나야나 : ");
            System.out.println("req Code : " + requestCode);
            System.out.println("res Code : " + resultCode);
            Toast.makeText(App.INSTANCE, "포스트 등록 안했음..", Toast.LENGTH_LONG).show();
        } else {
            System.out.println("req Code : " + requestCode);
            System.out.println("res Code : " + resultCode);
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
            Toast.makeText(App.INSTANCE, "포스트 등록 했음..", Toast.LENGTH_LONG).show();
        }
    }
}
