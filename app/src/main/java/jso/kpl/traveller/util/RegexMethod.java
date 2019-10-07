package jso.kpl.traveller.util;

import android.util.Patterns;

import java.util.regex.Pattern;

public class RegexMethod {

    //이메일 정규식 체크
    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //영소문 숫자 조합 8자 이상 특수문자는 !@#$%^*만 사용 가능
    public boolean isPasswordValid(String pwd) {

        return Pattern.matches("^(?=.*\\d)(?=.*[a-z])[a-z\\d!@#$%^&*]{8,}$", pwd);
    }

    //자음 + 모음 한글, 영어, 숫자 조합
    public boolean isNickValid(String nickname) {
        return Pattern.matches("^[\\w가-힣]{2,20}$", nickname);
    }

}