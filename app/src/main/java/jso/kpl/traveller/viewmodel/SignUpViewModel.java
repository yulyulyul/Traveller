package jso.kpl.traveller.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import jso.kpl.traveller.R;
import jso.kpl.traveller.model.UserSignup;
import jso.kpl.traveller.network.SignupAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.util.PermissionCheck;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpViewModel extends BaseObservable {

    Context context;

    public MutableLiveData<String> emailLD = new MutableLiveData<>();
    public MutableLiveData<String> passwordLD = new MutableLiveData<>();
    public MutableLiveData<String> nickNameLD = new MutableLiveData<>();

    //(예정) 이메일 인증 처리하면 미인증 -> 인증
    public MutableLiveData<String> emailAuth = new MutableLiveData<>();

    public MutableLiveData<Integer> buttonClickResult = new MutableLiveData<>();

    public MutableLiveData<String> photoUpdate = new MutableLiveData<>();

    //레트로핏
     SignupAPI signupAPI = WebService.INSTANCE.getClient().create(SignupAPI.class);

    //회원가입 Call
    Call<Void> su_call;

    //프로필 이미지 업로드RequestBody
    MultipartBody.Part imgBody;

    //회원 데이터 객체
    UserSignup userSignup;

    //이메일 인증을 하면 true 값으로 반환, 지금은 이메일 인증을 연결 안해두어서 true 값
    public boolean isAuth = true;

    String TAG = "TAG.SignUp.";

    public SignUpViewModel(Context context) {
        this.context = context;
        emailAuth.setValue("인증");

        String imageUri = "drawable://" + R.drawable.p_blank_person;
        photoUpdate.setValue(imageUri);

        nickNameLD.setValue("닉네임");
        emailLD.setValue("example@naver.com");
        passwordLD.setValue("1q2w3e4r!");
    }

    //디바이스 초기 값, 속성: 유니크, 디바이스 초기화할 때 바뀐다고 함
    private String getDeviceID() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    //토스트 메시지 메서드화
    private void sendToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    //이메일 정규식 체크
    private boolean isEmailValid() {
        return Patterns.EMAIL_ADDRESS.matcher(emailLD.getValue()).matches();
    }

    //영소문 숫자 조합 8자 이상 특수문자는 !@#$%^*만 사용 가능
    private boolean isPasswordValid() {

        return Pattern.matches("^(?=.*\\d)(?=.*[a-z])[a-z\\d!@#$%^&*]{8,}$", passwordLD.getValue());
    }

    //자음 + 모음 한글, 영어, 숫자 조합
    private boolean isNickValid() {
        return Pattern.matches("^[\\w가-힣]{2,20}$", nickNameLD.getValue());
    }

    //이메일 인증 버튼
    public void onEmailAuthClicked() {
        if (TextUtils.isEmpty(emailLD.getValue())) {
            sendToast(context, "이메일 칸이 비었습니다.");
        } else if (!isEmailValid()) {
            sendToast(context, "이메일 형식이 틀렸습니다.\n형식에 맞추어 작성부탁드립니다.");
        } else {
            sendToast(context, "System::Email Clear");

            //       buttonClickResult.setValue(2);

            //(예정) 여기서 이메일 인증 처리
        }
    }

    //회원 가입 버튼
    public void onSignUpClicked() {

        //각각의 입력 값을 정규식이 맞는지 null이 아닌지 체크
        if (TextUtils.isEmpty(nickNameLD.getValue())) {
            sendToast(context, "닉네임 칸이 비었습니다.");
        } else if (!isNickValid()) {
            nickNameLD.setValue("");
            sendToast(context, "닉네임 형식이 틀렸습니다.\n닉네임 형식에 맞추어 작성해주십시오.");
        } else if (TextUtils.isEmpty(emailLD.getValue())) {
            sendToast(context, "이메일 칸이 비었습니다.");
        } else if (!isEmailValid()) {
            emailLD.setValue("");
            sendToast(context, "이메일 형식이 틀렸습니다.\n형식에 맞추어 작성부탁드립니다.");
        } else if (TextUtils.isEmpty(passwordLD.getValue())) {
            sendToast(context, "패스워드 칸이 비었습니다.");
        } else if (!isPasswordValid()) {
            passwordLD.setValue("");
            sendToast(context, "패스워드 형식이 틀렸습니다.\n형식에 맞추어 작성부탁드립니다.");
        } else if (!isAuth) {
            sendToast(context, "이메일 인증이 미실시되었습니다.");
        } else {
            //모든 조건에 만족할 시 실행
            sendToast(context, "System::All Clear");

            //이메일, 닉네임, 패스워드를 담은 객체
            userSignup = new UserSignup(emailLD.getValue(), returnSHA256(passwordLD.getValue()), nickNameLD.getValue(), getDeviceID());

            //프로필 이미지가 null이거나 초기값 사진 또는 에러 이미지일 때
            if (photoUpdate.getValue() == null || photoUpdate.getValue().equals("drawable://" + R.drawable.p_blank_person)) {

                Log.d(TAG + "이미지", "존재하지 않음");
                imgBody = null;

            } else {
                //프로필 이미지가 null이거나 초기값 사진 또는 에러 이미지일 때가 아닐때

                File imgFile = new File((Uri.parse(photoUpdate.getValue().toString())).getPath());


                Log.d(TAG + "이미지", "존재함: " + imgFile.getAbsolutePath());


                //취사선택

                //******multipart/form-data는 파일 형식의 파싱법
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
                imgBody = MultipartBody.Part.createFormData("us_profile_img", imgFile.getName(), requestFile);

                //*****이미지 형식의 파싱법(image/*은 모든 이미지 형식이라는 뜻)
//              RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imgFile);
//              imgBody = MultipartBody.Part.createFormData("us_profile_img", imgFile.getName(), requestFile);
            }



            /*
            SignupAPI.java에서 번호와 밑의 번호에 맞춰서 하면 됨
            1.은 객체와 이미지
            2.은 각 변수와 이미지
             */

            //1. 객체 + 이미지
//          su_call = con.signupAPI.goSignUp(userSignup, imgBody);

            //2. 각 변수(이메일, 패스워드(해시) + 닉네임 + 디바이스 번호) + 이미지
            su_call = signupAPI.goSignUp(userSignup.getUs_email(),
                    userSignup.getUs_pwd(), userSignup.getUs_nick_name(),
                    userSignup.getUs_device(), imgBody);

            su_call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d(TAG + "통신 성공", "성공적으로 전송");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    sendToast(context, "통신 불량");
                    Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
                    t.printStackTrace();
                }
            });
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
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        });

    }

    //저장할 때 패스워드를 sha256으로 해시화 해서 보낸다.
    public String returnSHA256(String str) {

        String SHA = "";

        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");

            sh.update(str.getBytes());

            byte byteData[] = sh.digest();

            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            SHA = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            SHA = null;
        }

        return SHA;
    }




}
