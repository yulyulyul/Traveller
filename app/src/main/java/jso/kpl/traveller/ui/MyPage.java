package jso.kpl.traveller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyPageBinding;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.network.WebService;
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

        //유저 데이터를 가져온다.
        if (getIntent() != null) {

            user = ((User) getIntent().getSerializableExtra("user"));

            App.Companion.setUserid(user.getU_userid());
            App.Companion.setUserNickname(user.getU_nick_name());
        }

        //Editing Post로 이동 후 결과를 반환하는 클릭 이벤트 버튼
        myPageVM.setOnEditingPostClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Go 포스트 작성");
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
            Log.d(TAG, "포스트 등록 안함");
        } else {
            Log.d(TAG, "포스트 등록함");
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
        }
    }
}
