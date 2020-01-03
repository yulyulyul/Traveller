package jso.kpl.traveller.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.ProfileManagementBinding;
import jso.kpl.traveller.model.LoginUser;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import jso.kpl.traveller.network.UserAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.Fragment.RevisePwd;
import jso.kpl.traveller.util.JavaUtil;
import jso.kpl.traveller.util.PermissionCheck;
import jso.kpl.traveller.util.RegexMethod;
import jso.kpl.traveller.viewmodel.ProfileManagementViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileManagement extends AppCompatActivity {

    String TAG = "Trav.ProfileM.";

    ProfileManagementBinding binding;
    ProfileManagementViewModel pmVm;

    Uri uriPath;
    String absolutePath;

    RevisePwd revisePwd;

    UserAPI userAPI = WebService.INSTANCE.getClient().create(UserAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pmVm = new ProfileManagementViewModel();

        binding = DataBindingUtil.setContentView(this, R.layout.profile_management);
        binding.setPmVm(pmVm);
        binding.setLifecycleOwner(this);

        binding.getPmVm().onImgClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JavaUtil.downKeyboard(ProfileManagement.this);

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

        binding.getPmVm().onUpdateNickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.getPmVm().updateCall(2);
            }
        };

        binding.topActionBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.getPmVm().userInfoCall();

        binding.getPmVm().isRevise.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 1) {

                    SharedPreferences sp = App.INSTANCE.getSharedPreferences("auto_login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString("u_pwd", JavaUtil.returnSHA256(binding.getPmVm().updatePwd.getValue()));

                    editor.commit();

                    App.Companion.sendToast("성공적으로 비밀번호를 수정하셨습니다.");

                    getCurrentFocus().clearFocus();

                    JavaUtil.downKeyboard(ProfileManagement.this);

                } else {
                    binding.getPmVm().wrongCurrentPwd.setValue("현재 비밀번호가 틀리셨습니다.");
                }
            }
        });

        binding.profileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.getPmVm().toolbarTitle.setValue(1);
            }
        });

        binding.pwdUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.getPmVm().isInputPwd.setValue(true);
                binding.getPmVm().toolbarTitle.setValue(2);
            }
        });

        binding.userDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.getPmVm().isInputPwd.setValue(true);
                binding.getPmVm().toolbarTitle.setValue(3);
            }
        });

        binding.getPmVm().setUserDeleteClickListener(new ProfileManagementViewModel.OnUserDeleteClickListener() {
            @Override
            public void onClick() {
                SharedPreferences sp = App.INSTANCE.getSharedPreferences("auto_login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.remove("auto_login");
                editor.clear();
                editor.apply();

                Intent intent = new Intent();
                intent.putExtra("result", 1);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        binding.pwdCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPwd = JavaUtil.returnSHA256(binding.inputPwd.getText().toString());
                userAPI.goLogin(new LoginUser(App.Companion.getUser().getU_email(), inputPwd)).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        ResponseResult<User> user = ((ResponseResult<User>) response.body());

                        if (user.getRes_type() == 1) {
                            switch (binding.getPmVm().toolbarTitle.getValue()) {
                                case 2:
                                    binding.getPmVm().isInputPwd.setValue(false);
                                    binding.getPmVm().currentPwd.setValue(binding.inputPwd.getText().toString());
                                    binding.inputPwdTxt.setText("회원님의 정보 보호를 위해\n현재 비밀번호를 입력하세요.");
                                    binding.inputPwdTxt.setTextColor(Color.BLACK);
                                    binding.inputPwd.setText("");
                                    JavaUtil.downKeyboard(ProfileManagement.this);
                                    break;
                                case 3:
                                    binding.getPmVm().userDelete();
                                    break;
                            }
                        } else {
                            binding.inputPwdTxt.setText("비밀번호가 일치하지 않습니다.\n비밀번호를 다시 입력해주세요.");
                            binding.inputPwdTxt.setTextColor(Color.RED);
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
                        t.printStackTrace();
                    }
                });
            }
        });

        binding.inputPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.inputPwdTxt.getCurrentTextColor() == Color.RED) {
                    binding.inputPwdTxt.setText("회원님의 정보 보호를 위해\n현재 비밀번호를 입력하세요.");
                    binding.inputPwdTxt.setTextColor(Color.BLACK);
                }
            }
        });

        binding.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JavaUtil.downKeyboard(ProfileManagement.this);
            }
        });

        binding.userPwdUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPwd = binding.inputNewPwd.getText().toString().trim();
                String currentPwd = binding.getPmVm().currentPwd.getValue();
                if (!newPwd.equals("")) { // 공백인지 확인
                    if (newPwd.equals(binding.inputNewPwdCheck.getText().toString().trim())) { // 비밀번호 확인과 일치하는지 확인
                        if (RegexMethod.isPasswordValid(newPwd)) { // 비밀번호 조건에 맞는지 확인
                            String SHA256ToNewPwd = JavaUtil.returnSHA256(newPwd);
                            if (!SHA256ToNewPwd.equals(JavaUtil.returnSHA256(currentPwd))) { // 현재 비밀번호랑 새 비밀번호가 일치하는지 확인
                                binding.getPmVm().onUpdatePwdClicked(SHA256ToNewPwd);
                            } else {
                                binding.pwdCheckError.setText(binding.getPmVm().updatePwdError.get(3));
                                binding.pwdCheckError.setVisibility(View.VISIBLE);
                            }
                        } else {
                            binding.pwdCheckError.setText(binding.getPmVm().updatePwdError.get(2));
                            binding.pwdCheckError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.pwdCheckError.setText(binding.getPmVm().updatePwdError.get(1));
                        binding.pwdCheckError.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.pwdCheckError.setText(binding.getPmVm().updatePwdError.get(0));
                    binding.pwdCheckError.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.userInfoUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newNickName = binding.inputNick.getText().toString().trim();
                String currentNickName = binding.nick.getText().toString();
                if (!newNickName.equals("")) {
                    if (RegexMethod.isNickValid(newNickName)) {
                        if (!newNickName.equals(currentNickName)) {
                            binding.getPmVm().onUpdateUserInfo(newNickName);
                        } else {
                            binding.nickNameCheckError.setText(binding.getPmVm().updateNickNameError.get(2));
                            binding.nickNameCheckError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.nickNameCheckError.setText(binding.getPmVm().updateNickNameError.get(1));
                        binding.nickNameCheckError.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.nickNameCheckError.setText(binding.getPmVm().updateNickNameError.get(0));
                    binding.nickNameCheckError.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.getPmVm().setMypageStartActivity(
                new ProfileManagementViewModel.mypageStartActivity() {
                    @Override
                    public void goActivity() {
                        Intent intent = new Intent();
                        intent.putExtra("result", 2);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void goBack() {
                        onBackPressed();
                    }
                }
        );
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

    public void removeFragment(Fragment fragment) {
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
        if (binding.getPmVm().toolbarTitle.getValue() != 0) {
            switch (binding.getPmVm().toolbarTitle.getValue()) {
                case 1:
                    binding.inputNick.setText("");
                    binding.nickNameCheckError.setVisibility(View.GONE);
                    break;
                case 2:
                    binding.inputNewPwd.setText("");
                    binding.inputNewPwdCheck.setText("");
                    binding.pwdCheckError.setVisibility(View.GONE);
                    break;
            }
            binding.getPmVm().toolbarTitle.setValue(0);
            JavaUtil.downKeyboard(ProfileManagement.this);
            if (binding.getPmVm().isInputPwd.getValue()) {
                binding.getPmVm().isInputPwd.setValue(false);
                binding.inputPwd.setText("");
                if (binding.inputPwdTxt.getCurrentTextColor() == Color.RED) {
                    binding.inputPwdTxt.setText("회원님의 정보 보호를 위해\n현재 비밀번호를 입력하세요.");
                    binding.inputPwdTxt.setTextColor(Color.BLACK);
                }
            }
        } else {
            setResult(RESULT_OK);
            binding.getPmVm().onCleared();
            finish();
        }
        /*if (revisePwd != null) {
            getSupportFragmentManager().beginTransaction()
                    .detach(revisePwd)
                    .remove(revisePwd)
                    .commit();

            revisePwd = null;
        } else {
            setResult(RESULT_OK);
            binding.getPmVm().onCleared();
            finish();
        }*/
    }
}
