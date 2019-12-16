package jso.kpl.traveller.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.ProfileManagementBinding;
import jso.kpl.traveller.ui.Fragment.RevisePwd;
import jso.kpl.traveller.util.JavaUtil;
import jso.kpl.traveller.util.PermissionCheck;
import jso.kpl.traveller.viewmodel.ProfileManagementViewModel;

public class ProfileManagement extends AppCompatActivity {

    String TAG = "Trav.ProfileM.";

    ProfileManagementBinding binding;
    ProfileManagementViewModel pmVm;

    Uri uriPath;
    String absolutePath;

    RevisePwd revisePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pmVm = new ProfileManagementViewModel();

        binding = DataBindingUtil.setContentView(this, R.layout.profile_management);
        binding.setPmVm(pmVm);
        binding.setLifecycleOwner(this);

        binding.getPmVm().onReviseClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();

            }
        };

        binding.getPmVm().onImgClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionCheck permissionCheck = new PermissionCheck(ProfileManagement.this, new Activity());

                permissionCheck.setPermissionListener_camera(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        final AlertDialog.Builder photoAlert = new AlertDialog.Builder(ProfileManagement.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);

                        photoAlert.setTitle("이미지 등록")
                                .setMessage("앨범/카메라 선택")
                                .setNegativeButton("앨범", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent albumIntent = new Intent(Intent.ACTION_PICK);
                                        albumIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                        startActivityForResult(albumIntent, 0);
                                    }
                                })
                                .setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG + "앨범/카메라", "카메라 선택");

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
                                })
                                .setNeutralButton("취소", null).show();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                });
            }
        };

        binding.getPmVm().onLoadRevisePwdClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(revisePwd == null)
                    revisePwd = new RevisePwd();

                getSupportFragmentManager().beginTransaction().add(binding.container.getId(), revisePwd, "revise_pwd").commit();
            }
        };

        binding.topActionBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
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

    public void removeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().detach(fragment).remove(fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                try {
                    Uri tmpUri = data.getData();

                    binding.getPmVm().userLD.getValue().setU_profile_img(JavaUtil.getImageFullPath(this, tmpUri).toString());

                    binding.getPmVm().updateCall(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    File saveFile = new File(absolutePath);
                    uriPath = Uri.fromFile(saveFile);

                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(uriPath));

                    binding.getPmVm().userLD.getValue().setU_profile_img(uriPath.toString());

                    binding.getPmVm().updateCall(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(revisePwd != null){
            getSupportFragmentManager().beginTransaction()
                    .detach(revisePwd)
                    .remove(revisePwd)
                    .commit();

            revisePwd = null;
        } else {
            setResult(RESULT_OK);
            binding.getPmVm().onCleared();
            finish();
        }
    }
}
