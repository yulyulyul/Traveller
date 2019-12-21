package jso.kpl.traveller.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jso.kpl.traveller.App;
import jso.kpl.traveller.interfaces.DialogYNInterface;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.ui.CustomDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JavaUtil {

    //현재 시간을 구하는 함수
    public static String currentTime() {
        long now = System.currentTimeMillis();

        Date date = new Date(now);

        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        return sdf.format(date);
    }

    //이미지 축약 주소를 실제 주소로 바꿔주는 함수
    public static Uri getImageFullPath(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        return Uri.fromFile(new File(picturePath));
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("TESTTAG", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("TESTTAG", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("TESTTAG", "printHashKey()", e);
        }
    }

    public static int dpToPx(float dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.INSTANCE.getResources().getDisplayMetrics());
        return px;
    }

    // Glide drawable 이미지 경로 가져오기
    public static int getImage(Context context, String imageName) {
        // drawable 이미지 리소스 ID 찾기
        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        return drawableResourceId;
    }

    public static void onTouchDownKey(View v, final View... views) {

        final InputMethodManager imm = (InputMethodManager) App.INSTANCE.getSystemService(Context.INPUT_METHOD_SERVICE);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (final View view : views) {

                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Log.d("Trav.util", "onClick");
                    Log.d("Trav.util", "아이디: " + view.getId());
                }
            }
        });
    }

    public static void downKeyboard(Activity activity) {

        InputMethodManager mInputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static String returnSHA256(String str) {

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

    public static Boolean checkBlankString(String str) {

        return (str != null && str.length() > 0);
    }

    public static String travelPeriod(String startDate, String endDate){

        String[] starts = startDate.split("-");
        String[] ends = endDate.split("-");

        String result = startDate + " ~ ";

        for(int i = 0; i <starts.length; i++){
            if(!starts[i].equals(ends[i])){

                if(i == 2)
                    result += ends[i];
                else
                    result += ends[i]+ "-";
            }
        }

        return result;
    }
}
