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
import java.util.Arrays;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.network.ProfileAPI;
import jso.kpl.traveller.network.UserAPI;
import jso.kpl.traveller.network.WebService;
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

    public ArrayList<String> title = new ArrayList(Arrays.asList("프로필", "프로필 정보 변경", "계정 비밀번호 변경", "회원 탈퇴"));

    //프로필 API
    ProfileAPI profileAPI = WebService.INSTANCE.getClient().create(ProfileAPI.class);

    //유저 API
    UserAPI userAPI = WebService.INSTANCE.getClient().create(UserAPI.class);

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

    //회원 탈퇴 버튼
    private OnUserDeleteClickListener onUserDeleteClickListener;

    public interface OnUserDeleteClickListener {
        void onClick();
    }

    public void setUserDeleteClickListener(OnUserDeleteClickListener onUserDeleteClickListener) {
        this.onUserDeleteClickListener = onUserDeleteClickListener;
    }

    private mypageStartActivity mypageStartActivity;

    public interface mypageStartActivity {
        void goActivity();
        void goBack();
    }

    public void setMypageStartActivity(mypageStartActivity mypageStartActivity) {
        this.mypageStartActivity = mypageStartActivity;
    }

    //비밀번호 수정했을 때 가져오는 값
    public MutableLiveData<Integer> isRevise = new MutableLiveData<>();
    public MutableLiveData<Boolean> isPwd = new MutableLiveData<>();
    public MutableLiveData<Boolean> isNick = new MutableLiveData<>();

    public MutableLiveData<Integer> toolbarTitle = new MutableLiveData<>();
    public MutableLiveData<Boolean> isInputPwd = new MutableLiveData<>();

    public ArrayList<String> updatePwdError = new ArrayList<>(Arrays.asList("비밀번호를 입력하지 않았습니다.\n비밀번호를 입력해주세요.", "입력하신 비밀번호가\n비밀번호 확인과 일치하지 않습니다.", "입력하신 비밀번호가\n조건에 맞지 않습니다.", "입력하신 비밀번호가\n기존 비밀번호와 동일합니다."));
    public ArrayList<String> updateNickNameError = new ArrayList<>(Arrays.asList("닉네임을 입력하지 않았습니다.\n닉네임을 입력해주세요.", "입력하신 닉네임이\n조건에 맞지 않습니다.", "입력하신 닉네임이\n기존 닉네임과 동일합니다."));

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

        toolbarTitle.setValue(0);
        isInputPwd.setValue(false);

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
        } else if (type == 1) {
            if (RegexMethod.isPasswordValid(updatePwd.getValue())) {
                profileAPI.updateUserPwd(App.Companion.getUser().getU_userid(),
                        //JavaUtil.returnSHA256(currentPwd.getValue()),
                        JavaUtil.returnSHA256(updatePwd.getValue())).enqueue(this);
            }
        } else {
            if (RegexMethod.isNickValid(updateNick.getValue())) {
                profileAPI.updateUserInfo(App.Companion.getUser().getU_userid(), updateNick.getValue()).enqueue(this);
            } else {
                wrongUpdateNick.setValue("* 자음 + 모음 한글, 영어, 숫자 조합");
            }
        }
    }

    /*public void onReviseClicked(View view) {

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

    }*/

    public void onUpdatePwdClicked(String newPwd) {
        Log.d(TAG, "비밀번호 변경");
        updatePwd.setValue(newPwd);
        profileAPI.updateUserPwd(App.Companion.getUser().getU_userid(), newPwd).enqueue(this);
        callType = 1;
    }

    public void onUpdateUserInfo(String newNickName) {
        Log.d(TAG, "유저 인포 변경");
        updateNick.setValue(newNickName);
        profileAPI.updateUserInfo(App.Companion.getUser().getU_userid(), newNickName).enqueue(this);
        callType = 2;
    }

    public void userDelete() {
        userAPI.deleteUser(App.Companion.getUser().getU_userid()).enqueue(new Callback<ResponseResult<Integer>>() {
            @Override
            public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {
                if (response.body() != null) {
                    ResponseResult<Integer> result = response.body();

                    if (result.getRes_type() == 1) {
                        App.Companion.sendToast("회원 탈퇴되었습니다.");
                        App.Companion.setUser(new User());
                        if (onUserDeleteClickListener != null) {
                            onUserDeleteClickListener.onClick();
                        }
                    } else {
                        App.Companion.sendToast("회원 탈퇴에 실패했습니다.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                App.Companion.sendToast("서버 통신 에러, 잠시 후 다시 시도해주세요.");
                Log.e(TAG, "onFailure: ", t);
            }
        });
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
            if (callType == 0) {
                App.Companion.getUser().setU_profile_img((String) result.getRes_obj());
                userLD.setValue(App.Companion.getUser());
            } else if (callType == 1) {
                if (result.getRes_type() == 1) {
                    SharedPreferences sp = App.INSTANCE.getSharedPreferences("auto_login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("u_pwd", updatePwd.getValue());
                    editor.apply();
                    App.Companion.sendToast("성공적으로 비밀번호를 변경했습니다.");
                    mypageStartActivity.goActivity();
                } else {
                    App.Companion.sendToast("비밀번호 변경에 실패했습니다.");
                }
            } else if (callType == 2) {
                if (result.getRes_type() == 1) {
                    App.Companion.getUser().setU_nick_name(updateNick.getValue());
                    userLD.setValue(App.Companion.getUser());
                    App.Companion.sendToast("성공적으로 유저 정보를 변경했습니다.");
                    mypageStartActivity.goBack();
                } else {
                    App.Companion.sendToast("유저 정보 변경에 실패했습니다.");
                }
            }
        }

        /*if (response.body() != null) {

            ResponseResult<Object> result = (ResponseResult<Object>) response.body();

            if (result.getRes_type() == 1) {

                if (callType == 0) {
                    App.Companion.getUser().setU_profile_img((String) result.getRes_obj());
                    userLD.setValue(App.Companion.getUser());
                } else if (callType == 1) {
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
                else if (callType == 1) {
                    App.Companion.sendToast("비밀번호 변경에 실패하셨습니다.");
                    isRevise.setValue((Integer) result.getRes_obj());
                } else {
                    App.Companion.sendToast("닉네임 변경에 실패하셨습니다.");
                }

            }

        } else {
            if (callType == 0)
                App.Companion.sendToast("이미지 등록에 실패했습니다.");
            else if (callType == 1)
                App.Companion.sendToast("비밀번호 변경에 실패하셨습니다.");
            else
                App.Companion.sendToast("닉네임 변경에 실패하셨습니다.");

        }*/

    }

    @Override
    public void onFailure(Call call, Throwable t) {
        Log.e(TAG, "onFailure: ", t);

        if (callType == 0)
            App.Companion.sendToast("이미지 등록에 실패했습니다.");
        else if (callType == 1)
            App.Companion.sendToast("비밀번호 변경에 실패하셨습니다.");
        else
            App.Companion.sendToast("닉네임 변경에 실패하셨습니다.");
    }
}
