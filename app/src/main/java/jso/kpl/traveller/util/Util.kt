package mvvm.f4wzy.com.samplelogin.util

import android.util.Log
import java.util.regex.Pattern

class Util {

    companion object {

        /*
            Email 형식이 맞는지 검사
         */
        fun isEmailValid(email: String): Boolean {
            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }
    }


}