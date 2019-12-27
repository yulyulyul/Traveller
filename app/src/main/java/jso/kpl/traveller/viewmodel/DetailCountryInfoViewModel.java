package jso.kpl.traveller.viewmodel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.databinding.DetailCtPostBinding;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.FavoriteCountryInfoVO;
import jso.kpl.traveller.model.PostSideItem;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.CountryAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.Fragment.ImageSideItem;
import jso.kpl.traveller.ui.adapters.FavoriteCountryInfoItemAdapter;
import jso.kpl.traveller.ui.adapters.ImageSideVpAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailCountryInfoViewModel extends BaseObservable {

    String TAG = "Trav.FcInfo.";

    public MutableLiveData<Country> countryItem = new MutableLiveData<>();

    public ImageSideVpAdapter sideVpAdapter;

    public ImageSideVpAdapter getSideVpAdapter() {
        return sideVpAdapter;
    }

    public void setSideVpAdapter(ImageSideVpAdapter sideVpAdapter) {
        this.sideVpAdapter = sideVpAdapter;
    }

    public int CURRENT_POS = 0;

    public MutableLiveData<Integer> POST_ID = new MutableLiveData<>();
    public MutableLiveData<Boolean> isReload = new MutableLiveData<>();
    public MutableLiveData<Boolean> isRelative = new MutableLiveData<>();
    public MutableLiveData<Boolean> isFavorite = new MutableLiveData<>();

    public DetailCountryInfoViewModel() {
        countryAPI = WebService.INSTANCE.getClient().create(CountryAPI.class);

        isFavorite.setValue(true);
    }

    public View.OnClickListener onDetailExplainClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            TextView tv = ((TextView) v);

            if (tv.getLineCount() < 3)
                tv.setMaxLines(20);
            else
                tv.setMaxLines(2);
        }
    };

    public View.OnClickListener onCountryWarningClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.0404.go.kr/dev/country.mofa?group_idx=&stext=" + countryItem.getValue().getCt_name()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ContextCompat.startActivity(App.INSTANCE, intent, null);
        }
    };

    CountryAPI countryAPI;


    public void onFavoriteClicked(){

        Call<ResponseResult<Integer>> call;

        if (isFavorite.getValue()) {
            call = countryAPI.deleteFavoriteCountry(App.Companion.getUser().getU_userid(), countryItem.getValue().getCt_no());
        } else {
            call = countryAPI.addFlag(App.Companion.getUser().getU_userid(), countryItem.getValue().getCt_no());
        }

        call.enqueue(new Callback<ResponseResult<Integer>>() {
            @Override
            public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {
                if (response.body() != null) {

                    ResponseResult<Integer> result = response.body();

                    if (result.getRes_type() == 1) {
                        isFavorite.setValue(!isFavorite.getValue());
                    } else {
                    }

                } else {
                    Toast.makeText(App.INSTANCE, "선호 국가 추가를 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                Toast.makeText(App.INSTANCE, "서버에서 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void relationPostCall(){

        countryAPI.loadRelativePost(App.Companion.getUser().getU_userid(),
                countryItem.getValue().getCt_name())
                .enqueue(new Callback<ResponseResult<List<PostSideItem>>>() {
            @Override
            public void onResponse(Call<ResponseResult<List<PostSideItem>>> call, Response<ResponseResult<List<PostSideItem>>> response) {
                if(response.body() != null){

                    final ResponseResult<List<PostSideItem>> result = response.body();

                    if(result.getRes_type() == 1){

                        isRelative.setValue(true);

                        if(getSideVpAdapter().getCount() > 0){
                            getSideVpAdapter().removeItem();
                        }


                        for(int i = 0; i < result.getRes_obj().size(); i++){

                            final int index = i;

                            ImageSideItem imageSideItem = new ImageSideItem();

                            Bundle bundle = new Bundle();
                            bundle.putInt("type", 1);
                            bundle.putSerializable("item", result.getRes_obj().get(i));
                            imageSideItem.setArguments(bundle);

                            getSideVpAdapter().addItem(imageSideItem);

                            imageSideItem.onPostClickListener.setValue(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CURRENT_POS = index;
                                    POST_ID.setValue(result.getRes_obj().get(index).getP_id());
                                }
                            });
                        }

                        getSideVpAdapter().notifyDataSetChanged();
                    } else {
                        isRelative.setValue(false);
                    }
                }

                isReload.setValue(true);
            }

            @Override
            public void onFailure(Call<ResponseResult<List<PostSideItem>>> call, Throwable t) {
                isRelative.setValue(false);
            }
        });
    }

    public void loadCountryInfo(int ctNo) {

        Call<ResponseResult<Country>> call = countryAPI.loadCountryInfo(App.Companion.getUser().getU_userid(), ctNo);

        call.enqueue(new Callback<ResponseResult<Country>>() {
            @Override
            public void onResponse(Call<ResponseResult<Country>> call, Response<ResponseResult<Country>> response) {

                if (response.body() != null) {
                    ResponseResult<Country> result = response.body();

                    if (result.getRes_type() == 1) {
                        countryItem.setValue(result.getRes_obj());
                        countryItem.getValue().setCt_bg();
                        countryItem.getValue().setCt_flag();
                        countryItem.getValue().setR_img();
                        countryItem.getValue().setIs_favorite_ld();

                        isFavorite.setValue(countryItem.getValue().is_favorite_ld.getValue());
                        relationPostCall();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<Country>> call, Throwable t) {
                App.Companion.sendToast("서버 에러로 인해 로딩에 실패했습니다.");

                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}
