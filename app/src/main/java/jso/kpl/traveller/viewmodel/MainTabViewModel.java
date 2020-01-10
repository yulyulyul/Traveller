package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.FrameLayout;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.UserAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.LoginSelect;
import jso.kpl.traveller.ui.MsgList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public void onMsgBoxClicked(){
        Intent intent = new Intent(App.INSTANCE, MsgList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.INSTANCE.startActivity(intent);
    }

    public void onLogoutClicked() {
        SharedPreferences sp = App.INSTANCE.getSharedPreferences("auto_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove("auto_login");
        editor.clear();
        editor.commit();

        WebService.INSTANCE.getClient()
                .create(UserAPI.class)
                .goLogout(App.Companion.getUser().getU_userid())
                .enqueue(new Callback<ResponseResult<Integer>>() {
                    @Override
                    public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                        int res_type = response.body().getRes_type();

                        if(res_type == 1){
                            Intent intent = new Intent(App.INSTANCE, LoginSelect.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            App.INSTANCE.startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                        App.Companion.sendToast("서버 에러가 발생했습니다.");
                    }
                });

    }
}
