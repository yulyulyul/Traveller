package jso.kpl.traveller.ui.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyPageBinding;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.ui.AddOptionView;
import jso.kpl.traveller.ui.CountryList;
import jso.kpl.traveller.ui.DetailCountryInfo;
import jso.kpl.traveller.ui.DetailPost;
import jso.kpl.traveller.ui.EditingPost;
import jso.kpl.traveller.ui.LoginSelect;
import jso.kpl.traveller.ui.MainTab;
import jso.kpl.traveller.ui.ProfileManagement;
import jso.kpl.traveller.ui.RouteList;
import jso.kpl.traveller.ui.adapters.FlagRvAdapter;
import jso.kpl.traveller.viewmodel.MyPageViewModel;

public class MyPage extends Fragment implements FlagRvAdapter.OnFlagClickListener {

    Activity act = getActivity();

    String TAG = "Trav.MyPage.";

    MyPageBinding pageBinding;
    //MyPage의 ViewModel
    MyPageViewModel myPageVM;

    final int RETURN_PROFILE = 11;
    final int EDITING_POST = 22;
    final int RETURN_FAVORITE_COUNTRY = 33;
    final int RETURN_DETAIL_POST = 44;
    final int RETURN_MORE = 55;
    final int RETURN_ADD = 66;

    final int SUB_LIKE = 2;
    final int SUB_ENROLL = 3;
    final int SUB_CART = 4;

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
        if (getArguments() != null)
            user = (User) getArguments().getSerializable("user");
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

        pageBinding.getMyPageVm().mpProfileLD.setValue(user);

        pageBinding.getMyPageVm().flagRvAdapter.setOnFlagClickListener(this);

        //초기 값
        init();
        onMoreClicked();

        pageBinding.getMyPageVm().onProfileClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileManagement.class);
                startActivityForResult(intent, RETURN_PROFILE);
            }
        };

        pageBinding.getMyPageVm().onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageBinding.getMyPageVm().mpProfileLD.setValue(App.Companion.getUser());
                init();
                Log.d(TAG, "My Page 리프레쉬");
            }
        };

        pageBinding.getMyPageVm().POST_ID.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Intent intent = new Intent(getActivity(), DetailPost.class);
                intent.putExtra("p_id", integer);
                startActivityForResult(intent, RETURN_DETAIL_POST);
            }
        });

        pageBinding.getMyPageVm().onAddOptionViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddOptionView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 66);
            }
        };

        pageBinding.getMyPageVm().isCart.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    loadPostCall(pageBinding.cartPost, 4);
                } else {
                    pageBinding.cartPost.removeAllViews();
                }
            }
        });

        pageBinding.getMyPageVm().onMenuClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainTab) getActivity()).binding.mainDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        };

        return pageBinding.getRoot();
    }

    private void init() {
        pageBinding.getMyPageVm().countryCall();
        loadPostCall(pageBinding.likePost, 1);
        loadPostCall(pageBinding.recentPost, 2);
        loadPostCall(pageBinding.enrollPost, 3);

        if (App.Companion.getUser().getU_is_cart() == 1) {
            pageBinding.getMyPageVm().isCart.setValue(true);
        } else
            pageBinding.getMyPageVm().isCart.setValue(false);
    }

    @Override
    public void onFlagClicked(int ct_no) {
        Log.d(TAG, "선택 선호 국가: " + ct_no);
        Intent intent = new Intent(App.INSTANCE, DetailCountryInfo.class);
        intent.putExtra("ct_no", ct_no);
        startActivityForResult(intent, RETURN_FAVORITE_COUNTRY);
    }

    @Override
    public void onAddFlagClicked() {
        Log.d(TAG + "More", "선호 국가 더 보기");
        Intent intent = new Intent(getActivity(), CountryList.class);
        intent.putExtra("type", 1);
        startActivityForResult(intent, RETURN_FAVORITE_COUNTRY);
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
                Intent intent = new Intent(getActivity(), CountryList.class);
                intent.putExtra("type", 0);
                startActivityForResult(intent, RETURN_FAVORITE_COUNTRY);
            }
        };

        pageBinding.getMyPageVm().onMoreEnrollClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG + "More", "등록한 포스트 더 보기");
                Intent intent = new Intent(getActivity(), RouteList.class);
                intent.putExtra("req", new MyPageItem(App.Companion.getUser().getU_userid(), SUB_ENROLL));
                startActivityForResult(intent, RETURN_MORE);
            }
        };

        pageBinding.getMyPageVm().onMoreLikeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG + "More", "선호 포스트 더 보기");
                Intent intent = new Intent(App.INSTANCE, RouteList.class);
                intent.putExtra("req", new MyPageItem(App.Companion.getUser().getU_userid(), SUB_LIKE));
                startActivityForResult(intent, RETURN_MORE);
            }
        };

        pageBinding.getMyPageVm().onMoreCartClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG + "More", "선호 포스트 더 보기");
                Intent intent = new Intent(App.INSTANCE, RouteList.class);
                intent.putExtra("req", new MyPageItem(App.Companion.getUser().getU_userid(), SUB_CART));
                startActivityForResult(intent, RETURN_MORE);
            }
        };

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != getActivity().RESULT_OK)
            return;

        Log.d(TAG, "onActivityResult: " + requestCode);

        if (requestCode == RETURN_MORE) {

            pageBinding.getMyPageVm().mpProfileLD.setValue(App.Companion.getUser());

            loadPostCall(pageBinding.likePost, 1);
            loadPostCall(pageBinding.recentPost, 2);
            loadPostCall(pageBinding.enrollPost, 3);

            if (App.Companion.getUser().getU_is_cart() == 1)
                pageBinding.getMyPageVm().isCart.setValue(true);
            else
                pageBinding.getMyPageVm().isCart.setValue(false);
        }

        if (requestCode == RETURN_PROFILE) {
            if (data != null) {
                if (data.getIntExtra("result", 0) == 1) {
                    Intent intent = new Intent(getActivity(), LoginSelect.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else {
                pageBinding.getMyPageVm().mpProfileLD.setValue(App.Companion.getUser());
                ((MainTab) getActivity()).replaceFragment(newInstance(App.Companion.getUser()), "myPage");
                Log.d(TAG, "프로필:마이페이지 프래그먼트 갱신");
            }
        }

        if (requestCode == RETURN_DETAIL_POST) {

            Log.d(TAG, "My Page로 복귀");

            loadPostCall(pageBinding.likePost, 1);
            loadPostCall(pageBinding.recentPost, 2);
            loadPostCall(pageBinding.enrollPost, 3);

            if (App.Companion.getUser().getU_is_cart() == 1)
                pageBinding.getMyPageVm().isCart.setValue(true);
            else
                pageBinding.getMyPageVm().isCart.setValue(false);
        }

        if (requestCode == RETURN_FAVORITE_COUNTRY) {
            Log.d(TAG, "리셋 페이버릿 컨트리");
            init();
            pageBinding.getMyPageVm().flagRvAdapter.notifyDataSetChanged();
        }

        if (requestCode == EDITING_POST) {

            pageBinding.enrollPost.removeAllViews();
            pageBinding.getMyPageVm().postCall(act, pageBinding.enrollPost, 3);
            Toast.makeText(App.INSTANCE, "성공적으로 포스트 등록을 하셨습니다.", Toast.LENGTH_LONG).show();
        }

        //뷰 추가
        if (requestCode == RETURN_ADD) {
            int VIEW_TYPE = data.getIntExtra("add_view", 0);

            if (VIEW_TYPE == 1) {
                pageBinding.getMyPageVm().isCart.setValue(true);
            } else {
                pageBinding.getMyPageVm().isCart.setValue(false);
            }
        }
    }
}

