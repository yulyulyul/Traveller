package jso.kpl.traveller.viewmodel;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.MyPageProfile;
import jso.kpl.traveller.model.MyPageSubtitle;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.network.CountryAPI;
import jso.kpl.traveller.network.MyPageAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.FavoriteCountry;
import jso.kpl.traveller.ui.RouteList;
import jso.kpl.traveller.ui.RouteOtherDetail;
import jso.kpl.traveller.ui.RouteSearch;
import jso.kpl.traveller.ui.adapters.FlagRvAdapter;
import jso.kpl.traveller.ui.adapters.MyPageAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageViewModel extends ViewModel implements MyPageAdapter.OnMyPageClickListener {

    String TAG = "Trav.MyPageViewModel.";

    //레트로핏
    MyPageAPI myPageAPI = WebService.INSTANCE.getClient().create(MyPageAPI.class);

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
    public MutableLiveData<ListItem> mp_post = new MutableLiveData<>();

    //[My Page - View Type: Flag]의 아이템
    // -> Flag 뷰타입의 리사이클러 뷰의 아이템 리스트 - Object로 받는다. 1) 국기 2) post
    public MutableLiveData<List<Country>> mp_flag = new MutableLiveData<>();

    public MutableLiveData<String> mpFlagItem = new MutableLiveData<>();

    public String subtitleStr;

    List<Country> countryVOList;

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

    //통신------------------------------------------------------------------------------------------
    CountryAPI countryAPI;
    Call<ResponseResult<List<Country>>> call;
    //----------------------------------------------------------------------------------------------

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
    public void getFlagList() {

        final Country addCountry = new Country(0, null, "add_flag", null, null, null, null, null, false);
        addCountry.setCt_flag();

        countryVOList = new ArrayList<>();

        countryAPI = WebService.INSTANCE.getClient().create(CountryAPI.class);

        call = countryAPI.loadFavoriteCountry(App.Companion.getUserid(), 1);

        call.enqueue(new Callback<ResponseResult<List<Country>>>() {
            @Override
            public void onResponse(Call<ResponseResult<List<Country>>> call, Response<ResponseResult<List<Country>>> response) {

                if (response.body() != null) {
                    ResponseResult<List<Country>> res = ((ResponseResult<List<Country>>) response.body());

                    if(res.getRes_obj() != null){
                        List<Country> listItem = res.getRes_obj();

                        for(int i = 0; i < listItem.size(); i++){
                            listItem.get(i).setCt_flag();

                            Log.d(TAG, "My Page Country: " + listItem.get(i).toString());
                        }
                        countryVOList = listItem;
                    }

                } else {
                    Log.d(TAG, "onResponse: My Page 없음");
                }

                countryVOList.add(addCountry);

                itemList.getValue().add(3, new MyPageItem(countryVOList, FLAG_ITEM));
            }

            @Override
            public void onFailure(Call<ResponseResult<List<Country>>> call, Throwable t) {
                countryVOList.add(addCountry);
                itemList.getValue().add(3, new MyPageItem(countryVOList, FLAG_ITEM));
            }
        });

    }

    //[My Page - View Type: Flag]의 반환 값
    public MyPageItem getUserPost(int type) {

        Post post = new Post("asle1000", "France", true);
        post.setP_expenses(1000000 + "");

        switch (type) {
            case 0:
                //favorites
                return new MyPageItem(
                        new ListItem(2, "t_profile_1573822678472.jpg", 8, "미국", "99999", null), USER_POST);
            case 1:
                //Recent Post
                return new MyPageItem(
                        new ListItem(2, "t_profile_1573822678472.jpg", 7, "독일", "777777", null), USER_POST);
            case 2:
                //enroll
                return new MyPageItem(
                        new RePost(imageUri, post), USER_POST);
            default:
                return null;
        }
    }

    @Override
    public void onProfileClicked(String email) {
        Log.d(TAG, "Success: " + email);
    }

    //route search로 넘어가는 클릭 이벤트
    @Override
    public void onSearchClicked() {
        Log.d(TAG, "onSearchClicked");

        Intent intent = new Intent(App.INSTANCE, RouteSearch.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        App.INSTANCE.startActivity(intent);
    }

    //해당 포스트를 누르면 포스트 상세보기로 넘어가는 클릭 이벤트
    @Override
    public void onPostClicked(ListItem listItem) {
        Log.d(TAG + "Post", "Post: " + listItem.toString());
        Intent intent = new Intent(App.INSTANCE, RouteOtherDetail.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("p_id", listItem.getP_id());
        App.INSTANCE.startActivity(intent);
    }

    @Override
    public void onMoreClicked(int type) {

        Intent intent = new Intent(App.INSTANCE, RouteList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        switch (type) {
            case SUB_COUNTRY:

                Intent flagIntent = new Intent(App.INSTANCE, FavoriteCountry.class);
                flagIntent .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                flagIntent .putExtra("type", 0);
                App.INSTANCE.startActivity(flagIntent);

                break;
            case SUB_FAVORITE:
                intent.putExtra("req", new MyPageItem(App.Companion.getUserid(), SUB_ENROLL));
                App.INSTANCE.startActivity(intent);
                Log.d(TAG + "More", "선호 포스트 더 보기");
                break;
            case SUB_ENROLL:
                intent.putExtra("req", new MyPageItem(App.Companion.getUserid(), SUB_ENROLL));
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

        Intent flagIntent = new Intent(App.INSTANCE, FavoriteCountry.class);
        flagIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        flagIntent.putExtra("type", 1);
        App.INSTANCE.startActivity(flagIntent);

        Log.d(TAG + "More", "선호 국가 더 보기");
    }

    public void init(User user) {

        mp_flag.setValue(new ArrayList<Country>());

        //My Page의 리사이클러 뷰에 들어갈 데이터 객체화
        //들어가는 순서대로 뷰에 출력
        itemList.setValue(new ArrayList<MyPageItem>());

        //0. 유저 프로필
        if (user != null)
            itemList.getValue().add(getHeadProfile(user.getU_profile_img(), user.getU_email()));

        //1. 루트 서치
        itemList.getValue().add(getRouteSearch());

        //2. 서브 타이틀 - 선호하는 국가
        itemList.getValue().add(getSubtitle(1, "선호하는 국가", true));

        //3. 선호하는 국가 국기 리스트
        getFlagList();

        //4. 서브 타이틀 - 좋아하는 포스트
        itemList.getValue().add(getSubtitle(2, "좋아하는 포스트", true));

        //5, 6. 유저 포스트 - 좋아하는 포스트
        itemList.getValue().add( getUserPost(0));
        itemList.getValue().add(getUserPost(1));

        //7. 서브 타이틀 - 최근 포스트
        itemList.getValue().add(getSubtitle(-1, "최근 포스트", false));

        //8. 유저 포스트 - 최근 포스트
        itemList.getValue().add(getUserPost(1));

        //9. 서브 타이틀 - 내가 등록한 포스트
        itemList.getValue().add(getSubtitle(3, "내가 등록한 포스트", true));

        myPageAPI.myPageEnroll(App.Companion.getUserid()).enqueue(new Callback<ResponseResult<List<ListItem>>>() {

            @Override
            public void onResponse(Call call, Response response) {
                Log.d(TAG + "통신 성공", "성공적으로 전송");

                if (response.body() != null) {
                    ResponseResult<List<ListItem>> res = ((ResponseResult<List<ListItem>>) response.body());

                    List<ListItem> listItem = res.getRes_obj();
                    if (listItem != null) {
                        for (ListItem item : listItem) {
                            String imgPath = App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/" + item.getU_profile_img();
                            item.setU_profile_img(imgPath);
                            itemList.getValue().add(new MyPageItem(
                                    item, USER_POST));
                        }
                    } else {

                    }
                } else {
                    Log.d(TAG, "onResponse: My Page 없음");
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(App.INSTANCE, "통신 불량" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
                t.printStackTrace();
            }
        });


    }


}
