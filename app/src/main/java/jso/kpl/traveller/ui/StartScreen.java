package jso.kpl.traveller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import jso.kpl.traveller.R;
import jso.kpl.traveller.model.LoginUser;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.network.UserAPI;
import jso.kpl.traveller.network.WebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartScreen extends AppCompatActivity {

    String TAG = "Trav.StartScreen";
    boolean isPush = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

//        Bundle bundle = getIntent().getExtras();
//
//        if (bundle != null){
//            isPush = true;
//            Log.d(TAG, "미 확인 푸시가 있습니다.");
//        } else{
//            isPush = false;
//            Log.d(TAG, "미 확인 푸시가 없습니다.");
//        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoLogin();
            }
        }, 500);
    }

    // 자동 로그인 SharedPreferences에 자동 로그인 상태가 저장되어있다면 MainTab 화면으로 이동한다.
    // 아닐 시 LoginSelect 화면으로 이동한다.
    public void autoLogin() {
        SharedPreferences sp = getSharedPreferences("auto_login", MODE_PRIVATE);

        String email = sp.getString("u_email", "");
        String pwd = sp.getString("u_pwd", "");

        if (!email.equals("") && !pwd.equals("")) {
            WebService.INSTANCE.getClient().create(UserAPI.class)
                    .goLogin(new LoginUser(email, pwd))
                    .enqueue(new Callback<ResponseResult<User>>() {
                        @Override
                        public void onResponse(Call<ResponseResult<User>> call, Response<ResponseResult<User>> response) {
                            int res_type = response.body().getRes_type();

                            if (res_type == 1) {
                                User receiveUser = response.body().getRes_obj();

                                if (receiveUser != null) {
                                    Intent intent = new Intent(getApplication(), MainTab.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                              //      intent.putExtra("isPush", isPush);
                                    intent.putExtra("user", receiveUser);
                                    startActivity(intent);

                                    overridePendingTransition(R.anim.enter_fade_in, R.anim.exit_fade_out);

                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseResult<User>> call, Throwable t) {
                            finish();
                            Log.d(TAG, "onFailure: " + t.getMessage());
                        }

                    });
        } else {
            Intent intent = new Intent(this, LoginSelect.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_fade_in, R.anim.exit_fade_out);
            finish();
        }
    }
}