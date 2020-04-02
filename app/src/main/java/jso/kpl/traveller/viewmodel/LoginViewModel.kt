package jso.kpl.traveller.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import jso.kpl.traveller.App
import jso.kpl.traveller.model.LoginUser
import jso.kpl.traveller.model.ResponseResult
import jso.kpl.traveller.model.User
import jso.kpl.traveller.network.UserAPI
import jso.kpl.traveller.network.WebService
import jso.kpl.traveller.util.JavaUtil
import jso.kpl.traveller.util.RegexMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * login,xml에 대응하는 ViewModel
 */
class LoginViewModel(application: Application) : AndroidViewModel(application),
    Callback<ResponseResult<User>> {
    //버튼 활성화(이메일 형식이 아닐시, 비밀번호가 8글자 이상이 아닐시 로그인 버튼을 비활성화 시켜줌.
//    var btnSelected: ObservableBoolean? = null
    var email: ObservableField<String>? = null
    var password: ObservableField<String>? = null
    var muUser: MutableLiveData<User>? = null

    var isLogin: MutableLiveData<Boolean>? = null
    var isFindPwd: MutableLiveData<Boolean>? = null

    var findPwdInputError: ArrayList<String>? = null
    var SHAPassword: String? = null

    //통신으로 받은 유저 객체
    var receiveUser: User? = null

    init {
        email = ObservableField("")
        password = ObservableField("")
        muUser = MutableLiveData<User>()

        isLogin = MutableLiveData<Boolean>()
        isFindPwd = MutableLiveData<Boolean>()
        isFindPwd?.value = false

        findPwdInputError = ArrayList(
            Arrays.asList(
                "이메일을 입력하지 않았습니다.\n이메일을 입력해주세요.",
                "입력하신 이메일 형식이 올바르지 않습니다.",
                "가입 이력이 없는 이메일입니다.\n이메일을 확인해주세요."
            )
        )
    }

    companion object {
        val TAG: String = "Trav.LoginVm"
    }

    // 직접 로그인시 서버에 email과 password를 보내 검증하는 부분.
    fun onLoginclicked() {

        if(RegexMethod.isEmailValid(email?.get().toString()) && RegexMethod.isPasswordValid(password?.get()!!)){
            //   progressDialog?.value = true
            SHAPassword = JavaUtil.returnSHA256(password?.get()!!)

            WebService.client.create(UserAPI::class.java)
                .goLogin(LoginUser(email?.get()!!, SHAPassword!!))
                .enqueue(this)
        } else {
            App.sendToast("이메일 또는 비밀번호가 틀렸습니다.")
        }
    }

    fun findPwd(email: String, callback: ((Boolean) -> Unit)) {
        WebService.client.create(UserAPI::class.java)
            .authEmail(email)
            .enqueue(object : Callback<ResponseResult<Int>> {
                override fun onResponse(
                    call: Call<ResponseResult<Int>>,
                    response: Response<ResponseResult<Int>>
                ) {
                    if (response.body()?.res_obj != null && response.body()?.res_obj == 0) {
                        callback.invoke(true)
                    } else {
                        callback.invoke(false)
                    }
                }

                override fun onFailure(call: Call<ResponseResult<Int>>, t: Throwable?) {
                    App.sendToast("서버 통신에 실패하였습니다.\n잠시 후 다시 시도해주세요.")
                }
            })
    }

    fun updatePwdTemp(email: String, tempPwd: String) {
        WebService.client.create(UserAPI::class.java)
            .updateUserTempPwd(email, JavaUtil.returnSHA256(tempPwd))
            .enqueue(object : Callback<ResponseResult<Int>> {
                override fun onResponse(
                    call: Call<ResponseResult<Int>>,
                    response: Response<ResponseResult<Int>>
                ) {
                    if (response.body()?.res_obj != null && response.body()?.res_obj == 1) {
                        Log.d(TAG, "임시 비밀번호 수정 성공")
                    } else {
                        Log.d(TAG, "임시 비밀번호 수정 실패")
                    }
                }

                override fun onFailure(call: Call<ResponseResult<Int>>, t: Throwable?) {
                    App.sendToast("서버 통신에 실패하였습니다.\n잠시 후 다시 시도해주세요.")
                }
            })
    }

    //ViewModel이 끝날 때 불림.(Activity LifeCycle이랑 생명주기가 다름)
    public override fun onCleared() {
        super.onCleared()
    }

    // 로그인 요청시 Retrofit 결과 리턴받는 곳.
    override fun onResponse(call: Call<ResponseResult<User>>?, response: Response<ResponseResult<User>>?) {
        var res_type: Int? = response?.body()?.res_type

        if (res_type == 1) {

            receiveUser = response?.body()?.res_obj

            Log.d(TAG + "로그인 객체: ", receiveUser?.toString())

            if (receiveUser != null) {

                //로그인 확인 저장 용
                isLogin?.value = true

            } else {
                App.sendToast("이메일 또는 비밀번호가 틀렸습니다.")
            }
        } else if (res_type == 0) {
            App.sendToast("이메일 또는 비밀번호가 틀렸습니다.")
        }
    }

    //로그인 요청시 Retrofit 통신 실패시 호출..
    override fun onFailure(call: Call<ResponseResult<User>>?, t: Throwable?) {
        App.sendToast("로그인에 실패하셨습니다.")
    }

    //로그인 SharedPreferences
    fun autoSaveLogin(act: Activity) {
        val sp = act.getSharedPreferences("auto_login", Activity.MODE_PRIVATE)
        val editor = sp.edit()

        editor.putString("u_email", email?.get())
        editor.putString("u_pwd", SHAPassword)

        editor.commit()
    }

}