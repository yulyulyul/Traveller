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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yongbeom.aircalendar.core.AirCalendarIntent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.PostSideItem;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.AirlineAPI;
import jso.kpl.traveller.network.CountryAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.Fragment.ImageSideItem;
import jso.kpl.traveller.ui.adapters.ImageSideVpAdapter;
import jso.kpl.traveller.util.JavaUtil;
import me.jerryhanks.timelineview.model.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailCountryInfoViewModel extends BaseObservable {

    String TAG = "Trav.FcInfo.";

    public MutableLiveData<Country> countryItem = new MutableLiveData<>();
    public MutableLiveData<JsonObject> airlineItem = new MutableLiveData<>();

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
    public MutableLiveData<Boolean> isAirline = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSearching = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAirlineResult = new MutableLiveData<>();

    //날짜를 클릭했을 때 여행 기간을 고를 수 있는 캘린더를 호출하는 클릭 리스너
    public View.OnClickListener onCalendarClickListener;

    public void setOnCalendarClickListener(View.OnClickListener onCalendarClickListener) {
        this.onCalendarClickListener = onCalendarClickListener;
    }

    public interface airlineSearchClickListener {
        ArrayList<String> onClick();
        void onSearching();
        void onResetClick();
    }

    public airlineSearchClickListener onAirlineSearchClickListener;

    public void setOnAirlineSearchClickListener(airlineSearchClickListener airlineSearchClickListener) {
        this.onAirlineSearchClickListener = airlineSearchClickListener;
    }

    //여행 기간 [0]: Start / [1]: End
    public MutableLiveData<String[]> travelPeriod = new MutableLiveData<>();
    public MutableLiveData<String> travelResult = new MutableLiveData<>();

    public DetailCountryInfoViewModel() {
        countryAPI = WebService.INSTANCE.getClient().create(CountryAPI.class);

        travelPeriod.setValue(new String[2]);
        isFavorite.setValue(true);
        isAirline.setValue(false);
        isSearching.setValue(false);
        isAirlineResult.setValue(false);
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


    public void onFavoriteClicked() {

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

    public void relationPostCall() {

        countryAPI.loadRelativePost(App.Companion.getUser().getU_userid(),
                countryItem.getValue().getCt_name())
                .enqueue(new Callback<ResponseResult<List<PostSideItem>>>() {
                    @Override
                    public void onResponse(Call<ResponseResult<List<PostSideItem>>> call, Response<ResponseResult<List<PostSideItem>>> response) {
                        if (response.body() != null) {

                            final ResponseResult<List<PostSideItem>> result = response.body();

                            if (result.getRes_type() == 1) {

                                isRelative.setValue(true);

                                if (getSideVpAdapter().getCount() > 0) {
                                    getSideVpAdapter().removeItem();
                                }


                                for (int i = 0; i < result.getRes_obj().size(); i++) {

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

    public void onAirlineClick() {
        isAirline.setValue(!isAirline.getValue());
    }

    public void onAirlineSearchClick() {
        if (!isAirlineResult.getValue()) {
            ArrayList<String> searchData = onAirlineSearchClickListener.onClick();
            if (searchData.size() != 0) {
                onAirlineSearchClickListener.onSearching();
                WebService.INSTANCE.getClient().create(AirlineAPI.class).airlineTicketList(searchData.get(0), searchData.get(1), searchData.get(2), searchData.get(3)).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d(TAG + "통신 성공", "성공적으로 전송");
                        ResponseResult airlineSearchRes = ((ResponseResult) response.body());
                        if (airlineSearchRes.getRes_type() == 1) {
                            JsonParser jsonParse = new JsonParser();
                            JsonArray json = (JsonArray) jsonParse.parse(airlineSearchRes.getRes_obj().toString());
                            airlineItem.setValue((JsonObject) json.get(0));
                            isAirlineResult.setValue(true);
                            onAirlineSearchClickListener.onResetClick();
                            isSearching.setValue(false);
                        } else if (airlineSearchRes.getRes_type() == 0) {
                            App.Companion.sendToast("해당 기간의 항공권이 없습니다.");
                            isAirlineResult.setValue(false);
                            onAirlineSearchClickListener.onResetClick();
                            isSearching.setValue(false);
                        } else {
                            App.Companion.sendToast("서버와 통신에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                            isAirlineResult.setValue(false);
                            onAirlineSearchClickListener.onResetClick();
                            isSearching.setValue(false);
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
        } else {
            isAirlineResult.setValue(false);
            onAirlineSearchClickListener.onResetClick();
        }
    }

    //달력 API 호출 반환값 - (String) Start Day & End Day
    public Intent onCalendar(@NotNull String[] strings) {
        AirCalendarIntent intent = new AirCalendarIntent(App.INSTANCE);
        intent.setSelectButtonText("선   택");
        intent.setResetBtnText("초기화");

        //요일 설정
        intent.setWeekStart(Calendar.SUNDAY);
        ArrayList<String> weekDay = new ArrayList<>();

        weekDay.add("Mon");
        weekDay.add("Tue");
        weekDay.add("Wed");
        weekDay.add("Thu");
        weekDay.add("Fri");
        weekDay.add("Sat");
        weekDay.add("Sun");

        intent.setCustomWeekDays(weekDay);

        //한국 언어
        intent.setWeekDaysLanguage(AirCalendarIntent.Language.KO);
        intent.isSingleSelect(false);
        intent.isBooking(false); // DEFAULT false
        intent.isSelect(true); // DEFAULT false

        if (JavaUtil.checkBlankString(strings[0]) && JavaUtil.checkBlankString(strings[1])) {

            String[] startParse = strings[0].split("-");

            int s_year = Integer.parseInt(startParse[0]);
            int s_month = Integer.parseInt(startParse[1]);
            int s_day = Integer.parseInt(startParse[2]);

            String[] endParse = strings[1].split("-");
            int e_year = Integer.parseInt(endParse[0]);
            int e_month = Integer.parseInt(endParse[1]);
            int e_day = Integer.parseInt(endParse[2]);

            intent.setStartDate(s_year, s_month, s_day);
            intent.setEndDate(e_year, e_month, e_day);
        }

        intent.isMonthLabels(false);
        intent.setActiveMonth(24);

        //현재 연도를 받아온 다음 앞 뒤 (2018/해당 월일) ~ ((2021 - 1) 해당 월일)
        intent.setStartYear(2020);
        intent.setMaxYear(2021);

        return intent;
    }
}
