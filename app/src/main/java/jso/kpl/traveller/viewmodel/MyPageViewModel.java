package jso.kpl.traveller.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import jso.kpl.traveller.network.PostAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.RouteSearch;
import jso.kpl.traveller.ui.SimplePost;
import jso.kpl.traveller.ui.adapters.FlagRvAdapter;
import jso.kpl.traveller.ui.adapters.MyPageAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageViewModel extends ViewModel {

    String TAG = "Trav.MyPageViewModel.";

    //레트로핏
    PostAPI postAPI = WebService.INSTANCE.getClient().create(PostAPI.class);
    MyPageAPI myPageAPI = WebService.INSTANCE.getClient().create(MyPageAPI.class);

    //----------------------------------------------------------------------------------------------
    public MutableLiveData<User> user = new MutableLiveData<>();

    //[My Page]의 리사이클러 뷰 adapter
    public MyPageAdapter myPageAdapter;

    //(예정)Application class로 이동
    public String imageUri = "android.resource://jso.kpl.traveller/drawable/dummy_travel_img1";

    //[My Page]의 리사이클러 뷰의 아이템 리스트
    // -> (Object o, int ViewType)의 리스트 형식
    public MutableLiveData<List<MyPageItem>> itemList = new MutableLiveData<>();

    //[My Page - View Type: Profile]의 아이템
    // -> String 프로필 이미지, String 이메일
    public MutableLiveData<MyPageProfile> mpProfileLD = new MutableLiveData<>();

    //[My Page - View Type: subtitle]의 아이템
    // -> String 서브타이틀
    public MutableLiveData<MyPageSubtitle> mpSubtitleLD = new MutableLiveData<>();

    //[My Page - View Type: Flag]의 리사이클러 뷰의 adapter
    public FlagRvAdapter flagRvAdapter;

    //[My Page - View Type: Post]의 아이템
    // -> RePost 데이터 객체
    public MutableLiveData<ListItem> mp_post = new MutableLiveData<>();

    //[My Page - View Type: Flag]의 아이템
    // -> Flag 뷰타입의 리사이클러 뷰의 아이템 리스트 - Object로 받는다. 1) 국기 2) post
    public MutableLiveData<List<Country>> mp_flag = new MutableLiveData<>();

    List<Country> countryVOList;

    public View.OnClickListener onEditingPostClickListener;

    public void setOnEditingPostClickListener(View.OnClickListener onEditingPostClickListener) {
        this.onEditingPostClickListener = onEditingPostClickListener;
    }

    public MutableLiveData<Boolean> isClick = new MutableLiveData<>();

    public void onTestClicked() {
        Log.d(TAG, "테스트 클릭 이벤트");
        isClick.setValue(!isClick.getValue());
    }

    final int ROUTE_SEARCH = 1;
    final int SUBTITLE_MORE = 2;
    final int USER_POST = 3;

    //통신------------------------------------------------------------------------------------------
    CountryAPI countryAPI;
    Call<ResponseResult<List<Country>>> call;
    //----------------------------------------------------------------------------------------------

    public MyPageViewModel() {

        flagRvAdapter = new FlagRvAdapter();

        isClick.setValue(false);
        //[My Page]의 리사이클러 뷰 Adapter - 아이템 리스트를 넘겨준다.
        myPageAdapter = new MyPageAdapter(itemList, this);
    }

    //[My Page - View Type: Profile]의 반환 값
    public MyPageProfile getHeadProfile(User user) {

        //My Page의 리사이클러 뷰의 0번째 순서 프로필
        return new MyPageProfile(user.getU_profile_img(), user.getU_email());
    }

    //[My Page - View Type: Subtitle]의 반환 값
    public MyPageItem getSubtitle(int type, String subtitle, boolean isVisible) {
        return new MyPageItem(new MyPageSubtitle(type, subtitle, isVisible), SUBTITLE_MORE);
    }

    //[My Page - View Type: Search]의 반환 값
    public MyPageItem getRouteSearch() {
        return new MyPageItem(null, ROUTE_SEARCH);
    }

    //route search로 넘어가는 클릭 이벤트
    public void onSearchClicked() {
        Log.d(TAG, "onSearchClicked");

        Intent intent = new Intent(App.INSTANCE, RouteSearch.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.INSTANCE.startActivity(intent);
    }

    //[My Page - View Type: Flag]의 반환 값
    public void countryCall() {

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

                    if (res.getRes_obj() != null) {
                        List<Country> listItem = res.getRes_obj();

                        for (int i = 0; i < listItem.size(); i++) {
                            listItem.get(i).setCt_flag();
                            listItem.get(i).setCt_is_add_ld();
                        }

                        countryVOList = listItem;
                    }

                } else {
                    Log.d(TAG, "onResponse: My Page 없음");
                }

                if (flagRvAdapter.getItemCount() > 0) {
                    flagRvAdapter.removeItem();
                    Log.d(TAG, "리무브" + flagRvAdapter.getItemCount());
                }

                countryVOList.add(addCountry);

                for (int i = 0; i < countryVOList.size(); i++)
                    flagRvAdapter.updateItem(countryVOList.get(i));

                flagRvAdapter.notifyDataSetChanged();

                countryVOList.clear();
                Log.d(TAG, "진행: " + flagRvAdapter.getItemCount());
            }

            @Override
            public void onFailure(Call<ResponseResult<List<Country>>> call, Throwable t) {
                countryVOList.add(addCountry);

                for (int i = 0; i < countryVOList.size(); i++)
                    flagRvAdapter.updateItem(countryVOList.get(i));

                flagRvAdapter.notifyDataSetChanged();

            }
        });
    }

    public void postCall(final Activity act, final LinearLayout layout, int type) {
        //My Page의 리사이클러 뷰에 들어갈 데이터 객체화

        Call<ResponseResult<List<ListItem>>> call;

        if (type == 1)
            call = myPageAPI.myPageEnroll(App.Companion.getUserid());
        else if(type == 2)
            call = postAPI.loadLikePost(App.Companion.getUserid(), 0, 1);
        else
            call = null;

        call.enqueue(new Callback<ResponseResult<List<ListItem>>>() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.body() != null) {

                    ResponseResult<List<ListItem>> result = ((ResponseResult<List<ListItem>>) response.body());
                    List<ListItem> listItem = result.getRes_obj();

                    if (result.getRes_type() == 1) {
                        for (ListItem item : listItem) {

                            Log.d(TAG, "포스트: " + item.toString());
                            item.setU_profile_img(item.getU_profile_img());
                            layout.addView(new SimplePost(act, item));
                        }
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
