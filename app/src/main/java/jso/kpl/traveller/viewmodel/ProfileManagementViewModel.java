package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.network.ProfileAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.LoginSelect;
import jso.kpl.traveller.util.JavaUtil;
import jso.kpl.traveller.util.RegexMethod;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileManagementViewModel extends ViewModel implements Callback {

    String TAG = "Trav.ProfileVm.";

    public String title = "프로필";

    //프로필 API
    ProfileAPI profileAPI = WebService.INSTANCE.getClient().create(ProfileAPI.class);

    //이미지 URI -> MultipartBody
    MultipartBody.Part imgBody;

    //유저 라이브 데이터
    public MutableLiveData<User> userLD = new MutableLiveData<>();

    //RevisePwd:패스워드 변경 라이브 데이터 순서대로 현재 비밀번호, 바꿀 비밀번호
    public MutableLiveData<String> currentPwd = new MutableLiveData<>();
    public MutableLiveData<String> updatePwd = new MutableLiveData<>();

    public MutableLiveData<String> updateNick = new MutableLiveData<>();

    //비밀번호 수정 버튼, 프로필 이미지 수정 버튼
    public View.OnClickListener onUpdateNickListener;
    public View.OnClickListener onImgClickListener;

    //비밀번호 수정했을 때 가져오는 값
    public MutableLiveData<Integer> isRevise = new MutableLiveData<>();
    public MutableLiveData<Boolean> isPwd = new MutableLiveData<>();
    public MutableLiveData<Boolean> isNick = new MutableLiveData<>();

    public MutableLiveData<List<Integer>> cnts = new MutableLiveData<>();

    public View.OnFocusChangeListener currentPwdFocus, updatePwdFocus;

    public MutableLiveData<String> wrongCurrentPwd = new MutableLiveData<>();
    public MutableLiveData<String> wrongUpdatePwd = new MutableLiveData<>();
    public MutableLiveData<String> wrongUpdateNick = new MutableLiveData<>();

    //0:프로필 이미지 수정 통신, 1:비밀번호 수정 통신
    public int callType;

    public ProfileManagementViewModel() {
        userLD.setValue(App.Companion.getUser());
        cnts.setValue(new ArrayList<Integer>());

        isNick.setValue(false);
        isPwd.setValue(false);

        currentPwdFocus = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (RegexMethod.isPasswordValid((currentPwd.getValue()))) {
                        Log.d(TAG, "현재 비밀번호 패스");
                    } else {
                        wrongCurrentPwd.setValue("* 영소문 숫자 조합 8자 이상 특수문자는 !@#$%^*만 사용 가능");
                    }
                } else {
                    wrongCurrentPwd.setValue(null);
                }
            }
        };

        updatePwdFocus = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (RegexMethod.isPasswordValid((updatePwd.getValue()))) {
                        Log.d(TAG, "수정 비밀번호 패스");
                    } else {
                        wrongUpdatePwd.setValue("* 영소문 숫자 조합 8자 이상 특수문자는 !@#$%^*만 사용 가능");
                    }
                } else {
                    wrongUpdatePwd.setValue(null);
                }
            }
        };
    }

    public void updateCall(final int type) {

        callType = type;

        if (type == 0) {

            isNick.setValue(false);
            updateNick.setValue(null);

            isPwd.setValue(false);
            currentPwd.setValue(null);
            updateNick.setValue(null);

            File imgFile = new File(Uri.parse(userLD.getValue().getU_profile_img()).getPath());
            //******multipart/form-data는 파일 형식의 파싱법
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
            imgBody = MultipartBody.Part.createFormData("u_profile_img", imgFile.getName(), requestFile);

            profileAPI.updateProfileImg(userLD.getValue().getU_userid(), imgBody).enqueue(this);
        } else if(type == 1) {
            if (RegexMethod.isPasswordValid(currentPwd.getValue()) && RegexMethod.isPasswordValid(updatePwd.getValue())) {
                profileAPI.updatePwd(App.Companion.getUser().getU_userid(),
                        JavaUtil.returnSHA256(currentPwd.getValue()),
                        JavaUtil.returnSHA256(updatePwd.getValue())).enqueue(this);
            }
        } else {
            if(RegexMethod.isNickValid(updateNick.getValue())){
                profileAPI.updateNick(App.Companion.getUser().getU_userid(), updateNick.getValue()).enqueue(this);
            } else {
                wrongUpdateNick.setValue("* 자음 + 모음 한글, 영어, 숫자 조합");
            }
        }
    }

    public void onReviseClicked(View view) {

        Log.d(TAG, "onReviseClicked: 스바련아 실행 웨 안되냐 ");
        
        switch (view.getId()) {

            case R.id.reviseNick:

                if (isPwd.getValue())
                    isPwd.setValue(!isPwd.getValue());

                isNick.setValue(!isNick.getValue());
                Log.d(TAG, "onReviseClicked: 닉네임");

                break;

            case R.id.revisePwd:

                if (isNick.getValue())
                    isNick.setValue(!isNick.getValue());

                isPwd.setValue(!isPwd.getValue());
                Log.d(TAG, "onReviseClicked: 비밀번호");

                break;

        }

    }

    public void onUpdatePwdClicked() {
        updateCall(1);
        Log.d(TAG, "비밀번호 변경");
    }

    public void onLogoutClicked() {
        SharedPreferences sp = App.INSTANCE.getSharedPreferences("auto_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove("auto_login");
        editor.clear();
        editor.commit();

        Intent intent = new Intent(App.INSTANCE, LoginSelect.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.INSTANCE.startActivity(intent);
    }

    public void userInfoCall() {

        profileAPI.loadUserInfo(App.Companion.getUser().getU_userid()).enqueue(new Callback<ResponseResult<List<Integer>>>() {
            @Override
            public void onResponse(Call<ResponseResult<List<Integer>>> call, Response<ResponseResult<List<Integer>>> response) {
                if (response.body() != null) {
                    ResponseResult<List<Integer>> result = response.body();

                    if (result.getRes_type() == 1) {
                        cnts.setValue(result.getRes_obj());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<List<Integer>>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

    @Override
    public void onCleared() {
        super.onCleared();
    }

    @Override
    public void onResponse(Call call, Response response) {

        if (response.body() != null) {

            ResponseResult<Object> result = (ResponseResult<Object>) response.body();

            if (result.getRes_type() == 1) {

                if (callType == 0) {
                    App.Companion.getUser().setU_profile_img((String) result.getRes_obj());
                    userLD.setValue(App.Companion.getUser());
                } else if(callType == 1) {
                    isRevise.setValue((Integer) result.getRes_obj());

                    currentPwd.setValue(null);
                    updatePwd.setValue(null);
                    isPwd.setValue(false);
                } else {
                    isNick.setValue(false);
                    updateNick.setValue(null);

                    App.Companion.sendToast("성공적으로 닉네임 수정에 성공하셨습니다.");
                }

            } else if (result.getRes_type() == 0) {

                if (callType == 0)
                    App.Companion.sendToast("이미지 등록에 실패했습니다.");
                else if(callType == 1){
                    App.Companion.sendToast("비밀번호 변경에 실패하셨습니다.");
                    isRevise.setValue((Integer) result.getRes_obj());
                } else{
                    App.Companion.sendToast("닉네임 변경에 실패하셨습니다.");
                }

            }

        } else {
            if (callType == 0)
                App.Companion.sendToast("이미지 등록에 실패했습니다.");
            else if(callType == 1)
                App.Companion.sendToast("비밀번호 변경에 실패하셨습니다.");
            else
                App.Companion.sendToast("닉네임 변경에 실패하셨습니다.");

        }

    }

    @Override
    public void onFailure(Call call, Throwable t) {
        Log.e(TAG, "onFailure: ", t);

        if (callType == 0)
            App.Companion.sendToast("이미지 등록에 실패했습니다.");
        else if(callType == 1)
            App.Companion.sendToast("비밀번호 변경에 실패하셨습니다.");
        else
            App.Companion.sendToast("닉네임 변경에 실패하셨습니다.");
    }
}
