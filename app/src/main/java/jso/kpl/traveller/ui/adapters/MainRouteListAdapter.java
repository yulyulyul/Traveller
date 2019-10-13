package jso.kpl.traveller.ui.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import jso.kpl.toyroutesearch.view.Fragment.ImageRouteList;
import jso.kpl.toyroutesearch.view.Fragment.PostRouteList;

public class MainRouteListAdapter extends FragmentStatePagerAdapter {

    String TAG = "Trav.MainRouteListAdapter .";
    int numOfPage;

    public MainRouteListAdapter(@NonNull FragmentManager fm, int numOfPage) {
        super(fm, numOfPage);
        this.numOfPage = numOfPage;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ImageRouteList imageRouteList = new ImageRouteList();

                Log.d(TAG + "1st", "ImageRouteList");
                return imageRouteList;
            case 1:
                PostRouteList postRouteList = new PostRouteList();

                Log.d(TAG + "2nd", "PostRouteList");
                return postRouteList;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numOfPage;
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

}
