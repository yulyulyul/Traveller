package jso.kpl.traveller.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.interfaces.DialogYNInterface;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.network.UserAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.CustomDialog;
import jso.kpl.traveller.ui.Login;
import jso.kpl.traveller.util.JavaUtil;
import jso.kpl.traveller.util.PermissionCheck;
import jso.kpl.traveller.util.RegexMethod;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpViewModel extends BaseObservable implements Callback {

    Context context;

    public MutableLiveData<String> emailLD = new MutableLiveData<>();
    //이메일 인증을 하면 true 값으로 반환, 지금은 이메일 인증을 연결 안해두어서 true 값
    public MutableLiveData<Boolean> isAuth = new MutableLiveData<>();

    public MutableLiveData<String> passwordLD = new MutableLiveData<>();
    public MutableLiveData<String> nickNameLD = new MutableLiveData<>();

    public MutableLiveData<Integer> buttonClickResult = new MutableLiveData<>();

    public MutableLiveData<String> photoUpdate = new MutableLiveData<>();

    //정규식 함수
    RegexMethod regexMethod = new RegexMethod();

    //레트로핏
    UserAPI userAPI= WebService.INSTANCE.getClient().create(UserAPI.class);

    //회원가입 Call
    Call<ResponseResult<User>> suCall;
    Call<ResponseResult<Integer>> authCall;

    //프로필 이미지 업로드RequestBody
    MultipartBody.Part imgBody;

    //회원 데이터 객체
    User user;


    String TAG = "TAG.SignUp.";

    public SignUpViewModel(Context context) {
        this.context = context;

        photoUpdate.setValue("i_blank_person_icon");
    }

    //디바이스 초기 값, 속성: 유니크, 디바이스 초기화할 때 바뀐다고 함
    private String getDeviceID() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    //토스트 메시지 메서드화
    private void sendToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //이메일 인증 버튼
    public void onEmailAuthClicked() {

        if (TextUtils.isEmpty(emailLD.getValue())) {
            sendToast(context, "이메일 칸이 비었습니다.");
        } else if (!regexMethod.isEmailValid(emailLD.getValue())) {
            sendToast(context, "이메일 형식이 틀렸습니다.\n형식에 맞추어 작성부탁드립니다.");
        } else {

            Bundle bundle = new Bundle();
            bundle.putString("title", "이메일 인증하시겠습니까?");

            final CustomDialog customDialog = new CustomDialog();

            customDialog.setArguments(bundle);

            customDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Dialog_TAG");

            customDialog.setDialogYNInterface(new DialogYNInterface() {
                @Override
                public void positiveBtn() {

                    authCall = userAPI.authEmail(emailLD.getValue());

                    authCall.enqueue(new Callback<ResponseResult<Integer>>() {
                        @Override
                        public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                            ResponseResult<Integer> responseResult = response.body();

                            if(responseResult.getRes_obj() == 0){
                                sendToast(context, "중복된 이메일이 있습니다.");
                            }else if(responseResult.getRes_obj() == 1){
                                sendToast(context, "이메일 인증!!!");
                                isAuth.setValue(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                            t.printStackTrace();
                            sendToast(App.INSTANCE, "서버 에러");
                            isAuth.setValue(false);
                        }
                    });

                    customDialog.dismiss();
                }

                @Override
                public void negativeBtn() {
                    sendToast(context, "이메일 실패!!!");
                    isAuth.setValue(false);
                    customDialog.dismiss();
                }
            });


            //       buttonClickResult.setValue(2);

            //(예정) 여기서 이메일 인증 처리
        }
    }

    //회원 가입 버튼
    public void onSignUpClicked() {

        //각각의 입력 값을 정규식이 맞는지 null이 아닌지 체크
        if (TextUtils.isEmpty(nickNameLD.getValue())) {
            sendToast(context, "닉네임 칸이 비었습니다.");
        } else if (!regexMethod.isNickValid(nickNameLD.getValue())) {
            sendToast(context, "닉네임 형식이 틀렸습니다.\n닉네임 형식에 맞추어 작성해주십시오.");
        } else if (TextUtils.isEmpty(emailLD.getValue())) {
            sendToast(context, "이메일 칸이 비었습니다.");
        } else if (!regexMethod.isEmailValid(emailLD.getValue())) {
            sendToast(context, "이메일 형식이 틀렸습니다.\n형식에 맞추어 작성부탁드립니다.");
        } else if (TextUtils.isEmpty(passwordLD.getValue())) {
            sendToast(context, "패스워드 칸이 비었습니다.");
        } else if (!regexMethod.isPasswordValid(passwordLD.getValue())) {
            sendToast(context, "패스워드 형식이 틀렸습니다.\n형식에 맞추어 작성부탁드립니다.");
        } else if (!isAuth.getValue()) {
            sendToast(context, "이메일 인증이 미실시되었습니다.");
        } else {

            //이메일, 닉네임, 패스워드를 담은 객체
            user = new User(emailLD.getValue(), JavaUtil.returnSHA256(passwordLD.getValue()), nickNameLD.getValue(), getDeviceID());

            //프로필 이미지가 null이거나 초기값 사진 또는 에러 이미지일 때
            //if (photoUpdate.getValue() == null || photoUpdate.getValue().equals("drawable://" + R.drawable.i_blank_person_icon)) {
            if (photoUpdate.getValue() == null || photoUpdate.getValue().equals("android.resource://jso.kpl.traveller/drawable/i_blank_person_icon")) {

                Log.d(TAG + "이미지", "존재하지 않음");
                imgBody = null;

            } else {
                //프로필 이미지가 null이거나 초기값 사진 또는 에러 이미지일 때가 아닐때

                File imgFile = new File(Uri.parse(photoUpdate.getValue()).getPath());

                Log.d(TAG + "이미지", "존재함: " + Uri.parse(photoUpdate.getValue()).getPath());

                //취사선택

                //******multipart/form-data는 파일 형식의 파싱법
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
                imgBody = MultipartBody.Part.createFormData("u_profile_img", imgFile.getName(), requestFile);
                System.out.println("이미지 파일명 :  " + imgFile.getName());

                //*****이미지 형식의 파싱법(image/*은 모든 이미지 형식이라는 뜻)
                //RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imgFile);
                //imgBody = MultipartBody.Part.createFormData("us_profile_img", imgFile.getName(), requestFile);
            }

            /*
            SignupAPI.java에서 번호와 밑의 번호에 맞춰서 하면 됨
            1.은 객체와 이미지
            2.은 각 변수와 이미지
             */

            //1. 객체 + 이미지
            // suCall = con.signupAPI.goSignUp(user, imgBody);

            //2. 각 변수(이메일, 패스워드(해시) + 닉네임 + 디바이스 번호) + 이미지
            // String으로 데이터 보내면 DB에 ""가 포함되어 들어감


            suCall = userAPI.goSignUp(user, imgBody);

            suCall.enqueue(this);
        }
    }

    //프로필 이미지
    public void onImageClicked() {

        PermissionCheck permissionCheck = new PermissionCheck(context, new Activity());

        permissionCheck.setPermissionListener_camera(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                final AlertDialog.Builder photoAlert = new AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);

                photoAlert.setTitle("이미지 등록")
                        .setMessage("앨범/카메라 선택")
                        .setNegativeButton("앨범", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG + "앨범/카메라", "앨범 선택");
                                buttonClickResult.setValue(0);
                            }
                        })
                        .setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG + "앨범/카메라", "카메라 선택");
                                buttonClickResult.setValue(1);
                            }
                        })
                        .setNeutralButton("취소", null).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        });

    }

    @Override
    public void onResponse(Call call, Response response) {
        Log.d(TAG + "통신 성공","성공적으로 전송");

        ResponseResult<User> reUser = ((ResponseResult<User>) response.body());

        if(reUser.getRes_type() == -1){

        }else if(reUser.getRes_type() == 0){

        }else{
            User user = ((ResponseResult<User>) response.body()).getRes_obj();

            System.out.println(user.toString());
            if(user.getU_email() != null) {
                Log.d(TAG + "회원가입","성공");
                sendToast(context, "회원가입에 성공하였습니다.");
                Intent intent = new Intent(context, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ContextCompat.startActivity(context, intent, null);
            } else {
                Log.d(TAG + "회원가입","실패");
                sendToast(context, "이미 존재하는 이메일입니다.");
            }
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        sendToast(context, "통신 불량" + t.getMessage());
        Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
        t.printStackTrace();
    }
}