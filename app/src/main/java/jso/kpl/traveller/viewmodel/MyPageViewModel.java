package jso.kpl.traveller.viewmodel;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.MyPageProfile;
import jso.kpl.traveller.model.MyPageSubtitle;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.ui.FavoriteCountry;
import jso.kpl.traveller.ui.MyPage;
import jso.kpl.traveller.ui.RouteList;
import jso.kpl.traveller.ui.RouteOtherDetail;
import jso.kpl.traveller.ui.RouteSearch;
import jso.kpl.traveller.ui.adapters.FlagRvAdapter;
import jso.kpl.traveller.ui.adapters.MyPageAdapter;

public class MyPageViewModel extends ViewModel implements MyPageAdapter.OnMyPageClickListener {

    String TAG = "Trav.MyPageViewModel.";

    //----------------------------------------------------------------------------------------------
    public MutableLiveData<User> user = new MutableLiveData<>();

    //[My Page]의 리사이클러 뷰 adapter
    public MyPageAdapter myPageAdapter;

    //(예정)Application class로 이동
    String imageUri = "android.resource://jso.kpl.traveller/drawable/i_blank_person_icon";

    //[My Page]의 리사이클러 뷰의 아이템 리스트
    // -> (Object o, int ViewType)의 리스트 형식
    MutableLiveData<List<MyPageItem>> itemList = new MutableLiveData<>();

    //[My Page - View Type: Profile]의 아이템
    // -> String 프로필 이미지, String 이메일
    public MutableLiveData<MyPageProfile> mp_profile = new MutableLiveData<>();

    //[My Page - View Type: subtitle]의 아이템
    // -> String 서브타이틀
    public MutableLiveData<MyPageSubtitle> mp_subtitle = new MutableLiveData<>();

    //[My Page - View Type: Flag]의 리사이클러 뷰의 adapter
    public FlagRvAdapter flagRvAdapter;

    //[My Page - View Type: Post]의 아이템
    // -> RePost 데이터 객체
    public MutableLiveData<RePost> mp_post = new MutableLiveData<>();

    //[My Page - View Type: Flag]의 아이템
    // -> Flag 뷰타입의 리사이클러 뷰의 아이템 리스트 - Object로 받는다. 1) 국기 2) post
    public MutableLiveData<List<String>> mp_flag = new MutableLiveData<>();

    public MutableLiveData<String> mp_flag_item = new MutableLiveData<>();

    public View.OnClickListener onEditingPostClickListener;

    public void setOnEditingPostClickListener(View.OnClickListener onEditingPostClickListener) {
        this.onEditingPostClickListener = onEditingPostClickListener;
    }

    //My Page의 각 뷰타입
    final int HEAD_PROFILE = 0;
    final int ROUTE_SEARCH = 1;
    final int SUBTITLE_MORE = 2;
    final int USER_POST = 3;
    final int FLAG_ITEM = 4;

    final int SUB_COUNTRY = 1;
    final int SUB_FAVORITE = 2;
    final int SUB_ENROLL = 3;

    public MyPageViewModel() {

        //[My Page]의 리사이클러 뷰 Adapter - 아이템 리스트를 넘겨준다.
        myPageAdapter = new MyPageAdapter(itemList);

        //[My Page]의 리사이클러 뷰 클릭 리스너 생성자
        myPageAdapter.setMyPageClickListener(this);

    }

    //[My Page - View Type: Profile]의 반환 값
    public MyPageItem getHeadProfile(String imgStr, String email) {

        String path = App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/" + imgStr;

        //My Page의 리사이클러 뷰의 0번째 순서 프로필
        return new MyPageItem(
                new MyPageProfile(path, email),
                HEAD_PROFILE);
    }

    //[My Page - View Type: Subtitle]의 반환 값
    public MyPageItem getSubtitle(int type, String subtitle, boolean isVisible) {
        return new MyPageItem(new MyPageSubtitle(type, subtitle, isVisible), SUBTITLE_MORE);
    }

    //[My Page - View Type: Search]의 반환 값
    public MyPageItem getRouteSearch() {
        return new MyPageItem(null, ROUTE_SEARCH);
    }

    //[My Page - View Type: Flag]의 반환 값
    public MyPageItem getFlagList(List<String> list) {
        return new MyPageItem(list, FLAG_ITEM);
    }

    //[My Page - View Type: Flag]의 반환 값
    public MyPageItem getUserPost(int type) {

        Post post = new Post("asle1000", "France", true);
        post.setP_expenses(1000000 + "");

        switch (type) {
            case 0:
                //favorites
                return new MyPageItem(
                        new RePost(imageUri, post), USER_POST);
            case 1:
                //Recent Post
                return new MyPageItem(
                        new RePost(imageUri, post), USER_POST);
            case 2:
                //enroll
                return new MyPageItem(
                        new RePost(imageUri,post), USER_POST);
            default:
                return null;
        }
    }

    @Override
    public void onProfileClicked(String email) {
        Log.d(TAG, "Success: " + email);
    }

    //route search로 넘어가는 클ㄺ 이벤트
    @Override
    public void onSearchClicked() {
        Log.d(TAG, "onSearchClicked");

        Intent intent =new Intent(App.INSTANCE, RouteSearch.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        App.INSTANCE.startActivity(intent);
    }

    //해당 포스트를 누르면 포스트 상세보기로 넘어가는 클릭 이벤트
    @Override
    public void onPostClicked(RePost rePost) {
        Log.d(TAG + "Post", "Post: " + rePost.toString());
        Intent intent = new Intent(App.INSTANCE, RouteOtherDetail.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.INSTANCE.startActivity(intent);
    }

    @Override
    public void onMoreClicked(int type) {

        Intent intent = new Intent(App.INSTANCE, RouteList.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        switch (type) {
            case SUB_COUNTRY:
                intent.putExtra("req", new MyPageItem(null, SUB_COUNTRY));
                App.INSTANCE.startActivity(intent);
                Log.d(TAG + "More", "선호 국가 더 보기");
                break;
            case SUB_FAVORITE:
                intent.putExtra("req", new MyPageItem(null, SUB_FAVORITE));
                App.INSTANCE.startActivity(intent);
                Log.d(TAG + "More", "선호 포스트 더 보기");
                break;
            case SUB_ENROLL:
                intent.putExtra("req", new MyPageItem(null, SUB_ENROLL));
                App.INSTANCE.startActivity(intent);
                Log.d(TAG + "More", "등록한 포스트 더 보기");
                break;
        }
    }

    @Override
    public void onFlagClicked() {
        Log.d(TAG, "onFlagClicked: ");
    }

    @Override
    public void onAddFlagClicked() {
        Log.d(TAG, "onAddFlagClicked: ");

        Intent intent =new Intent(App.INSTANCE, FavoriteCountry.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        App.INSTANCE.startActivity(intent);
    }

    public void init(User user) {

        mp_flag.setValue(new ArrayList<String>());
        mp_flag.getValue().add(imageUri);
        mp_flag.getValue().add(imageUri);
        mp_flag.getValue().add("android.resource://jso.kpl.traveller/drawable/i_flag_add_icon");

        //My Page의 리사이클러 뷰에 들어갈 데이터 객체화
        //들어가는 순서대로 뷰에 출력
        itemList.setValue(new ArrayList<MyPageItem>());

        if(user != null)
            itemList.getValue().add(getHeadProfile(user.getU_profile_img(), user.getU_email()));
        else
            itemList.getValue().add(getHeadProfile(imageUri, "Test@gmail.com"));

        itemList.getValue().add(getRouteSearch());

        itemList.getValue().add(getSubtitle(1, "Preferred Country", true));
        itemList.getValue().add(getFlagList(mp_flag.getValue()));

        itemList.getValue().add(getSubtitle(2, "Favorites Post", true));
        itemList.getValue().add(getUserPost(0));
        itemList.getValue().add(getUserPost(1));

        itemList.getValue().add(getSubtitle(-1, "Recent Post", false));
        itemList.getValue().add(getUserPost(1));

        itemList.getValue().add(getSubtitle(3, "Enrolled Post", true));
        itemList.getValue().add(getUserPost(2));
        itemList.getValue().add(getUserPost(2));
    }


}
