package jso.kpl.traveller.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyPageBinding;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.ui.adapters.FlagRvAdapter;
import jso.kpl.traveller.viewmodel.MyPageViewModel;

public class MyPage extends Fragment implements FlagRvAdapter.OnFlagClickListener {

    Activity act = getActivity();

    String TAG = "Trav.MyPage.";

    MyPageBinding pageBinding;
    //MyPage의 ViewModel
    MyPageViewModel myPageVM;

    final int EDITING_POST = 22;

    final int REUTRN_FAVORITE_COUNTRY = 55;

    final int SUB_LIKE = 2;
    final int SUB_ENROLL = 3;

    User user;

    public Fragment newInstance(User user) {

        MyPage myPage = new MyPage();

        Bundle args = new Bundle();
        args.putSerializable("user", user);

        myPage.setArguments(args);

        return myPage;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(getArguments() != null){
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "My Page 호출");

        myPageVM = new MyPageViewModel();

        //Editing Post로 이동 후 결과를 반환하는 클릭 이벤트 버튼
        myPageVM.setOnEditingPostClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Go 포스트 작성");
                startActivityForResult(new Intent(getActivity(), EditingPost.class), EDITING_POST);
            }
        });

        pageBinding = DataBindingUtil.inflate(inflater, R.layout.my_page, container, false);
        pageBinding.setMyPageVm(myPageVM);
        pageBinding.setLifecycleOwner(this);

        pageBinding.getMyPageVm().mpProfileLD.setValue(pageBinding.getMyPageVm().getHeadProfile(user));


        pageBinding.getMyPageVm().flagRvAdapter.setOnFlagClickListener(this);

        //초기 값
        pageBinding.getMyPageVm().countryCall();
        loadPostCall(pageBinding.likePost, 1);
        loadPostCall(pageBinding.recentPost, 2);
        loadPostCall(pageBinding.enrollPost, 3);

        pageBinding.getMyPageVm().onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Log.d(TAG, "My Page 리프레쉬");
                pageBinding.getMyPageVm().countryCall();
                loadPostCall(pageBinding.likePost, 1);
                loadPostCall(pageBinding.recentPost, 2);
                loadPostCall(pageBinding.enrollPost, 3);

                pageBinding.getMyPageVm().isRefresh.setValue(false);
            }
        };

        onMoreClicked();

        return pageBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

//        Log.d(TAG, "onRestart");
//
//        loadPostCall(pageBinding.likePost, 1);
//        loadPostCall(pageBinding.recentPost, 2);
    }

    @Override
    public void onFlagClicked(String country) {

        Log.d(TAG, "onFlagClicked: " + country);

        Intent intent = new Intent(getActivity(), RouteList.class);
        intent.putExtra("req", new MyPageItem(new SearchReq(country, 0, 1000000000), 0));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onAddFlagClicked() {

        Intent flagIntent = new Intent(getActivity(), FavoriteCountry.class);
        flagIntent.putExtra("type", 1);
        startActivityForResult(flagIntent, REUTRN_FAVORITE_COUNTRY);

        Log.d(TAG + "More", "선호 국가 더 보기");
    }

    public void loadPostCall(LinearLayout layout, int type) {

        if (layout.getChildCount() > 0)
            layout.removeAllViews();

        pageBinding.getMyPageVm().postCall(getActivity(), layout, type);
    }

    public void onMoreClicked() {

        pageBinding.getMyPageVm().onMoreCountryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent flagIntent = new Intent(App.INSTANCE, FavoriteCountry.class);
                flagIntent.putExtra("type", 0);
                startActivityForResult(flagIntent, REUTRN_FAVORITE_COUNTRY);
            }
        };

        pageBinding.getMyPageVm().onMoreEnrollClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(App.INSTANCE, RouteList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("req", new MyPageItem(App.Companion.getUser().getU_userid(), SUB_ENROLL));
                App.INSTANCE.startActivity(intent);

                Log.d(TAG + "More", "등록한 포스트 더 보기");
            }
        };

        pageBinding.getMyPageVm().onMoreLikeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(App.INSTANCE, RouteList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("req", new MyPageItem(App.Companion.getUser().getU_userid(), SUB_LIKE));
                App.INSTANCE.startActivity(intent);

                Log.d(TAG + "More", "선호 포스트 더 보기");
            }
        };

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != getActivity().RESULT_OK)
            return;

        if (requestCode == 55) {
            pageBinding.getMyPageVm().countryCall();
            pageBinding.getMyPageVm().flagRvAdapter.notifyDataSetChanged();
        }

        if (requestCode == EDITING_POST) {

            pageBinding.enrollPost.removeAllViews();
            pageBinding.getMyPageVm().postCall(act, pageBinding.enrollPost, 3);
            Toast.makeText(App.INSTANCE, "성공적으로 포스트 등록을 하셨습니다.", Toast.LENGTH_LONG).show();
        }
    }


}

