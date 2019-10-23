package jso.kpl.traveller.viewmodel

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import jso.kpl.traveller.model.LoginUser
import mvvm.f4wzy.com.samplelogin.util.SingleLiveEvent
import mvvm.f4wzy.com.samplelogin.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import com.facebook.CallbackManager
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.UnLinkResponseCallback
import jso.kpl.traveller.network.LoginAPI
import jso.kpl.traveller.network.WebService
import jso.kpl.traveller.thirdpartyapi.facebook.LoginCallback
import jso.kpl.traveller.thirdpartyapi.kakao.SessionCallback
import com.facebook.login.LoginManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat
import com.facebook.login.Login
import jso.kpl.traveller.ui.MyPage
import java.util.*

/**
 * login,xml에 대응하는 ViewModel
 */
class LoginViewModel(application: Application): AndroidViewModel(application) , Callback<LoginUser>
{
    //버튼 활성화(이메일 형식이 아닐시, 비밀번호가 8글자 이상이 아닐시 로그인 버튼을 비활성화 시켜줌.
    var btnSelected: ObservableBoolean? = null
    var email: ObservableField<String>? = null
    var password: ObservableField<String>? = null
    var muUser: MutableLiveData<LoginUser>? = null
    var tempUser: MutableLiveData<LoginUser>? = null
    var progressDialog: SingleLiveEvent<Boolean>? = null

    init
    {
        btnSelected = ObservableBoolean(false)
        email = ObservableField("")
        password = ObservableField("")
        muUser = MutableLiveData<LoginUser>()
        tempUser = MutableLiveData<LoginUser>()
        progressDialog = SingleLiveEvent<Boolean>()
    }
    companion object
    {
        val TAG :String = "Trav.LoginVm"
    }

    // '이메일' EditText의 입력의 변화를 감지하는 부분
    fun onEmailChanged(s: CharSequence, start: Int, befor: Int, count: Int)
    {
        btnSelected?.set(Util.isEmailValid(s.toString()) && password?.get()!!.length >= 8)
    }

    // 'Password' EditText의 입력의 변화를 감지하는 부분
    fun onPasswordChanged(s: CharSequence, start: Int, befor: Int, count: Int)
    {
        btnSelected?.set(Util.isEmailValid(email?.get()!!) && s.toString().length >= 8)
    }

    // 직접 로그인시 서버에 email과 password를 보내 검증하는 부분.
    fun login() {
        progressDialog?.value = true

        WebService.client.create(LoginAPI::class.java).LOGIN(LoginUser(email?.get()!!, password?.get()!!))
            .enqueue(this)
    }

    //ViewModel이 끝날 때 불림.(Activity LifeCycle이랑 생명주기가 다름)
    override fun onCleared() {
        super.onCleared()
    }

    // 로그인 요청시 Retrofit 결과 리턴받는 곳.
    override fun onResponse(call: Call<LoginUser>?, response: Response<LoginUser>?) {
        progressDialog?.value = false
        tempUser?.value = response?.body()
        /*
            로그인 성공시 받는 객체를 따로 만들어야함.
            밑에는 그냥 디버그용으로 만듬. 바꿔야함.
         */
        if(tempUser?.value?.lu_email == "fail..")
        {
            Toast.makeText(getApplication(), "Email 또는 Password가 틀렸습니다.", Toast.LENGTH_LONG).show()
        }
        else
        {
            muUser?.value = tempUser?.value;
            /*
            val nextIntent = Intent(getApplication(), IdolActivity::class.java)
            nextIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(getApplication(), nextIntent, null)
             */
            Toast.makeText(getApplication(), "로그인에 성공하였습니다.", Toast.LENGTH_LONG).show()
            val ls_goLogin = Intent(getApplication(), MyPage::class.java)
            ls_goLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            ls_goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(getApplication(), ls_goLogin, null)
        }
    }

    //로그인 요청시 Retrofit 통신 실패시 호출..
    override fun onFailure(call: Call<LoginUser>?, t: Throwable?) {
        Toast.makeText(getApplication(), "서버와의 통신에 실패하였습니다." + t.toString(), Toast.LENGTH_LONG).show()
        progressDialog?.value = false
    }
}