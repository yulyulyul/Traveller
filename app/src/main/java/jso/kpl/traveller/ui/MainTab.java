package jso.kpl.traveller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MainTabBinding;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.ui.Fragment.AllRouteList;
import jso.kpl.traveller.ui.Fragment.MyPage;
import jso.kpl.traveller.viewmodel.MainTabViewModel;

public class MainTab extends AppCompatActivity {

    User user;

    FragmentManager fm;

    MyPage myPage;
    AllRouteList allRouteList;

    MainTabBinding binding;
    MainTabViewModel mainTabVm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {

            boolean isLogin = getIntent().getSerializableExtra("user") instanceof User;

            if (isLogin) {
                user = (User) getIntent().getSerializableExtra("user");
                App.Companion.setUser(user);
            }

        }

        fm = getSupportFragmentManager();

        mainTabVm = new MainTabViewModel();

        binding = DataBindingUtil.setContentView(this, R.layout.main_tab);
        binding.setMainTabVm(mainTabVm);
        binding.setLifecycleOwner(this);

        transitedContainer(binding.mainContainer);
    }

    public void transitedContainer(final FrameLayout layout) {

        if (myPage == null) {
            myPage = new MyPage();

            fm.beginTransaction()
                    .add(layout.getId(), myPage.newInstance(App.Companion.getUser()), "myPage")
                    .commit();
        }

        binding.getMainTabVm().TAP_POS.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                if (integer == 0) {

                    if (allRouteList != null) {
                        fm.beginTransaction()
                                .hide(fm.findFragmentByTag("allRouteList"))
                                .show(fm.findFragmentByTag("myPage"))
                                .commit();
                    }

                } else if (integer == 1) {

                    if (allRouteList == null) {
                        allRouteList = new AllRouteList();

                        fm.beginTransaction()
                                .hide(fm.findFragmentByTag("myPage"))
                                .add(layout.getId(), allRouteList.newInstance(new MyPageItem(App.Companion.getUser().getU_userid(), 5)), "allRouteList")
                                .commit();
                    } else {
                        fm.beginTransaction()
                                .hide(fm.findFragmentByTag("myPage"))
                                .show(fm.findFragmentByTag("allRouteList"))
                                .commit();
                    }
                }
            }
        });
    }

    public void replaceFragment(Fragment fragment, String tag) {
        fm.beginTransaction().replace(binding.mainContainer.getId(), fragment, tag).commit();
    }
}
