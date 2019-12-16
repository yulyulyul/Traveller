package jso.kpl.traveller.viewmodel;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.FavoriteCountryInfoVO;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.CountryAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.adapters.FavoriteCountryInfoItemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailCountryInfoViewModel extends BaseObservable {

    String TAG = "Trav.FcInfo.";

    public MutableLiveData<GridLayoutManager> layoutManager = new MutableLiveData<>();
    public MutableLiveData<List<FavoriteCountryInfoVO>> list = new MutableLiveData<>();
    public MutableLiveData<Country> countryItem = new MutableLiveData<>();

    public FavoriteCountryInfoItemAdapter adapter = new FavoriteCountryInfoItemAdapter(list);

    CountryAPI countryAPI;
    Call<ResponseResult<Integer>> call;

    public DetailCountryInfoViewModel() {

        countryAPI = WebService.INSTANCE.getClient().create(CountryAPI.class);

    }

    public void init() {
        // RecyclerView 세팅
        setRecyclerView();
    }

    public void countryWarningClicked() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.0404.go.kr/dev/country.mofa?group_idx=&stext=" + countryItem.getValue().getCt_name()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextCompat.startActivity(App.INSTANCE, intent, null);
    }

    public Call<ResponseResult<Integer>> favoriteCountryCall (int type) {

        if (type == 0)
            call = countryAPI.addFlag(App.Companion.getUser().getU_userid(), countryItem.getValue().getCt_no());
        else
            call = countryAPI.deleteFavoriteCountry(App.Companion.getUser().getU_userid(), countryItem.getValue().getCt_no());

        return call;
    }

    public void setRecyclerView() {
        layoutManager.setValue(new GridLayoutManager(App.INSTANCE, 3));
        // recyclerView에 표시할 데이터 리스트 생성
        ArrayList<FavoriteCountryInfoVO> list = new ArrayList<>();
        // 추후 루트 게시글 리스트로 변경
        for (int i = 0; i < 6; i++) {
            switch (i) {
                case 0:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img1"));
                    break;
                case 1:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img2"));
                    break;
                case 2:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img3"));
                    break;
                case 3:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img4"));
                    break;
                case 4:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img5"));
                    break;
                case 5:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img6"));
                    break;
            }
        }
        this.list.setValue(list);
        // 클릭 이벤트 지정
        adapter.setOnItemClickListener(new FavoriteCountryInfoItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(App.INSTANCE, "해당 게시글로 이동.", Toast.LENGTH_SHORT).show();
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
                        countryItem.getValue().setCt_flag();
                        countryItem.getValue().setIs_favorite_ld();

                        Log.d(TAG, "이거: " + countryItem.getValue().toString());
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
