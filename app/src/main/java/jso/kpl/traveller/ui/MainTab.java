package jso.kpl.traveller.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MainTabBinding;
import jso.kpl.traveller.model.Message;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.network.MsgAPI;
import jso.kpl.traveller.network.MsgWebService;
import jso.kpl.traveller.ui.Fragment.AllRouteList;
import jso.kpl.traveller.ui.Fragment.MyPage;
import jso.kpl.traveller.viewmodel.MainTabViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainTab extends AppCompatActivity {

    String TAG = "Trav.MainTab";
    User user;

    FragmentManager fm;

    MyPage myPage;
    AllRouteList allRouteList;

    public MainTabBinding binding;
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

        //binding.mainDrawerLayout.openDrawer(Gravity.RIGHT);
        binding.mainDrawerLayout.closeDrawer(Gravity.RIGHT);

        MsgWebService.INSTANCE.getClient().create(MsgAPI.class)
                .sendBackPush(App.Companion.getUser().getU_userid()).enqueue(new Callback<ResponseResult<Integer>>() {
            @Override
            public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                if(response.body().getRes_type() == 1){


                    Log.d(TAG, "밀린 푸시 Go");
//                    Intent intent = new Intent(MainTab.this, MsgList.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        binding.getMainTabVm().isMenuLD.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean)
                    binding.mainDrawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });

//        if(getIntent().getBooleanExtra("isPush", false)){
//
//            Log.d(TAG, "푸시 아이템: " + getIntent().getBooleanExtra("isPush", false));
//
//            Intent intent = new Intent(App.INSTANCE, MsgList.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            App.INSTANCE.startActivity(intent);
//        }
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
