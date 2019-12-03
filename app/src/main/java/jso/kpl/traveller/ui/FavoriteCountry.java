package jso.kpl.traveller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.FavoriteCountryBinding;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.CountryAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.adapters.FavoriteCountryItemAdapter;
import jso.kpl.traveller.viewmodel.FavoriteCountryViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteCountry extends AppCompatActivity {

    String TAG = "Trav.FavoriteCountry.";

    private FavoriteCountryBinding binding;
    private FavoriteCountryViewModel fcVm;

    Call<ResponseResult<Integer>> call;
    CountryAPI countryAPI = WebService.INSTANCE.getClient().create(CountryAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fcVm = new FavoriteCountryViewModel();

        binding = DataBindingUtil.setContentView(this, R.layout.favorite_country);

        binding.setFcVm(fcVm);
        binding.setLifecycleOwner(this);

        // viewModel 세팅
        binding.getFcVm().init();

        if (getIntent() != null) {

            int type = getIntent().getIntExtra("type", 0);
            binding.getFcVm().loadCountryList(type);

            onAdapterItemClicked();
        }
    }

    //선호 국가 클릭 및 상세보기 클릭 이벤트
    public void onAdapterItemClicked() {

        // 클릭 이벤트 지정
        binding.getFcVm().fciAdapter.setOnItemClickListener(new FavoriteCountryItemAdapter.OnCountryClickListener() {
            // 선호 국가 추가
            @Override
            public void onBtnClicked(final int position, int type) {

                final Country country = binding.getFcVm().countryList.getValue().get(position);

                if(type == 0){
                    call = countryAPI.deleteFavoriteCountry(App.Companion.getUserid(), country.getCt_no());
                } else{
                    call = countryAPI.addFlag(App.Companion.getUserid(), country.getCt_no());
                }

                Log.d(TAG, "onBtnClicked: ");
                call.enqueue(new Callback<ResponseResult<Integer>>() {
                    @Override
                    public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {
                        if (response.body() != null) {

                            ResponseResult<Integer> result = response.body();

                                if (result.getRes_type() == 1) {
                                    if(!binding.getFcVm().countryList.getValue().get(position).ct_is_add_ld.getValue()){
                                        App.Companion.sendToast("선호 국가 추가되었습니다.");
                                        binding.getFcVm().countryList.getValue().remove(position);
                                        binding.getFcVm().fciAdapter.notifyItemRemoved(position);
                                    } else {
                                        App.Companion.sendToast("선호 국가 취소되었습니다.");
                                        binding.getFcVm().countryList.getValue().remove(position);
                                        binding.getFcVm().fciAdapter.notifyItemRemoved(position);
                                    }
                                } else {
                                    if(!binding.getFcVm().countryList.getValue().get(position).ct_is_add_ld.getValue()){
                                        App.Companion.sendToast("선호 국가 추가에 실패했습니다.");
                                    } else {
                                        App.Companion.sendToast("선호 국가 취소에 실패했습니다.");
                                    }
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

            // 상세 정보 페이지 전환
            @Override
            public void onDetailClicked(int position) {

                Country selectCountry = binding.getFcVm().countryList.getValue().get(position);
                Intent intent = new Intent(App.INSTANCE, FavoriteCountryInfo.class);

                intent.putExtra("ct_no", selectCountry.getCt_no());
                intent.putExtra("pos", position);
               // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 22);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;
        else{
            if (requestCode == 22) {

                Log.d(TAG, "onActivityResult: 설마 시발려나");

                Log.d(TAG, "onActivityResult: " + data.getIntExtra("pos", 0));
                Log.d(TAG, "여기로 돌아온다." + data.getBooleanExtra("isAdd", false));
            }
        }


    }
}
