package jso.kpl.traveller.bindings;

import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.util.CurrencyChange;
import jso.kpl.traveller.util.GridSpacingItemDecoration;

//RecyclerView에 Adapter 적용
public class BindingAdapters {
    @BindingAdapter({"setLinearRvAdapter"})

    public static void bindRecyclerViewAdapter(@NotNull RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {

        Log.d("Trav.SetAdapter", "Adapter");

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter("setGridRvAdapter")
    public static void onBindGridRvAdapter(RecyclerView rv, RecyclerView.Adapter<?> adapter) {

        rv.setLayoutManager(new GridLayoutManager(rv.getContext(), 5));
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

        Glide.with(iv.getContext())
                .load(imgUri)
                .apply(new RequestOptions().placeholder(R.drawable.i_blank_person_icon).error(R.drawable.i_blank_person_icon))
                .into(iv);
    }

    //이미지 원형 글라이드 - 이미지뷰에 이미지를 넣는 바인딩 어댑터, 매개변수는 String 형식으로 받는다.
    @BindingAdapter({"setCircleImg"})
    public static void onBindCircleImage(ImageView iv, String imgUri) {

        RequestOptions options
                = RequestOptions.bitmapTransform(new CircleCrop()).error(R.drawable.i_blank_person_icon);

        Glide.with(iv.getContext())
                .load(imgUri)
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
    public static void bindDontEnter(View v, String a){
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

}
