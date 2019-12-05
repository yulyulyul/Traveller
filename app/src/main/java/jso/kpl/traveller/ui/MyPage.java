package jso.kpl.traveller.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyPageBinding;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.ui.adapters.FlagRvAdapter;
import jso.kpl.traveller.viewmodel.MyPageViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPage extends AppCompatActivity implements View.OnClickListener, FlagRvAdapter.OnFlagClickListener {

    Activity act = this;

    String TAG = "Trav.MyPage.";

    MyPageBinding pageBinding;
    //MyPage의 ViewModel
    MyPageViewModel myPageVM;

    final int EDITING_POST = 22;

    final int REUTRN_FAVORITE_COUNTRY = 55;

    final int SUB_COUNTRY = 1;
    final int SUB_FAVORITE = 2;
    final int SUB_ENROLL = 3;

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

        pageBinding = DataBindingUtil.setContentView(this, R.layout.my_page);
        pageBinding.setMyPageVm(myPageVM);
        pageBinding.setLifecycleOwner(this);

        pageBinding.getMyPageVm().mpProfileLD.setValue(pageBinding.getMyPageVm().getHeadProfile(user));
        pageBinding.getMyPageVm().countryCall();

        pageBinding.getMyPageVm().flagRvAdapter.setOnFlagClickListener(this);

        loadEnroll();
    }

    public void loadEnroll() {

        pageBinding.getMyPageVm().postCall(act, pageBinding.enrollPost, 1);
        pageBinding.getMyPageVm().postCall(act, pageBinding.likePost, 2);
    }

    @Override
    public void onFlagClicked(String country) {

        Log.d(TAG, "onFlagClicked: " + country);

        Intent intent = new Intent(getApplicationContext(), RouteList.class);
        intent.putExtra("req", new MyPageItem(new SearchReq(country, 0, 1000000000), 0));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onAddFlagClicked() {

        Intent flagIntent = new Intent(getApplicationContext(), FavoriteCountry.class);
        flagIntent.putExtra("type", 1);
        startActivityForResult(flagIntent, REUTRN_FAVORITE_COUNTRY);

        Log.d(TAG + "More", "선호 국가 더 보기");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == 55) {
            pageBinding.getMyPageVm().countryCall();
            pageBinding.getMyPageVm().flagRvAdapter.notifyDataSetChanged();
        }

        if (requestCode == EDITING_POST) {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
            Toast.makeText(App.INSTANCE, "성공적으로 포스트 등록을 하셨습니다.", Toast.LENGTH_LONG).show();
        }
    }

    //더보기 클릭 리스너
    @Override
    public void onClick(View v) {

        Intent intent = new Intent(App.INSTANCE, RouteList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        int id = v.getId();

        switch (id) {
            case R.id.mp_favorite_more:

                Intent flagIntent = new Intent(getApplicationContext(), FavoriteCountry.class);
                flagIntent.putExtra("type", 0);
                startActivityForResult(flagIntent, REUTRN_FAVORITE_COUNTRY);

                break;
            case R.id.mp_like_more:
                intent.putExtra("req", new MyPageItem(App.Companion.getUserid(), SUB_ENROLL));
                App.INSTANCE.startActivity(intent);
                Log.d(TAG + "More", "선호 포스트 더 보기");
                break;
            case R.id.mp_enroll_more:
                intent.putExtra("req", new MyPageItem(App.Companion.getUserid(), SUB_ENROLL));
                App.INSTANCE.startActivity(intent);
                Log.d(TAG + "More", "등록한 포스트 더 보기");
                break;

        }
    }
}
