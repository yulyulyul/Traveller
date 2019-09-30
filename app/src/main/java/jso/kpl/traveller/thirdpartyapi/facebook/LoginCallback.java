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

import jso.kpl.traveller.ui.SignUp;


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
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        Log.d(TAG, "result : "+object.toString());

                        try {
                            Intent intent = new Intent(context, SignUp.class);
                            intent.putExtra("email", object.getString("email").toString());
                            intent.putExtra("auth", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
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