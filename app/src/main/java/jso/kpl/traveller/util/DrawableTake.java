package jso.kpl.traveller.util;

import android.content.Context;

public class DrawableTake {
    public static int getImage(Context context, String imageName) {
        // drawable 이미지 리소스 ID 찾기
        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        return drawableResourceId;
    }
}
