package jso.kpl.traveller.viewmodel;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.CountryAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.adapters.CountryItemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryListViewModel extends BaseObservable {

    public String title = "국가 리스트";

    String TAG = "Trav.FavoriteCountryVm.";

    public CountryItemAdapter fciAdapter = new CountryItemAdapter();

    public MutableLiveData<Country> countryItem = new MutableLiveData<>();
    public MutableLiveData<List<Country>> countryList = new MutableLiveData<>();

    public RecyclerView.OnScrollListener onCountryScrollListener;

    //에러 및 없을 때
    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    public MutableLiveData<String> errorStr = new MutableLiveData<>();
    public View.OnClickListener onErrClickListener;

    //통신------------------------------------------------------------------------------------------
    Call<ResponseResult<List<Country>>> call;

    CountryAPI countryAPI;

    int ct_no = 0;

    public CountryListViewModel() {

        isSuccess.setValue(true);

        countryAPI = WebService.INSTANCE.getClient().create(CountryAPI.class);

        countryList.setValue(new ArrayList<Country>());
    }

    //국가 리스트 불러오는 통신 함수
    public void countryCall(final int type) {

        if (type == 0) {
            //국가 전체 리스트 호출
            call = countryAPI.loadCountryList(App.Companion.getUser().getU_userid(), ct_no);

        } else if (type == 1) {
            //선호 국가 리스트 호출
            call = countryAPI.loadFavoriteCountry(App.Companion.getUser().getU_userid(), ct_no);
        }

        call.enqueue(new Callback<ResponseResult<List<Country>>>() {
            @Override
            public void onResponse(Call<ResponseResult<List<Country>>> call, Response<ResponseResult<List<Country>>> response) {

                if (response.body() != null) {

                    ResponseResult<List<Country>> result = response.body();

                    if (result.getRes_type() == 1) {

                        for (int i = 0; i < result.getRes_obj().size(); i++) {
                            result.getRes_obj().get(i).setIs_favorite_ld();
                            fciAdapter.updateItem(result.getRes_obj().get(i));

                            Log.d(TAG, result.getRes_obj().get(i).getCt_no() + ". 나라 데이터: " + result.getRes_obj().get(i).getCt_name());
                        }

                        countryList.getValue().addAll(result.getRes_obj());

                        fciAdapter.notifyDataSetChanged();

                    } else {
                        if (fciAdapter.getItemCount() > 0) {

                        } else {
                            isSuccess.setValue(false);
                            errorStr.setValue("등록된 선호 국가가 없습니다.");
                        }
                    }

                    if (countryList.getValue() != null) {
                        ct_no = countryList.getValue().get(countryList.getValue().size() - 1).getCt_no();
                        Log.d(TAG, "불러온 마지막 번호: " + ct_no);
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
