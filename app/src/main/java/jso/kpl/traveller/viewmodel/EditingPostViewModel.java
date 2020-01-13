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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import jso.kpl.traveller.network.AirlineAPI;
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

public class EditingPostViewModel extends ViewModel implements View.OnClickListener, Callback {

    String TAG = "Trav.EditingPostVm";

    //CartListItem---------------------------------------------------------------------------------------
    public MutableLiveData<CartlistItemAdapter> cartlistItemAdapter = new MutableLiveData<>();
    public MutableLiveData<List<CartListItem>> cartlistItem = new MutableLiveData<>();

    public MutableLiveData<Boolean> isCartlist = new MutableLiveData<>();
    public MutableLiveData<Boolean> noCartlist = new MutableLiveData<>();

    public View.OnClickListener onCartlistClickListener;
    public MutableLiveData<IndicatorAdapter<Timeline>> timelineAdapter = new MutableLiveData<>();
    public MutableLiveData<List<Timeline>> timelineItem = new MutableLiveData<>();
    public MutableLiveData<Integer> timelineSelect = new MutableLiveData<>();

    public ViewGroup recyclerView;
    public MutableLiveData<Integer> itemViewHeight = new MutableLiveData<>();
    public MutableLiveData<Integer> itemViewWidth = new MutableLiveData<>();

    //레트로핏
    CartlistAPI cartlistAPI = WebService.INSTANCE.getClient().create(CartlistAPI.class);

    public void setOnCartlistClickListener(View.OnClickListener onCartlistClickListener) {
        this.onCartlistClickListener = onCartlistClickListener;
    }

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

    //여행 기간 [0]: Start / [1]: End
    public MutableLiveData<String[]> travelPeriod = new MutableLiveData<>();
    public MutableLiveData<String> travelResult = new MutableLiveData<>();

    //커맨트 입력
    public MutableLiveData<String> inputComment = new MutableLiveData<>();

    public MutableLiveData<Boolean> isClick = new MutableLiveData<>();

    //----------------------------------------------------------------------------------------------
    //항공권 리스트
    public MutableLiveData<JsonObject> airlineItem = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAirlineOk = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAirline = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSearching = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAirlineResult = new MutableLiveData<>();

    public interface airlineSearchClickListener {
        ArrayList<String> onClick();
        void onSearching();
        void onResetClick();
    }

    public airlineSearchClickListener onAirlineSearchClickListener;

    public void setOnAirlineSearchClickListener(airlineSearchClickListener airlineSearchClickListener) {
        this.onAirlineSearchClickListener = airlineSearchClickListener;
    }

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

        travelPeriod.setValue(new String[2]);

        isClick.setValue(true);
        isSmallPost.setValue(true);

        //Default:비공개
        isOpen.setValue(true);

        //Small Post 추가 뷰 가시성
        isPhoto.setValue(false);
        isCalendar.setValue(false);
        isTag.setValue(false);

        photoList.setValue(new ArrayList<Uri>());

        isAirlineOk.setValue(false);
        isAirline.setValue(false);
        isSearching.setValue(false);
        isAirlineResult.setValue(false);

        //------------------------------------------------------------------------------------------
        isCartlist.setValue(false);

        cartlistAPI.cartlist(App.Companion.getUser().getU_userid()).enqueue(this);

        timelineItem.setValue(new ArrayList<Timeline>());
        timelineItem.getValue().add(new Timeline(Status.ATTENTION, "", "", "", "", ""));
        timelineAdapter.setValue(new IndicatorAdapter<>(timelineItem.getValue(), App.INSTANCE, new TimeLineViewCallback<Timeline>() {
            public View onBindView(Timeline model, FrameLayout container, final int position) {
                TimeLineItemBinding timeLineItemBinding = DataBindingUtil.inflate(LayoutInflater.from(App.INSTANCE), R.layout.time_line_item, container, false);
                View view = timeLineItemBinding.getRoot();
                return view;
            }
        }));
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

    //Add SmallPostDialog
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

            if (inputExpenses.getValue().contains("₩"))
                smallPost.setSp_expenses(inputExpenses.getValue());
            else
                smallPost.setSp_expenses(CurrencyChange.moneyFormatToWon(Long.parseLong(inputExpenses.getValue())));

        } else {
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

        if (JavaUtil.checkBlankString(travelPeriod.getValue()[0])) {
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

    public void onCatelistCancelClicked() {
        isCartlist.setValue(false);
    }

    public void onCartlistBackClick() {
        isCartlist.setValue(false);
        //cartlistWidth.setValue(-2);
    }

    @Override
    public void onResponse(Call call, Response response) {
        Log.d(TAG + "통신 성공", "성공적으로 전송");

        ResponseResult<List<CartListItem>> cartlistRes = ((ResponseResult<List<CartListItem>>) response.body());

        if (cartlistRes.getRes_type() == -1) {

        } else if (cartlistRes.getRes_type() == 0) {
            noCartlist.setValue(true);
            Log.d(TAG, "카트리스트 없음");
        } else {
            noCartlist.setValue(false);
            List<CartListItem> cartListItem = cartlistRes.getRes_obj();
            for (CartListItem idx : cartListItem) {
                if (idx.getP_category() != null) {
                    if (idx.getP_category().split(", ").length > 4) {
                        ArrayList<String> categoryList = new ArrayList<>(Arrays.asList(idx.getP_category().split(", ", 5)));
                        categoryList.remove(categoryList.size() - 1);
                        String category = categoryList.toString();
                        idx.setP_category(category.substring(1, category.length() - 1).concat(" ..."));
                    }
                }
            }
            Log.d(TAG, cartListItem.toString());
            cartlistItem.setValue(new ArrayList<CartListItem>());
            cartlistItem.getValue().addAll(cartListItem);
            Log.d(TAG, cartlistItem.getValue().toString());

            cartlistItemAdapter.setValue(new CartlistItemAdapter(cartlistItem));

            cartlistItemAdapter.getValue().setOnItemClickListener(
                    new CartlistItemAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            int p_id = cartlistItem.getValue().get(position).getP_id();
                            cartlistAPI.cartlist_timeline(p_id).enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    Log.d(TAG + "통신 성공", "성공적으로 전송");
                                    timelineSelect.setValue(0);

                                    ResponseResult<List<Timeline>> cartlistTimelineRes = ((ResponseResult<List<Timeline>>) response.body());
                                    List<Timeline> cartlistTimeline = cartlistTimelineRes.getRes_obj();
                                    for (int i = 0; i < cartlistTimeline.size(); i++) {
                                        cartlistTimeline.get(i).setStatus(Status.COMPLETED);
                                        if (cartlistTimeline.get(i).getSp_period() != null) {
                                            String tmp = cartlistTimeline.get(i).getSp_period().replace(" ", "");
                                            String[] tmps = tmp.split("~");

                                            cartlistTimeline.get(i).setSp_period(JavaUtil.travelPeriod(tmps[0], tmps[1]));
                                        }
                                        if (cartlistTimeline.get(i).getSp_category() != null) {
                                            if (cartlistTimeline.get(i).getSp_category().split(", ").length > 4) {
                                                ArrayList<String> categoryList = new ArrayList<>(Arrays.asList(cartlistTimeline.get(i).getSp_category().split(", ", 5)));
                                                categoryList.remove(categoryList.size() - 1);
                                                String category = categoryList.toString();
                                                cartlistTimeline.get(i).setSp_category(category.substring(1, category.length() - 1).concat(" ..."));
                                            }
                                        }
                                    }
                                    timelineItem.getValue().clear();
                                    timelineItem.getValue().addAll(cartlistTimeline);

                                    timelineAdapter.setValue(new IndicatorAdapter<>(timelineItem.getValue(), App.INSTANCE, new TimeLineViewCallback<Timeline>() {
                                        public View onBindView(Timeline model, FrameLayout container, final int position) {
                                            TimeLineItemBinding timeLineItemBinding = DataBindingUtil.inflate(LayoutInflater.from(App.INSTANCE), R.layout.time_line_item, container, false);
                                            timeLineItemBinding.setTL(model);
                                            timeLineItemBinding.content.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    System.out.println(position);
                                                }
                                            });

                                            View view = timeLineItemBinding.getRoot();
                                            return view;
                                        }
                                    }));

                                    timelineAdapter.getValue().setOnItemClickListener(new IndicatorAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            timelineSelect.setValue(position);
                                        }
                                    });

                                    if (itemViewWidth.getValue() == null || itemViewWidth.getValue() == 0) {
                                        itemViewWidth.setValue((recyclerView.getChildAt(0).getWidth() - ((ViewGroup) recyclerView.getChildAt(0)).getChildAt(3).getWidth()) - 30);
                                    }
                                    if (itemViewHeight.getValue() == null || itemViewHeight.getValue() == 0) {
                                        itemViewHeight.setValue(recyclerView.getChildAt(0).getHeight());
                                    }
                                }

                                @Override
                                public void onFailure(Call call, Throwable t) {
                                    Toast.makeText(App.INSTANCE, "통신 불량" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
                                    t.printStackTrace();
                                }
                            });

                            isCartlist.setValue(true); // 카트리스트 타임라인뷰 보이기
                            onCartlistClickListener.onClick(view); // 네비게이션바 닫기
                            //cartlistWidth.setValue(200);
                        }
                    });
        }
    }

    public void onAirlineCancel() {
        isAirlineOk.setValue(false);
        isAirline.setValue(false);
        isAirlineResult.setValue(false);
        isSearching.setValue(false);
        onAirlineSearchClickListener.onResetClick();
    }

    public void onAirlineClick() {
        isAirline.setValue(!isAirline.getValue());
        if (isAirlineOk.getValue()) {
            isAirlineOk.setValue(false);
        }
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

    @Override
    public void onFailure(Call call, Throwable t) {
        Toast.makeText(App.INSTANCE, "통신 불량" + t.getMessage(), Toast.LENGTH_SHORT).show();
        Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
        t.printStackTrace();
    }
}
