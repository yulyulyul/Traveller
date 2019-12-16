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

import jso.kpl.traveller.App;
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

    //비밀번호 수정 버튼, 프로필 이미지 수정 버튼
    public View.OnClickListener onReviseClickListener;
    public View.OnClickListener onImgClickListener;

    //비밀번호 수정했을 때 가져오는 값
    public MutableLiveData<Integer> isRevise = new MutableLiveData<>();

    //0:프로필 이미지 수정 통신, 1:비밀번호 수정 통신
    int callType;

    public ProfileManagementViewModel() {
        userLD.setValue(App.Companion.getUser());
    }

    public void updateCall(final int type) {

        callType = type;

        if (type == 0) {
            File imgFile = new File(Uri.parse(userLD.getValue().getU_profile_img()).getPath());
            //******multipart/form-data는 파일 형식의 파싱법
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
            imgBody = MultipartBody.Part.createFormData("u_profile_img", imgFile.getName(), requestFile);

            profileAPI.updateProfileImg(userLD.getValue().getU_userid(), imgBody).enqueue(this);
        } else{
            if(!RegexMethod.isPasswordValid(currentPwd.getValue())){
                App.Companion.sendToast("현재 비밀번호 형식이 틀립니다.");
            } else if(!RegexMethod.isPasswordValid(updatePwd.getValue())){
                App.Companion.sendToast("수정 패스워드 형식이 틀립니다.");
            } else{
                profileAPI.updatePwd(App.Companion.getUser().getU_userid(),
                        JavaUtil.returnSHA256(currentPwd.getValue()),
                        JavaUtil.returnSHA256(updatePwd.getValue())).enqueue(this);
            }
        }
    }

    public View.OnClickListener onLoadRevisePwdClickListener;

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

    @Override
    public void onCleared() {
        super.onCleared();
    }

    @Override
    public void onResponse(Call call, Response response) {

        if (response.body() != null) {

            ResponseResult<Object> result = (ResponseResult<Object>) response.body();

            if (result.getRes_type() == 1) {

                if(callType == 0){
                    App.Companion.getUser().setU_profile_img((String) result.getRes_obj());
                    userLD.setValue(App.Companion.getUser());
                } else {
                    isRevise.setValue((Integer) result.getRes_obj());
                }

            } else if(result.getRes_type() == 0) {

                if(callType == 0)
                    App.Companion.sendToast("이미지 등록에 실패했습니다.");
                else
                    App.Companion.sendToast("비밀번호 변경에 실패하셨습니다.");
            }

        } else {
            if(callType == 0)
                App.Companion.sendToast("이미지 등록에 실패했습니다.");
            else
                App.Companion.sendToast("비밀번호 변경에 실패하셨습니다.");
        }

    }

    @Override
    public void onFailure(Call call, Throwable t) {
        Log.e(TAG, "onFailure: ", t);
    }
}
