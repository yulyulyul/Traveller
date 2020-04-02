package jso.kpl.traveller.thirdpartyapi.facebook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.UserAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.SignUp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginCallback implements FacebookCallback<LoginResult> {

    public static String TAG = "Trav.LoginCallback";

    Context context;

    public LoginCallback(Context context) {
        this.context = context;
    }

    // 로그인 성공 시 호출 됩니다. Access Token 발급 성공.
    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d(TAG, "onSuccess");
        Log.d(TAG, "loginResult : " + loginResult);

        requestMe(loginResult.getAccessToken());
    }

    // 로그인 창을 닫을 경우, 호출됩니다.
    @Override
    public void onCancel() {
        Log.e(TAG, "onCancel");
    }


    // 로그인 실패 시에 호출됩니다.
    @Override
    public void onError(FacebookException error) {
        Log.e(TAG, "onError : " + error.getMessage());
    }

    // 사용자 정보 요청
    public void requestMe(AccessToken token) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d(TAG, "result : " + object.toString());

                        try {

                            final String email = object.getString("email");
                            Call<ResponseResult<Integer>> authCall = WebService.INSTANCE.getClient().create(UserAPI.class).authEmail(object.getString("email"));
                            authCall.enqueue(new Callback<ResponseResult<Integer>>() {
                                @Override
                                public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                                    ResponseResult<Integer> responseResult = response.body();

                                    if (responseResult.getRes_obj() == 0) {
                                        App.Companion.sendToast("이미 가입된 이메일이 있습니다..");
                                    } else if (responseResult.getRes_obj() == 1) {
                                        Intent intent = new Intent(context, SignUp.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("auth", true);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                                    t.printStackTrace();
                                    App.Companion.sendToast("서버 에러");
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}