package jso.kpl.traveller.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import jso.kpl.traveller.App
import jso.kpl.traveller.model.LoginUser
import jso.kpl.traveller.model.ResponseResult
import jso.kpl.traveller.model.User
import jso.kpl.traveller.network.LoginAPI
import jso.kpl.traveller.network.WebService
import jso.kpl.traveller.ui.MainTab
import jso.kpl.traveller.ui.MyPage
import mvvm.f4wzy.com.samplelogin.util.SingleLiveEvent
import mvvm.f4wzy.com.samplelogin.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * login,xml에 대응하는 ViewModel
 */
class LoginViewModel(application: Application) : AndroidViewModel(application),
    Callback<ResponseResult<User>> {
    //버튼 활성화(이메일 형식이 아닐시, 비밀번호가 8글자 이상이 아닐시 로그인 버튼을 비활성화 시켜줌.
    var btnSelected: ObservableBoolean? = null
    var email: ObservableField<String>? = null
    var password: ObservableField<String>? = null
    var muUser: MutableLiveData<User>? = null
    var progressDialog: SingleLiveEvent<Boolean>? = null

    //통신으로 받은 유저 객체
    var receiveUser: User? = null

    init {
        btnSelected = ObservableBoolean(false)
        email = ObservableField("test@gmail.com")
        password = ObservableField("1q2w3e4r")
        muUser = MutableLiveData<User>()
        progressDialog = SingleLiveEvent<Boolean>()
    }

    companion object {
        val TAG: String = "Trav.LoginVm"
    }

    // '이메일' EditText의 입력의 변화를 감지하는 부분
    fun onEmailChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        btnSelected?.set(Util.isEmailValid(s.toString()) && password?.get()!!.length >= 8)

    }

    // 'Password' EditText의 입력의 변화를 감지하는 부분
    fun onPasswordChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        btnSelected?.set(Util.isEmailValid(email?.get()!!) && s.toString().length >= 8)
    }

    // 직접 로그인시 서버에 email과 password를 보내 검증하는 부분.
    fun login() {
        progressDialog?.value = true

        val SHAPassword: String? = returnSHA256(password?.get()!!)

        WebService.client.create(LoginAPI::class.java)
            .LOGIN(LoginUser(email?.get()!!, SHAPassword!!))
            .enqueue(this)
    }

    //입력받은 패스워드를 sha256으로 해시화 해서 보낸다.
    fun returnSHA256(str: String): String? {

        var SHA: String? = ""

        try {
            val sh = MessageDigest.getInstance("SHA-256")

            sh.update(str.toByteArray())

            val byteData = sh.digest()

            val sb = StringBuffer()

            for (i in byteData.indices) {
                sb.append(Integer.toString((byteData[i].toInt() and 0xff) + 0x100, 16).substring(1))
            }

            SHA = sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            SHA = null
        }

        return SHA
    }

    //ViewModel이 끝날 때 불림.(Activity LifeCycle이랑 생명주기가 다름)
    override fun onCleared() {
        super.onCleared()
    }

    // 로그인 요청시 Retrofit 결과 리턴받는 곳.
    override fun onResponse(call: Call<ResponseResult<User>>?,
        response: Response<ResponseResult<User>>?
    ) {
        progressDialog?.value = false

        var res_type:Int? = response?.body()?.res_type

        if(res_type == 1){
            receiveUser = response?.body()?.res_obj
            Log.d(TAG + "응답 객체 : ", receiveUser?.toString())

            if (receiveUser != null) {

                Toast.makeText(getApplication(), "로그인에 성공하였습니다.", Toast.LENGTH_LONG).show()
                val ls_goLogin = Intent(getApplication(), MainTab::class.java)

                ls_goLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                ls_goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                ls_goLogin.putExtra("user", receiveUser);

                ContextCompat.startActivity(getApplication(), ls_goLogin, null)
            } else {
                Toast.makeText(getApplication(), "Email 또는 Password가 틀렸습니다.", Toast.LENGTH_LONG).show()

            }
        } else if(res_type == 0){
            Toast.makeText(getApplication(), "Email 또는 Password가 틀렸습니다.", Toast.LENGTH_LONG).show()
        }else if(res_type == -1){

        }
    }

    //로그인 요청시 Retrofit 통신 실패시 호출..
    override fun onFailure(call: Call<ResponseResult<User>>?, t: Throwable?) {
        Toast.makeText(getApplication(), "서버와의 통신에 실패하였습니다." + t.toString(), Toast.LENGTH_LONG)
            .show()
        progressDialog?.value = false
    }
}