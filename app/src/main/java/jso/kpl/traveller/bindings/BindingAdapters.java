package jso.kpl.traveller.bindings;

import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import jso.kpl.traveller.R;
import jso.kpl.traveller.ui.adapters.ImageRouteListAdapter;
import jso.kpl.traveller.ui.adapters.MainRouteListAdapter;
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

    @BindingAdapter({"setVpAdapter"})
    public static void onBindSetVpAdapter(ViewPager viewPager, MainRouteListAdapter adapter){
        viewPager.setAdapter(adapter);
    }

    @BindingAdapter({"addOnPageChangedListener"})
    public static void onBindAddOnPageChanged(final ViewPager viewPager, TabLayout tabLayout){
        tabLayout.addTab(tabLayout.newTab().setText("1st"));
        tabLayout.addTab(tabLayout.newTab().setText("2st"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
}
