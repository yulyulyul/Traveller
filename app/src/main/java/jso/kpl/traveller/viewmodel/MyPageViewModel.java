package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.MyPageProfile;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.MyPage;
import jso.kpl.traveller.ui.adapters.MyPageAdapter;

public class MyPageViewModel {

    public Context context;

    public MyPageViewModel(Context context) {
        this.context = context;
    }

    String TAG = "TAG.MyPageViewModel.";

    final int HEAD_PROFILE = 0;
    final int ROUTE_SEARCH = 1;
    final int SUBTITLE_MORE = 2;
    final int USER_POST = 3;
    final int HORIZON_ITEM = 4;
    final int ERROR_REFRESH = 5;

    String imageUri = "android.resource://jso.kpl.traveller/drawable/i_blank_person_icon";

    MutableLiveData<List<MyPageItem>> itemList = new MutableLiveData<>();

    //My Page의 리사이클러뷰 어뎁터 라이브데이터
    public MutableLiveData<MyPageAdapter> adapter = new MutableLiveData<>();

    //My Page의 리사이클러 뷰 뷰타입 프로필 관련 데이터 - 프로필 이미지와 이메일
    public MutableLiveData<MyPageProfile> mp_profileLD = new MutableLiveData<>();

    //My Page의 리사이클러 뷰 뷰타입 서브타이틀 관련 데이터 - 부제목(ex:Favorites Country)
    public MutableLiveData<String> subtitleLD = new MutableLiveData<>();

    //My Page의 리사이클러 뷰 뷰타입 포스트 관련 데이터 - 프로필 이미지와 포스트 데이터(Post)
    public MutableLiveData<RePost> postLD = new MutableLiveData<>();

    //My Page의 리사이클러 뷰 뷰타입 수평 리스트 관련 데이터 - 이미지
    //(예정) 들어가는 데이터 클래스를 정해야한다.
    public MutableLiveData<List<String>> horizonLD = new MutableLiveData<>();

    public void init() {

        horizonLD.setValue(new ArrayList<String>());
        horizonLD.getValue().add(imageUri);
        horizonLD.getValue().add(imageUri);
        horizonLD.getValue().add(imageUri);
        horizonLD.getValue().add(imageUri);
        horizonLD.getValue().add(imageUri);

        //My Page의 리사이클러 뷰에 들어갈 데이터 객체화
        //들어가는 순서대로 뷰에 출력
        itemList.setValue(new ArrayList<MyPageItem>());

        itemList.getValue().add(getHeadProfile(imageUri, "asle1221@naver.com"));

        itemList.getValue().add(getRouteSearch());

        itemList.getValue().add(getSubtitle("Preferred Country"));
        itemList.getValue().add(getHorizonList(horizonLD.getValue()));

        itemList.getValue().add(getSubtitle("Favorites Post"));
        itemList.getValue().add(getUserPost(0));
        itemList.getValue().add(getUserPost(0));

        itemList.getValue().add(getSubtitle("Recent Post"));
        itemList.getValue().add(getUserPost(1));

        itemList.getValue().add(getSubtitle("Enrolled Post"));
        itemList.getValue().add(getUserPost(2));
        itemList.getValue().add(getUserPost(2));

        adapter.setValue(new MyPageAdapter(this, itemList));

    }

    //Head_프로필의 값을 넣는 작업 메서드
    public MyPageItem getHeadProfile(String imgStr, String email) {

        //My Page의 리사이클러 뷰의 0번째 순서 프로필
        return new MyPageItem(
                new MyPageProfile(imgStr, email),
                HEAD_PROFILE);
    }

    public MyPageItem getRouteSearch() {
        return new MyPageItem(null, ROUTE_SEARCH);
    }

    public MyPageItem getHorizonList(List<String> list) {
        return new MyPageItem(list, HORIZON_ITEM);
    }

    public MyPageItem getSubtitle(String subtitle) {
        return new MyPageItem(subtitle, SUBTITLE_MORE);
    }

    public MyPageItem getUserPost(int type) {

        switch (type) {
            case 0:
                //favorites
                return new MyPageItem(
                        new RePost(imageUri,
                                new Post(0, "My Favorites", "asle1221@naver.com", "1,000,000$", "미국(America)")), USER_POST);
            case 1:
                //Recent Post
                return new MyPageItem(
                        new RePost(imageUri,
                                new Post(0, "Recent", "asle1221@naver.com", "1,000,000$", "독일(German)")), USER_POST);
            case 2:
                //enroll
                return new MyPageItem(
                        new RePost(imageUri,
                                new Post(0, "Enrolled", "asle1221@naver.com", "1,000,000$", "프랑스(France)")), USER_POST);
            default:
                return null;
        }

    }

    public void onProfileClicked() {
        //프로필 이미지를 클릭하는 이벤트 메서드
        Log.d(TAG + "onProfileClicked", "프로필 클릭 이벤트");
    }

    public void onSearchClicked() {
        Log.d(TAG + "onSearchClicked", "루트 서치 클릭 이벤트");
    }

    public void onMoreClicked() {
        Log.d(TAG + "onMoreClicked", "서브 타이틀 더보기 클릭 이벤트");
    }

    public void onPostClicked(){
        Log.d(TAG + "onPostClicked", "onPostClicked: ");
    }

}
