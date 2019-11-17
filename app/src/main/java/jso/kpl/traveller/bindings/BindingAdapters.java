package jso.kpl.traveller.bindings;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.ui.EditingPost;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
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
    public static void onBindTabLayout(TabLayout tabLayout, List<TabLayout.Tab> tabs, TabLayout.OnTabSelectedListener listener){

        for(TabLayout.Tab tab : tabs){
            tabLayout.addTab(tab);
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(listener);
    }

    @BindingAdapter({"setFragmentManager","setInitFragment"})
    public static void onBindFragment(FrameLayout frameLayout, FragmentManager fm, Fragment fragment){
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
    public static void onBindSetDecoration(RecyclerView recyclerView, GridSpacingItemDecoration decoration){
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

    @BindingAdapter("setInitFocusing")
    public static void bindSetFocus(View v, boolean b){
        v.setFocusable(b);
        v.setFocusableInTouchMode(b);
        v.requestFocus();
    }

    //프래그먼트 초기값 설정 바인딩 어댑터
    @BindingAdapter({"setFragmentManger", "setInitFragment"})
    public static void bindSetFragment(FrameLayout container, FragmentManager fm, Fragment fragment){

        if(fragment != null)
            fm.beginTransaction().add(container.getId(), fragment, "post_tag").commit();
    }
}
