package jso.kpl.traveller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.CountryListBinding;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.CountryAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.adapters.CountryItemAdapter;
import jso.kpl.traveller.viewmodel.CountryListViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryList extends AppCompatActivity {

    String TAG = "Trav.CountryList.";

    private CountryListBinding binding;
    private CountryListViewModel fcVm;

    Call<ResponseResult<Integer>> call;
    CountryAPI countryAPI = WebService.INSTANCE.getClient().create(CountryAPI.class);

    int type = 0;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fcVm = new CountryListViewModel();

        binding = DataBindingUtil.setContentView(this, R.layout.country_list);

        binding.setFcVm(fcVm);
        binding.setLifecycleOwner(this);

        if (getIntent() != null) {
            type = getIntent().getIntExtra("type", 0);
            binding.getFcVm().countryCall(type);

            onAdapterItemClicked();
        }

        binding.getFcVm().onErrClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);

                overridePendingTransition(0, 0);
            }
        };

        binding.recyclerView.setItemAnimator(null);
    }

    //선호 국가 클릭 및 상세보기 클릭 이벤트
    public void onAdapterItemClicked() {

        // 클릭 이벤트 지정
        binding.getFcVm().fciAdapter.setOnItemClickListener(new CountryItemAdapter.OnCountryClickListener() {
            // 선호 국가 추가
            @Override
            public void onBtnClicked(final int position, int type) {

                final Country country = binding.getFcVm().countryList.getValue().get(position);

                if (type == 0) {
                    Log.d(TAG, "선호 국가 삭제: " + position);
                    call = countryAPI.deleteFavoriteCountry(App.Companion.getUser().getU_userid(), country.getCt_no());
                } else {
                    Log.d(TAG, "선호 국가 추가: " + position);
                    call = countryAPI.addFlag(App.Companion.getUser().getU_userid(), country.getCt_no());
                }

                call.enqueue(new Callback<ResponseResult<Integer>>() {
                    @Override
                    public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {
                        if (response.body() != null) {

                            ResponseResult<Integer> result = response.body();

                            if (result.getRes_type() == 1) {
                                if (!binding.getFcVm().countryList.getValue().get(position).is_favorite_ld.getValue()) {
                                    App.Companion.sendToast("선호 국가 추가되었습니다.");

                                    binding.getFcVm().countryList.getValue().get(position).is_favorite_ld.setValue(!binding.getFcVm().countryList.getValue().get(position).is_favorite_ld.getValue());

                                    binding.getFcVm().fciAdapter.notifyItemChanged(position);
                                } else {
                                    App.Companion.sendToast("선호 국가 취소되었습니다.");

                                    binding.getFcVm().countryList.getValue().get(position).is_favorite_ld.setValue(!binding.getFcVm().countryList.getValue().get(position).is_favorite_ld.getValue());

                                    binding.getFcVm().fciAdapter.notifyItemChanged(position);
                                }
                            } else {
                                if (!binding.getFcVm().countryList.getValue().get(position).is_favorite_ld.getValue()) {
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
            public void onDetailClicked(int position, int ct_no) {

                Intent intent = new Intent(App.INSTANCE, DetailCountryInfo.class);
                intent.putExtra("ct_no", ct_no);
                startActivityForResult(intent, 22);
                pos = position;
            }
        });

        binding.topActionBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.getFcVm().onCountryScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                if (itemTotalCount > 13) {
                    if (lastVisibleItemPosition == itemTotalCount) {
                        Log.d(TAG, "마지막 아이템: ");
                        binding.getFcVm().countryCall(type);
                    }
                }
            }

        };
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;
        else {
            if (requestCode == 22) {

                binding.getFcVm().countryList.getValue().get(pos).is_favorite_ld.setValue(data.getBooleanExtra("is_favorite", false));

                Log.d(TAG, "onActivityResult: " + binding.getFcVm().countryList.getValue().get(pos).is_favorite_ld.getValue());

                binding.getFcVm().fciAdapter.notifyItemChanged(pos);
//                binding.getFcVm().countryList.getValue().clear();
//                binding.getFcVm().fciAdapter.removeItem();
//
//                binding.getFcVm().countryCall(type);
            }
        }
    }
}
