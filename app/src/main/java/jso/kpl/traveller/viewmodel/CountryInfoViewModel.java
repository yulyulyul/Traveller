package jso.kpl.traveller.viewmodel;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yongbeom.aircalendar.core.AirCalendarIntent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.TimeLineItemBinding;
import jso.kpl.traveller.model.CartListItem;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.model.Timeline;
import jso.kpl.traveller.network.CartlistAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.Fragment.WritePostType;
import jso.kpl.traveller.ui.adapters.CartlistItemAdapter;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.util.CurrencyChange;
import jso.kpl.traveller.util.JavaUtil;
import me.jerryhanks.timelineview.IndicatorAdapter;
import me.jerryhanks.timelineview.interfaces.TimeLineViewCallback;
import me.jerryhanks.timelineview.model.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryInfoViewModel extends ViewModel implements Callback {

    String TAG = "Trav.CountryInfoVm";


    //날짜를 클릭했을 때 여행 기간을 고를 수 있는 캘린더를 호출하는 클릭 리스너
    public View.OnClickListener onCalendarClickListener;

    public void setOnCalendarClickListener(View.OnClickListener onCalendarClickListener) {
        this.onCalendarClickListener = onCalendarClickListener;
    }

    //여행 기간 [0]: Start / [1]: End
    public MutableLiveData<String[]> travelPeriod = new MutableLiveData<>();
    public MutableLiveData<String> travelResult = new MutableLiveData<>();

    public CountryInfoViewModel() {
        travelPeriod.setValue(new String[2]);
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
        intent.setStartYear(2018);
        intent.setMaxYear(2021);

        return intent;
    }

    @Override
    public void onResponse(Call call, Response response) {
        Log.d(TAG + "통신 성공", "성공적으로 전송");

        ResponseResult<List<CartListItem>> cartlistRes = ((ResponseResult<List<CartListItem>>) response.body());

        if (cartlistRes.getRes_type() == -1) {

        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        Toast.makeText(App.INSTANCE, "통신 불량" + t.getMessage(), Toast.LENGTH_SHORT).show();
        Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
        t.printStackTrace();
    }
}
