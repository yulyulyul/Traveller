package jso.kpl.traveller.thirdpartyapi.kakao;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.UserAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.SignUp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionCallback implements ISessionCallback {

    public String TAG = "Trav.SessionCallback";

    Context context;

    public SessionCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onSessionOpened() {
        Log.e(TAG, "카카오 로그인 성공 ");
        requestMe();
    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        if (exception != null) {
            Log.e(TAG, "exception : " + exception);
        }
    }

    /** 사용자에 대한 정보를 가져온다 **/
    private void requestMe() {

        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                super.onFailure(errorResult);
                Log.e(TAG, "requestMe onFailure message : " + errorResult.getErrorMessage());
            }

            @Override
            public void onFailureForUiThread(ErrorResult errorResult) {
                super.onFailureForUiThread(errorResult);
                Log.e(TAG, "requestMe onFailureForUiThread message : " + errorResult.getErrorMessage());
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e(TAG, "requestMe onSessionClosed message : " + errorResult.getErrorMessage());
            }

            /*
                여기서 사용자의 정보를 얻을 수 있다.
             */
            @Override
            public void onSuccess(final MeV2Response result) {

                Call<ResponseResult<Integer>> authCall = WebService.INSTANCE.getClient().create(UserAPI.class).authEmail(result.getKakaoAccount().getEmail());
                authCall.enqueue(new Callback<ResponseResult<Integer>>() {
                    @Override
                    public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                        ResponseResult<Integer> responseResult = response.body();

                        if (responseResult.getRes_obj() == 0) {
                            App.Companion.sendToast("이미 가입된 이메일이 있습니다..");
                        } else if (responseResult.getRes_obj() == 1) {
                            Log.e(TAG, "카카오 아이디: " + result.getKakaoAccount().getEmail());

                            Intent intent = new Intent(context, SignUp.class);
                            intent.putExtra("email", result.getKakaoAccount().getEmail());
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



            }
        });
    }

}

