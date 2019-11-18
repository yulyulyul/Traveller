package jso.kpl.traveller.viewmodel;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yongbeom.aircalendar.core.AirCalendarIntent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.ui.Fragment.WritePostType;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.util.CurrencyChange;

public class EditingPostViewModel extends ViewModel implements View.OnClickListener {

    String TAG = "Trav.EditingPostVm";

    //상단바-----------------------------------------------------------------------------------------
    //EditPost 나가는 버튼 클릭 리스너
    public View.OnClickListener onBackClickListener;

    //포스트 저장하는 버튼 클릭 리스너
    public View.OnClickListener onSaveClickListener;
    //----------------------------------------------------------------------------------------------

    //노드 폼 API(By Lee) -------------------------------------------------------------------------
    public RouteNodeAdapter routeNodeAdapter;

    public RouteNodeAdapter getRouteNodeAdapter() {
        return routeNodeAdapter;
    }

    public void setRouteNodeAdapter(RouteNodeAdapter routeNodeAdapter) {
        this.routeNodeAdapter = routeNodeAdapter;
    }
    //----------------------------------------------------------------------------------------------

    //작성 폼----------------------------------------------------------------------------------------
    public MutableLiveData<FragmentManager> fm = new MutableLiveData<>();
    public MutableLiveData<Fragment> fragment = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSmallPost = new MutableLiveData<>();
    //국가 또는 장소 입력
    public MutableLiveData<String> inputPlace = new MutableLiveData<>();

    //경비 입력
    public MutableLiveData<String> inputExpenses = new MutableLiveData<>();

    //날짜를 클릭했을 때 여행 기간을 고를 수 있는 캘린더를 호출하는 클릭 리스너
    public View.OnClickListener onCalendarClickListener;

    public void setOnCalendarClickListener(View.OnClickListener onCalendarClickListener) {
        this.onCalendarClickListener = onCalendarClickListener;
    }

    //여행 기간 [0]: Start / [1]: End / [2]: Start ~ End
    public MutableLiveData<String[]> travelPeriod = new MutableLiveData<>();
    public MutableLiveData<String> travelResult = new MutableLiveData<>();

    //커맨트 입력
    public MutableLiveData<String> inputComment = new MutableLiveData<>();

    public MutableLiveData<Boolean> isClick = new MutableLiveData<>();

    //----------------------------------------------------------------------------------------------
    //하단바-----------------------------------------------------------------------------------------
    //포스트 공개 비공개 여부
    public MutableLiveData<Boolean> isOpen = new MutableLiveData<>();

    public void onOpenClicked() {
        isOpen.setValue(!isOpen.getValue());

        Log.d(TAG, "onOpenClicked: " + isOpen.getValue());
    }

    //----------------------------------------------------------------------------------------------
    //Small Post 작성폼------------------------------------------------------------------------------
    //Small Post 작성 폼 상단 좌측 back 버튼
    public View.OnClickListener onSaveSpClickListener;
    public View.OnClickListener onDetachFragmentClickListener;

    public View.OnClickListener onAddCategoryClickListener;
    public MutableLiveData<List<String>> tagList = new MutableLiveData<>();

    public MutableLiveData<List<Uri>> photoList = new MutableLiveData<>();

    //각 3개의 뷰의 가시성[photo, calendar, tag]
    public MutableLiveData<Boolean> isPhoto = new MutableLiveData<>();
    public MutableLiveData<Boolean> isCalendar = new MutableLiveData<>();
    public MutableLiveData<Boolean> isTag = new MutableLiveData<>();

    public EditingPostViewModel() {

        isClick.setValue(true);

        isSmallPost.setValue(true);

        //Default:비공개
        isOpen.setValue(true);

        //Small Post 추가 뷰 가시성
        isPhoto.setValue(false);
        isCalendar.setValue(false);
        isTag.setValue(false);

        photoList.setValue(new ArrayList<Uri>());
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

        if (strings != null && !strings[0].isEmpty() && !strings[1].isEmpty()) {

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

    //Add SmallPost
    public void onAddSmallPostClicked() {

        if (fragment.getValue() != null) {
            Toast.makeText(App.INSTANCE, "이미 작성 중인 노드 포스트가 존재합니다.", Toast.LENGTH_SHORT).show();
        } else {
            fragment.setValue(new WritePostType());
        }

    }

    public SmallPost saveSmallPostData() {

        SmallPost smallPost = new SmallPost();

        if (inputExpenses.getValue() != null && !inputExpenses.getValue().equals("")) {

            if(inputExpenses.getValue().contains("₩"))
                smallPost.setSp_expenses(inputExpenses.getValue());
            else
                smallPost.setSp_expenses(CurrencyChange.moneyFormatToWon(Long.parseLong(inputExpenses.getValue())));

        }else{
            smallPost.setSp_expenses(CurrencyChange.moneyFormatToWon(0));
        }

        if (inputComment.getValue() != null && !inputComment.getValue().equals("")) {
            smallPost.setSp_comment(inputComment.getValue());
        }

        if (photoList.getValue() != null && !photoList.getValue().isEmpty() && photoList.getValue().size() > 0) {

            List<String> uriToStr = new ArrayList<>();

            for (Uri uri : photoList.getValue()) {
                uriToStr.add(uri.toString());
            }

            smallPost.setSp_imgs(uriToStr);
        }

        if (tagList.getValue() != null && tagList.getValue().size() > 0) {
            smallPost.setSp_category(tagList.getValue());
        }

        if (travelPeriod.getValue() != null && !travelPeriod.getValue()[0].equals("")) {
            smallPost.setSp_start_date(travelPeriod.getValue()[0]);
            smallPost.setSp_end_date(travelPeriod.getValue()[1]);
        }

        if (inputPlace.getValue() == null || inputPlace.getValue().equals("")) {
            Toast.makeText(App.INSTANCE, "장소를 기입해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            smallPost.setSp_place(inputPlace.getValue());
        }

        Log.d(TAG, "onSaveSpClicked: " + smallPost.toString());

        return smallPost;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.isPhoto:
                isPhoto.setValue(!isPhoto.getValue());
                break;

            case R.id.isTag:
                isTag.setValue(!isTag.getValue());
                break;

            case R.id.isCalendar:
                isCalendar.setValue(!isCalendar.getValue());
                break;
        }
    }
}
