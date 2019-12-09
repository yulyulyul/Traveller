package jso.kpl.traveller.ui;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MainTabBinding;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.viewmodel.MainTabViewModel;

public class MainTab extends AppCompatActivity {

    User user;

    FragmentManager fm;

    MyPage myPage;
    RouteList routeList = new RouteList();

    MainTabBinding binding;
    MainTabViewModel mainTabVm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent() != null){

            boolean isLogin = getIntent().getSerializableExtra("user") instanceof User;

            if(isLogin){
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

        if(myPage == null)
            myPage = new MyPage();

        binding.getMainTabVm().TAP_POS.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 0){
                    fm.beginTransaction().replace(layout.getId(), myPage.newInstance(App.Companion.getUser()), "main_tab").commit();
                } else if(integer == 1){
                    fm.beginTransaction().replace(layout.getId(), myPage.newInstance(App.Companion.getUser()), "main_tab").commit();
                }
            }
        });

    }
}
