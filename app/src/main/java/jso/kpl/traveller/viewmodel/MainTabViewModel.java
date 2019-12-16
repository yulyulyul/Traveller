package jso.kpl.traveller.viewmodel;

import android.widget.FrameLayout;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;

public class MainTabViewModel extends ViewModel {

    public MutableLiveData<Integer> TAP_POS = new MutableLiveData<>();

    public TabLayout.OnTabSelectedListener listener;

    public MainTabViewModel() {

        TAP_POS.setValue(0);

    }

    //탭레이아웃의 탭 추가
    public List<TabLayout.Tab> getTabs(TabLayout tabLayout) {

        List<TabLayout.Tab> tabs = new ArrayList<>();

        tabs.add(tabLayout.newTab().setIcon(R.drawable.i_home_icon));

        tabs.add(tabLayout.newTab().setIcon(R.drawable.i_clipboard_icon));

        tabs.get(0).getIcon().setTint(App.INSTANCE.getResources().getColor(R.color.clicked));
        tabs.get(1).getIcon().setTint(App.INSTANCE.getResources().getColor(R.color.non_clicked));

        return tabs;
    }

    //탭레이아웃의 콜백 리스너
    public TabLayout.OnTabSelectedListener onTabSelectedListener(final FrameLayout container) {

        listener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                TAP_POS.setValue(tab.getPosition());

                tab.getIcon().setTint(App.INSTANCE.getResources().getColor(R.color.clicked));

                //클릭하는 탭의 번호
                final int CLICK_NO = tab.getPosition();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                tab.getIcon().setTint(App.INSTANCE.getResources().getColor(R.color.non_clicked));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };

        return listener;
    }

}
