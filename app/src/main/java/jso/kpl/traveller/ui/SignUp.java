package jso.kpl.traveller.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;

import java.io.File;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.SignUpBinding;
import jso.kpl.traveller.util.JavaUtil;
import jso.kpl.traveller.util.RegexMethod;
import jso.kpl.traveller.viewmodel.SignUpViewModel;

public class SignUp extends AppCompatActivity {

    String TAG = "TAG.View.";

    SignUpBinding signUpBinding;

    Uri uriPath;
    String absolutePath;

    SignUpViewModel svm;

    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         signUpBinding = DataBindingUtil.setContentView(this, R.layout.sign_up);

        signUpBinding.setSignUpVM(new SignUpViewModel(this));
        signUpBinding.setLifecycleOwner(this);
        signUpBinding.executePendingBindings();

        svm = signUpBinding.getSignUpVM();

        if(getIntent() != null){
            svm.isAuth.setValue(getIntent().getBooleanExtra("auth", false));

            if(svm.isAuth.getValue()){
                email = (String) getIntent().getStringExtra("email");
                Log.d(TAG + "API 접근", "아이디: " + email);
                svm.emailLD.setValue(email);
            }
        } else{
            svm.isAuth.setValue(false);
        }

        checkRegex();

        svm.isAuth.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    signUpBinding.emailAuthBtn.setTextColor(Color.parseColor("#80808080"));
                    signUpBinding.emailAuthTv.setTextColor(Color.parseColor("#80808080"));
                } else{
                    signUpBinding.emailAuthBtn.setTextColor(Color.parseColor("#ffffff"));
                    signUpBinding.emailAuthTv.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        signUpBinding.getSignUpVM().buttonClickResult.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer == 0) {
                    Intent albumIntent = new Intent(Intent.ACTION_PICK);
                    albumIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(albumIntent, integer);
                } else if (integer == 1) {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File pFile = createDir();

                    if (pFile != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            Uri providerPath = FileProvider.getUriForFile
                                    (getApplicationContext(), App.INSTANCE.getPackageName() + ".file_provider", pFile);
                            uriPath = providerPath;
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
                            startActivityForResult(cameraIntent, 1);
                        } else {
                            uriPath = Uri.fromFile(pFile);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
                            startActivityForResult(cameraIntent, 1);
                        }
                    }
                }

            }
        });
    }

    public void checkRegex(){
        signUpBinding.emailAuthTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(signUpBinding.getSignUpVM().emailLD.getValue() == null || !RegexMethod.isEmailValid(signUpBinding.getSignUpVM().emailLD.getValue())){
                        Toast.makeText(App.INSTANCE, "이메일 형식에 맞추어 작성해주세요.\n(example@naver.com)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        signUpBinding.emailEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(TextUtils.isEmpty(signUpBinding.getSignUpVM().nickNameLD.getValue()) || !RegexMethod.isNickValid(signUpBinding.getSignUpVM().nickNameLD.getValue())){
                        Toast.makeText(App.INSTANCE, "닉네임 형식에 맞추어 작성해주세요.\n(자음 + 모음 한글, 영어, 숫자 조합)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        signUpBinding.passwordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(TextUtils.isEmpty(signUpBinding.getSignUpVM().passwordLD.getValue()) || !RegexMethod.isPasswordValid(signUpBinding.getSignUpVM().passwordLD.getValue())){
                        Toast.makeText(App.INSTANCE, "패스워드 형식에 맞추어 작성해주세요.\n(영소문 숫자 조합 8자 이상 특수문자는 !@#$%^*만 사용 가능)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // 디렉토리 생성
    public File createDir() {
        //저장될 파일의 이름
        String imgName = "t_" + System.currentTimeMillis() + ".jpg";

        //저장 디렉토리 주소
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Traveller/");

        //디렉토리가 없을 시 생성
        if (!storageDir.exists())
            storageDir.mkdirs();

        //저장될 파일의 주소
        File imgFilePath = new File(storageDir, imgName);

        //경로를 따로 또 받는 이유는 갤러리 상에서의 갱신을 위해서이다.
        absolutePath = imgFilePath.getAbsolutePath();

        return imgFilePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                try {
                    Uri tmpUri = data.getData();

                    svm.photoUpdate.setValue(JavaUtil.getImageFullPath(this, tmpUri).toString());
                    Log.d(TAG +"앨범", "앨범이미지 주소: " + svm.photoUpdate.getValue());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    File saveFile = new File(absolutePath);
                    uriPath = Uri.fromFile(saveFile);

                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(uriPath));

                    svm.photoUpdate.setValue(uriPath.toString());

                    Log.d(TAG +"카메라", "카메라 이미지 주소: " + svm.photoUpdate.getValue());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                //이메일 인증 이후 처리 인증 or 미인증
                break;
        }
    }


}
