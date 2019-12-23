package jso.kpl.traveller.bindings;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.util.CurrencyChange;
import jso.kpl.traveller.util.GridSpacingItemDecoration;
import jso.kpl.traveller.util.JavaUtil;
import me.jerryhanks.timelineview.IndicatorAdapter;
import me.jerryhanks.timelineview.TimeLineView;

//RecyclerView에 Adapter 적용
public class BindingAdapters {
    @BindingAdapter({"setLinearRvAdapter"})
    public static void bindRecyclerViewAdapter(@NotNull RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {

        Log.d("Trav.SetAdapter", "Adapter");

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter({"setLinearHorRvAdapter"})
    public static void bindRecyclerViewHorAdapter(@NotNull RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {

        Log.d("Trav.SetAdapter", "Adapter");

        recyclerView.setLayoutManager(new LinearLayoutManager(App.INSTANCE, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter({"setGridRvAdapter", "setGridSpan"})
    public static void onBindGridRvAdapter(RecyclerView rv, RecyclerView.Adapter<?> adapter, int count) {

        rv.setLayoutManager(new GridLayoutManager(rv.getContext(), count));
        rv.setAdapter(adapter);
    }

    @BindingAdapter("setGridPatternRvAdapter")
    public static void onBindInsGridRvAdapter(RecyclerView rv, RecyclerView.Adapter<?> adapter) {

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(App.INSTANCE, 12, RecyclerView.VERTICAL, false);
        //아이템을 2/3/2/3/..패턴으로 화면에 출력한다.


        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                int gridPosition = position % 12;

                if (gridPosition == 1 || gridPosition == 7) {

                    return 8;
                } else if (gridPosition == 0 || gridPosition == 2) {
                    return 4;
                } else {
                    return 0;
                }
//                if(gridPosition >= 0 && gridPosition < 6){
//
//                    switch (gridPosition) {
//                        case 0:
//                        case 2:
//                        case 3:
//                        case 4:
//                        case 5:
//                            return 4;
//                        case 1:
//                            return 8;
//                    }
//
//
//                }else if(gridPosition >= 6 && gridPosition < 12){
//                    switch (gridPosition) {
//                        case 7:
//                        case 8:
//                        case 9:
//                        case 10:
//                        case 11:
//                            return 4;
//                        case 6:
//                            return 8;
//                    }
//
//                }
//
//               return 0;
            }
        });

//        f (position % 12 == 0 || position % 12 == 7) {
//            SpannedGridLayoutManager.SpanInfo(2, 2)
//        } else {
//            SpannedGridLayoutManager.SpanInfo(1, 1)
//        }
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(adapter);
    }

    @BindingAdapter({"setStaggerRvAdapter"})
    public static void onBindStaggerRvAdapter(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 0);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        staggeredGridLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        recyclerView.setAdapter(adapter);
    }


    //탭레이아웃 설정
    //1st [parameter]는 해당 뷰, 2nd [parameter]는 탭레이아웃의 탭 리스트, 3rd [parameter] 탭 클릭 이벤트 리스너
    @BindingAdapter({"setTabLayout", "addOnTabSelected"})
    public static void onBindTabLayout(TabLayout tabLayout, List<TabLayout.Tab> tabs, TabLayout.OnTabSelectedListener listener) {

        for (TabLayout.Tab tab : tabs) {
            tabLayout.addTab(tab);
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(listener);
    }

    @BindingAdapter({"setFragmentManager", "setInitFragment"})
    public static void onBindFragment(FrameLayout frameLayout, FragmentManager fm, Fragment fragment) {
        fm.beginTransaction().add(frameLayout.getId(), fragment).commit();
    }

    //이미지뷰에 이미지를 넣는 바인딩 어댑터, 매개변수는 String 형식으로 받는다.
    @BindingAdapter({"setImg"})
    public static void onBindImage(ImageView iv, String imgUri) {

        String path;

        if(imgUri != null && imgUri.contains("file:///"))
            path = imgUri;
        else if(imgUri != null && (imgUri.contains(".jpg") || imgUri.contains(".gif") || imgUri.contains(".png")))
            path = App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/" + imgUri;
        else
            path = "android.resource://jso.kpl.traveller/drawable/" + imgUri;


        Log.d("Trav.img", "setImg: " + path);

        Glide.with(iv.getContext())
                .load(path)
                .apply(new RequestOptions().placeholder(R.drawable.i_blank_person_icon).error(R.drawable.i_blank_person_icon))
                .into(iv);
    }

    //이미지 원형 글라이드 - 이미지뷰에 이미지를 넣는 바인딩 어댑터, 매개변수는 String 형식으로 받는다.
    @BindingAdapter({"setCircleImg"})
    public static void onBindCircleImage(ImageView iv, String imgUri) {

        String path;

        if(imgUri != null && imgUri.contains("file:///"))
            path = imgUri;
        else if(imgUri != null && (imgUri.contains(".jpg") || imgUri.contains(".gif") || imgUri.contains(".png")))
            path = App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/" + imgUri;
        else
            path = "android.resource://jso.kpl.traveller/drawable/" + imgUri;

        Log.d("Trav.img", "onBindCircleImage: " + path);

        RequestOptions options
                = RequestOptions.bitmapTransform(new CircleCrop()).error(R.drawable.i_blank_person_icon);

        Glide.with(iv.getContext())
                .load(path)
                .apply(options)
                .into(iv);
    }

    @BindingAdapter({"setFitCenterImg"})
    public static void onBindFitCenterImage(ImageView iv, String imgUri) {

        String path;

        if(imgUri != null && imgUri.contains("file:///"))
            path = imgUri;
        else if(imgUri != null && (imgUri.contains(".jpg") || imgUri.contains(".gif") || imgUri.contains(".png")))
            path = App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/" + imgUri;
        else
            path = "android.resource://jso.kpl.traveller/drawable/" + imgUri;

        Log.d("Trav.img", "onBindFitCenterImage: " + path);
        RequestOptions options
                = RequestOptions.bitmapTransform(new FitCenter()).error(R.drawable.i_empty_image_icon);

        Glide.with(iv.getContext())
                .load(path)
                .centerCrop()
                .apply(options)
                .into(iv);
    }

    @BindingAdapter({"setDecoration"})
    public static void onBindSetDecoration(RecyclerView recyclerView, GridSpacingItemDecoration decoration) {
        recyclerView.addItemDecoration(decoration);
    }


    @BindingAdapter("setMAX")
    public static void bindMaxValue(CrystalRangeSeekbar seekbar, int max) {
        seekbar.setMaxValue(max);
    }

    @BindingAdapter("setMIN")
    public static void bindMinValue(CrystalRangeSeekbar seekbar, int min) {
        seekbar.setMinValue(min);
    }

    //클릭 리스너 범용
    @BindingAdapter("OnClickListener")
    public static void bindOnClickListener(View v, View.OnClickListener listener) {
        v.setOnClickListener(listener);
    }

    // 화면 상 첫 포커싱
    @BindingAdapter("setInitFocusing")
    public static void bindSetFocus(View v, boolean b) {
        v.setFocusable(b);
        v.setFocusableInTouchMode(b);
        v.requestFocus();
    }

    @BindingAdapter("setDontEnter")
    public static void bindDontEnter(View v, String a) {
        //엔터입력시 없으면 입력 하지 않음.
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }

                return false;
            }
        });

    }

    //프래그먼트 초기값 설정 바인딩 어댑터
    @BindingAdapter({"setFragmentManger", "setInitFragment"})
    public static void bindSetFragment(FrameLayout container, FragmentManager fm, Fragment fragment) {

        if (fragment != null)
            fm.beginTransaction().setCustomAnimations(R.anim.enter_to_top, R.anim.exit_to_down).add(container.getId(), fragment, "post_tag").commit();
    }

    //금액 EditText를 통화 단위로 변경, 누르면 일반 숫자로 나열
    //최대 입력 길이는 10
    @BindingAdapter("onChangeMoney")
    public static void bindChangeMoney(EditText et, @Nullable String money) {

        final int len = 10;

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    Log.d("TAG", "bindChangeMoney: focus on");

                    if (!((EditText) v).getText().equals("")) {
                        String a = ((EditText) v).getText().toString().replace("₩", "");
                        a = a.replace(",", "");

                        ((EditText) v).setText(a);

                        ((EditText) v).setFilters(new InputFilter[]{
                                new InputFilter.LengthFilter(len)
                        });

                    }
                } else {

                    try {
                        if (((EditText) v).getText().toString().equals("")) {
                            Log.d("TAG", "onFocusChange: off");
                            ((EditText) v).setText("0");
                        }

                        int len = ((EditText) v).length();

                        int change_length = (10 + len / 3 + 1);

                        ((EditText) v).setFilters(new InputFilter[]{
                                new InputFilter.LengthFilter(change_length)});

                        Long lngMoney = Long.parseLong(((EditText) v).getText().toString());
                        String cc = CurrencyChange.moneyFormatToWon(lngMoney);

                        ((EditText) v).setText(cc);

                        Log.d("TAG", "bindChangeMoney: focus off" + lngMoney);
                    } catch (Exception e) {
                        Toast.makeText(App.INSTANCE, "너무 많다..", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    @BindingAdapter("extendTouchRange")
    public static void bindTouchRange(final View v, final int size) {
        final View parent = (View) v.getParent();

        parent.post(new Runnable() {
            @Override
            public void run() {
                final Rect r = new Rect();
                r.bottom = JavaUtil.dpToPx(size);
                r.top = JavaUtil.dpToPx(size);
                r.left = JavaUtil.dpToPx(size);
                r.right = JavaUtil.dpToPx(size);

                parent.setTouchDelegate(new TouchDelegate(r, v));
            }
        });
    }

    @BindingAdapter({"bind:loadImage"})
    public static void loadImage(ImageView imageView, int imageUrl) {
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .error(R.drawable.i_empty_image_icon)
                .into(imageView);
    }

    // 동적 width 조절
    @BindingAdapter("layout_width")
    public static void setLayoutWidth(View view, float width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) width;
        view.setLayoutParams(layoutParams);
    }

    // 동적 height 조절
    @BindingAdapter("layout_height")
    public static void setLayoutHeight(View view, float height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) height;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter({"setTimelineAdapter"})
    public static void onBindTimelineAdapter(TimeLineView timelineView, IndicatorAdapter adapter) {
        timelineView.setIndicatorAdapter(adapter);
    }

    @BindingAdapter("setViewPagerAdapter")
    public static void onBindVpAdapter(ViewPager vp, PagerAdapter adapter){

        vp.setAdapter(adapter);
        vp.setClipToPadding(false);

        int dpValue = 16;
        float d = App.INSTANCE.getResources().getDisplayMetrics().density;
        int margin = (int) (dpValue * d);
        vp.setPadding(margin, 0, margin, 0);
        vp.setPageMargin(margin/2);
    }

    @BindingAdapter({"onRefreshListener", "checkRefresh"})
    public static void bindRefreshListener(SwipeRefreshLayout swipeRefreshLayout, SwipeRefreshLayout.OnRefreshListener listener, boolean isRefresh) {

        swipeRefreshLayout.setOnRefreshListener(listener);
        swipeRefreshLayout.setRefreshing(isRefresh);
    }

    //포커스 완료 바인딩 어댑터
    @BindingAdapter("setFocusComplete")
    public static void bindFocusComplete(View v, View.OnFocusChangeListener listener) {
        v.setOnFocusChangeListener(listener);
    }

}
