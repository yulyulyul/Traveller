package jso.kpl.traveller.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

public class PermissionCheck {

    Context context;
    Activity activity;

    public PermissionCheck(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setPermissionListener_camera(PermissionListener permissionListener){
        TedPermission.with(context)
                .setRationaleTitle("카메라 권한 승인")
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .setRationaleMessage("카메라 기능을 이용하실려면\n카메라 권한과 외부 저장소 권한을 승인해주시길 바랍니다.")
                .setDeniedTitle("카메라 권한 미승인")
                .setDeniedMessage("카메라 권한이 미승인되었습니다. \n수동: [설정] > [권한] > [카메라]")
                .check();
    }
}


