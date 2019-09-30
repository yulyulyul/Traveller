package jso.kpl.traveller.thirdpartyapi.kakao;

import android.app.Application;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.viewmodel.LoginSelectViewModel;

public class SessionCallback implements ISessionCallback, LoginSelectViewModel.SignUpListener {

    public String TAG = "Trav.SessionCallback";

    public LoginSelectViewModel.SignUpListener signUpListener;


    public String email;

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

                email = result.getKakaoAccount().getEmail();
                Log.e(TAG, "카카오 아이디: " + email);

//                 signUpListener = new LoginSelectViewModel.SignUpListener() {
//                    @NotNull
//                    @Override
//                    public String goToSignUp() {
//                        return result.getKakaoAccount().getEmail();
//                    }
//                };
            }
        });
    }


    @NotNull
    @Override
    public String goToSignUp() {
        return email;
    }
}

