package jso.kpl.traveller.viewmodel;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.CountryAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.adapters.FavoriteCountryItemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteCountryViewModel extends BaseObservable {

    String TAG = "Trav.FavoriteCountryVm.";

    public MutableLiveData<List<Country>> countryList = new MutableLiveData<>();
    public FavoriteCountryItemAdapter fciAdapter = new FavoriteCountryItemAdapter(countryList);
    public MutableLiveData<Country> countryItem = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAdd = new MutableLiveData<>();

    //에러 및 없을 때
    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    public MutableLiveData<String> errorStr = new MutableLiveData<>();
    public View.OnClickListener onRefreshClickListener;

    //통신------------------------------------------------------------------------------------------
    Call<ResponseResult<List<Country>>> loadCountryCall;

    Call<ResponseResult<Integer>> call;
    CountryAPI countryAPI;

    public FavoriteCountryViewModel() {

        isSuccess.setValue(true);
        isAdd.setValue(false);
        countryAPI= WebService.INSTANCE.getClient().create(CountryAPI.class);
    }

    public void init() {
        countryList.setValue(new ArrayList<Country>());
        // 모델 세팅
    }

    //국가 리스트 불러오는 통신 함수
    public void loadCountryList(final int type){

        if(type == 0){

            loadCountryCall = countryAPI.loadCountryList(App.Companion.getUser().getU_userid());

        } else if(type == 1){

            loadCountryCall = countryAPI.loadFavoriteCountry(App.Companion.getUser().getU_userid(), 0);
        }

        loadCountryCall.enqueue(new Callback<ResponseResult<List<Country>>>() {
            @Override
            public void onResponse(Call<ResponseResult<List<Country>>> call, Response<ResponseResult<List<Country>>> response) {

                if(response.body() != null){

                    ResponseResult<List<Country>> result = response.body();

                    if(result.getRes_type() == 1){

                        countryList.setValue((List<Country>)result.getRes_obj());

                        if (type == 0){
                            for(int i = 0; i < countryList.getValue().size(); i++){
                                countryList.getValue().get(i).ct_is_add_ld = new MutableLiveData<>();
                                countryList.getValue().get(i).ct_is_add_ld.setValue(false);
                            }
                        } else{
                            for(int i = 0; i < countryList.getValue().size(); i++){
                                countryList.getValue().get(i).ct_is_add_ld = new MutableLiveData<>();
                                countryList.getValue().get(i).ct_is_add_ld.setValue(true);
                            }
                        }

                        fciAdapter.notifyDataSetChanged();

                    } else{
                        isSuccess.setValue(false);
                        errorStr.setValue("등록된 선호 국가가 없습니다.");
                       // Toast.makeText(App.INSTANCE, "국가 리스트를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseResult<List<Country>>> call, Throwable t) {

                isSuccess.setValue(false);
                errorStr.setValue("일시적 오류입니다.");
                Toast.makeText(App.INSTANCE, "국가 리스트를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
