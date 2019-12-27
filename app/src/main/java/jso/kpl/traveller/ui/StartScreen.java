package jso.kpl.traveller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoLogin();
            }
        }, 500);
    }

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

                                    intent.putExtra("user", receiveUser);

                                    startActivity(intent);

                                    overridePendingTransition(R.anim.enter_silde_left, R.anim.exit_silde_right);

                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseResult<User>> call, Throwable t) {
                            finish();
                        }

                    });
        } else {
            Intent intent = new Intent(this, LoginSelect.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_silde_left, R.anim.exit_silde_right);

            finish();
        }
    }
}