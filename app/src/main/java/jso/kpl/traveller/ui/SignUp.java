package jso.kpl.traveller.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.kakao.usermgmt.response.model.User;

import java.io.File;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.ActivitySignUpBinding;
import jso.kpl.traveller.model.LoginUser;
import jso.kpl.traveller.util.JavaUtil;
import jso.kpl.traveller.viewmodel.SignUpViewModel;

public class SignUp extends AppCompatActivity {

    String TAG = "TAG.View.";

    LoginUser lUser;

    Uri uriPath;
    String absolutePath;

    SignUpViewModel svm;

    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivitySignUpBinding signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        signUpBinding.setSignUpVM(new SignUpViewModel(this));
        signUpBinding.setLifecycleOwner(this);
        signUpBinding.executePendingBindings();

        svm = signUpBinding.getSignUpVM();

        if(getIntent() != null){

            svm.isAuth.setValue((boolean) getIntent().getBooleanExtra("auth", false));

            if(svm.isAuth.getValue()){
                email = (String) getIntent().getStringExtra("email");
                Log.d(TAG + "API 접근", "아이디: " + email);
                svm.emailLD.setValue(email);
            }
        } else{
            svm.isAuth.setValue(false);
        }

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
                                    (getApplicationContext(), getPackageName() + ".file_provider", pFile);
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
