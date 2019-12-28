package jso.kpl.traveller.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.network.CountryAPI;
import jso.kpl.traveller.network.PostAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.PostType.EmptyPost;
import jso.kpl.traveller.ui.PostType.SimplePost;
import jso.kpl.traveller.ui.RouteSearch;
import jso.kpl.traveller.ui.adapters.FlagRvAdapter;
import jso.kpl.traveller.util.CurrencyChange;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageViewModel extends ViewModel {

    String TAG = "Trav.MyPageViewModel.";

    //레트로핏
    PostAPI postAPI = WebService.INSTANCE.getClient().create(PostAPI.class);

    //----------------------------------------------------------------------------------------------
    public MutableLiveData<User> user = new MutableLiveData<>();

    //[My Page - View Type: Profile]의 아이템
    // -> String 프로필 이미지, String 이메일
    public MutableLiveData<User> mpProfileLD = new MutableLiveData<>();

    //[My Page - View Type: Flag]의 리사이클러 뷰의 adapter
    public FlagRvAdapter flagRvAdapter;

    public List<Country> favCountryList;

    public View.OnClickListener onProfileClickListener;
    public View.OnClickListener onEditingPostClickListener;

    public void setOnEditingPostClickListener(View.OnClickListener onEditingPostClickListener) {
        this.onEditingPostClickListener = onEditingPostClickListener;
    }

    public MutableLiveData<Boolean> isLike = new MutableLiveData<>();
    public MutableLiveData<Boolean> isEnroll = new MutableLiveData<>();
    public MutableLiveData<Boolean> isRecent = new MutableLiveData<>();
    public MutableLiveData<Boolean> isCart = new MutableLiveData<>();

    public MutableLiveData<Boolean> isLikeMore = new MutableLiveData<>();
    public MutableLiveData<Boolean> isEnrollMore = new MutableLiveData<>();
    public MutableLiveData<Boolean> isCartMore = new MutableLiveData<>();

    public MutableLiveData<Boolean> isClick = new MutableLiveData<>();

    public SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    public MutableLiveData<Boolean> isRefresh = new MutableLiveData<>();

    public View.OnClickListener onMoreEnrollClickListener;
    public View.OnClickListener onMoreCountryClickListener;
    public View.OnClickListener onMoreLikeClickListener;
    public View.OnClickListener onMoreCartClickListener;

    public MutableLiveData<Integer> POST_ID = new MutableLiveData<>();

    public View.OnClickListener onAddOptionViewListener;

    //통신------------------------------------------------------------------------------------------
    CountryAPI countryAPI;
    Call<ResponseResult<List<Country>>> call;

    //에러 처리---------------------------------------------------------------------------------------

    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    public MutableLiveData<String> errorStr = new MutableLiveData<>();
    public View.OnClickListener onErrClickListener;

    public MyPageViewModel() {

        errorStr.setValue("일시적 오류입니다.");

        countryAPI = WebService.INSTANCE.getClient().create(CountryAPI.class);

        isEnroll.setValue(true);
        isLike.setValue(true);
        isRecent.setValue(true);
        isCart.setValue(false);

        isLikeMore.setValue(false);
        isEnrollMore.setValue(false);
        isCartMore.setValue(false);

        flagRvAdapter = new FlagRvAdapter();

        isRefresh.setValue(false);
        isClick.setValue(false);
        //[My Page]의 리사이클러 뷰 Adapter - 아이템 리스트를 넘겨준다.
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

        final Country addCountry = new Country("add_flag");
        addCountry.setCt_flag();

        favCountryList = new ArrayList<>();

        call = countryAPI.loadFavoriteCountry(App.Companion.getUser().getU_userid(), 1);

        call.enqueue(new Callback<ResponseResult<List<Country>>>() {
            @Override
            public void onResponse(Call<ResponseResult<List<Country>>> call, Response<ResponseResult<List<Country>>> response) {

                isSuccess.setValue(true);

                if (response.body() != null) {
                    ResponseResult<List<Country>> res = ((ResponseResult<List<Country>>) response.body());

                    if (res.getRes_obj() != null) {
                        List<Country> listItem = res.getRes_obj();

                        for (int i = 0; i < listItem.size(); i++) {
                            listItem.get(i).setCt_flag();
                            listItem.get(i).setIs_favorite_ld();
                        }

                        favCountryList = listItem;
                    }

                } else {
                    Log.d(TAG, "onResponse: My Page 없음");
                }

                if (flagRvAdapter.getItemCount() > 0) {
                    flagRvAdapter.removeItem();
                    Log.d(TAG, "리무브" + flagRvAdapter.getItemCount());
                }

                favCountryList.add(addCountry);

                for (int i = 0; i < favCountryList.size(); i++)
                    flagRvAdapter.updateItem(favCountryList.get(i));

                flagRvAdapter.notifyDataSetChanged();

                favCountryList.clear();
                Log.d(TAG, "진행: " + flagRvAdapter.getItemCount());
            }

            @Override
            public void onFailure(Call<ResponseResult<List<Country>>> call, Throwable t) {

                if (flagRvAdapter.getItemCount() > 0) {
                    flagRvAdapter.removeItem();
                    Log.d(TAG, "리무브" + flagRvAdapter.getItemCount());
                }

                favCountryList.add(addCountry);

                for (int i = 0; i < favCountryList.size(); i++)
                    flagRvAdapter.updateItem(favCountryList.get(i));

                flagRvAdapter.notifyDataSetChanged();

                favCountryList.clear();

                isSuccess.setValue(false);
                errorStr.setValue("일시적 오류입니다.");
            }
        });
    }

    public void postCall(final Activity act, final LinearLayout layout, final int type) {
        //My Page의 리사이클러 뷰에 들어갈 데이터 객체화

        Log.d(TAG, "마이 페이지 포스트 호출");

        Call<ResponseResult<List<ListItem>>> call;

        if (type == 1)
            call = postAPI.loadLikePost(App.Companion.getUser().getU_userid(), 0, 1);
        else if (type == 2)
            call = postAPI.loadRecentPost(App.Companion.getUser().getU_userid());
        else if (type == 3)
            call = postAPI.myPageEnroll(App.Companion.getUser().getU_userid());
        else if(type == 4)
            call = postAPI.loadCartList(App.Companion.getUser().getU_userid(), 0,1);
        else
            call = null;

        call.enqueue(new Callback<ResponseResult<List<ListItem>>>() {
            @Override
            public void onResponse(Call call, Response response) {

                isSuccess.setValue(true);

                if (response.body() != null) {

                    ResponseResult<List<ListItem>> result = ((ResponseResult<List<ListItem>>) response.body());
                    List<ListItem> listItem = result.getRes_obj();

                    if (result.getRes_type() == 1) {

                        for (ListItem item : listItem) {
                            Log.d(TAG, "포스트: " + item.toString());

                            final SimplePost simplePost = new SimplePost(App.INSTANCE, item);

                            if (!item.getP_expenses().contains("₩"))
                                item.setP_expenses(CurrencyChange.moneyFormatToWon(Long.parseLong(item.getP_expenses())));

                            layout.addView(simplePost);

                            simplePost.binding.getItem().onPostClickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("Trav.Item", "Post: " + simplePost.binding.getItem().p_id);
                                    POST_ID.setValue(simplePost.binding.getItem().p_id);
                                }
                            };
                        }

                        if (type == 1) {

                            isLike.setValue(true);

                            if (listItem.size() > 1) {
                                isLikeMore.setValue(true);
                                Log.d(TAG, "좋아요 포스트 더보기 2: " + isLikeMore.getValue());
                            } else {
                                isLikeMore.setValue(false);
                                Log.d(TAG, "좋아요 포스트 더보기 1 이하: " + isLikeMore.getValue());
                            }

                        } else if(type == 2){
                            isRecent.setValue(true);
                        } else if (type == 3) {

                            isEnroll.setValue(true);

                            if (listItem.size() > 1) {
                                isEnrollMore.setValue(true);
                                Log.d(TAG, "등록 포스트 더보기 2: " + isEnrollMore.getValue());
                            } else {
                                isEnrollMore.setValue(false);
                                Log.d(TAG, "등록 포스트 더보기 1 이하: " + isEnrollMore.getValue());
                            }
                        } else if(type == 4){
                            isEnroll.setValue(true);

                            if (listItem.size() > 1) {
                                isCartMore.setValue(true);
                                Log.d(TAG, "카트 포스트 더보기 2: " + isEnrollMore.getValue());
                            } else {
                                isCartMore.setValue(false);
                                Log.d(TAG, "카트 포스트 더보기 1 이하: " + isEnrollMore.getValue());
                            }
                        }

                    } else if (result.getRes_type() == 0) {

                        if (type == 1) {
                            isLike.setValue(false);
                        } else if(type == 2){
                            isRecent.setValue(false);
                        } else if (type == 3) {
                            isEnroll.setValue(false);
                        } else if (type == 4){
                            isCart.setValue(false);
                        }
                    }
                } else {

                    if (type == 1) {
                        isLike.setValue(false);
                    } else if(type == 2){
                        isRecent.setValue(false);
                    } else if (type == 3) {
                        isEnroll.setValue(false);
                    } else {
                        isCart.setValue(false);
                    }
                }

                isRefresh.setValue(false);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
                t.printStackTrace();

                isRefresh.setValue(false);

                if (type == 1) {
                    isLike.setValue(false);
                } else if(type == 2){
                    isRecent.setValue(false);
                } else if (type == 3) {
                    isEnroll.setValue(false);
                } else {
                    isCart.setValue(false);
                }

                isSuccess.setValue(false);
                errorStr.setValue("일시적 오류입니다.");
            }
        });
    }
}
