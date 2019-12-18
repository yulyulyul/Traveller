package jso.kpl.traveller.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.UnLinkResponseCallback
import jso.kpl.traveller.App
import jso.kpl.traveller.R
import jso.kpl.traveller.model.LoginUser
import jso.kpl.traveller.model.ResponseResult
import jso.kpl.traveller.model.User
import jso.kpl.traveller.network.UserAPI
import jso.kpl.traveller.network.WebService
import jso.kpl.traveller.thirdpartyapi.facebook.LoginCallback
import jso.kpl.traveller.thirdpartyapi.kakao.SessionCallback
import jso.kpl.traveller.ui.LoginSelect
import jso.kpl.traveller.ui.LoginSelect.Companion.getInstance
import jso.kpl.traveller.ui.MainTab
import jso.kpl.traveller.ui.SignUp
import jso.kpl.traveller.util.JavaUtil
import mvvm.f4wzy.com.samplelogin.util.SingleLiveEvent
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.security.auth.callback.Callback

class LoginSelectViewModel(application: Application) : AndroidViewModel(application), retrofit2.Callback<ResponseResult<User>>{

    // 카카오로 시작하기 버튼을 눌렀을때 view(Login.kt)에 변화를 알려주는 객체
    var iskakaoLogin: MutableLiveData<Boolean>? = null
    // 카카오 로그인 callback
    var kakao_session_callback: SessionCallback? = null

    // 페이스북 callback을 관리해주는 객체
    var FacebookCallbackManager: CallbackManager? = null
    // 페이스북으로 시작하기 버튼을 눌렀을때 view(Login.kt)에 변화를 알려주는 객체
    var isFacebookLogin: MutableLiveData<Boolean>? = null
    // 페이스북 로그인 callback
    var facebook_session_callback: LoginCallback? = null

    // Firebase 인증 객체 선언
    var firebaseAuth: FirebaseAuth? = null

    //자동 로그인
    var progressDialog: SingleLiveEvent<Boolean>? = null

    //통신으로 받은 유저 객체
    var receiveUser: User? = null

    var callback : retrofit2.Callback<ResponseResult<User>> ?= null

    init {

        callback = this

        progressDialog = SingleLiveEvent<Boolean>()

        //카카오
        iskakaoLogin = MutableLiveData<Boolean>()


        //페이스북
        isFacebookLogin = MutableLiveData<Boolean>()
        FacebookCallbackManager = CallbackManager.Factory.create()

        //구글
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun gotoLogin() {

      //  val ls_goLogin = Intent(getApplication(), jso.kpl.traveller.ui.Fragment.MyPage::class.java)
        val ls_goLogin = Intent(getApplication(), jso.kpl.traveller.ui.Login::class.java)
        ls_goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ContextCompat.startActivity(getApplication(), ls_goLogin, null)
    }

    fun googleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(App.INSTANCE.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        var googleSignInClient: GoogleSignInClient =
            GoogleSignIn.getClient(LoginSelect.getInstance(), gso)

        val signInIntent = googleSignInClient.signInIntent
        LoginSelect.getInstance().startActivityForResult(signInIntent, LoginSelect.RC_SIGN_IN)
    }

    //구글 로그인 버튼
    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        App.getGoogleAuth().signInWithCredential(credential).addOnCompleteListener(getInstance())
        { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = App.getGoogleAuth().currentUser
                Log.d(TAG, "Google Login , Email : " + user?.email)
                Log.d(TAG, "Google Login , displayName : " + user?.displayName)
                Log.d(TAG, "Google Login , phoneNumber : " + user?.phoneNumber)
                Log.d(TAG, "Google Login , photoUrl : " + user?.photoUrl)
                Log.d(TAG, "Google Login , providerId : " + user?.providerId)
                Log.d(TAG, "Google Login , uid : " + user?.uid)

                goToSignUp(user?.email)
//                successLogin()
            } else {
                Log.d(TAG, "signInWithCredential:failure", task.exception)
//            Snackbar.make(getInstance().view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show() }
            }
        }
    }

    //구글 로그아웃
    fun googleLogout() {
        Log.d(TAG, "googleLogout..")
        App.getGoogleAuth().signOut()

        val user = App.getGoogleAuth().currentUser
        Log.d(TAG, "Google Login , Email : " + user?.email)
        Log.d(TAG, "Google Login , displayName : " + user?.displayName)
        Log.d(TAG, "Google Login , phoneNumber : " + user?.phoneNumber)
        Log.d(TAG, "Google Login , photoUrl : " + user?.photoUrl)
        Log.d(TAG, "Google Login , providerId : " + user?.providerId)
        Log.d(TAG, "Google Login , uid : " + user?.uid)
    }

    fun facebookLogin() {
        isFacebookLogin?.value = true
        JavaUtil.printHashKey(getApplication())
    }

    fun facebook_callback() {
        facebook_session_callback = LoginCallback(getApplication())
        val loginManager = LoginManager.getInstance()
        loginManager.logInWithReadPermissions(
            getInstance(),
            Arrays.asList("public_profile", "email", "user_friends")
        )
        loginManager.registerCallback(FacebookCallbackManager, facebook_session_callback)

    }

    //페이스북 로그아웃
    fun facebookLogout() {
        LoginManager.getInstance().logOut()
    }

    fun kakaoLogin() {
        iskakaoLogin?.value = true
    }


    fun kakao_callback() {
        if (kakao_session_callback != null) {
            Session.getCurrentSession().removeCallback(kakao_session_callback)
        }
        //카카오로 로그인하여 생기는 결과가 리턴하는 곳.
        kakao_session_callback = SessionCallback(getApplication())
        Session.getCurrentSession().addCallback(kakao_session_callback)


    }

    fun kakaoLogout() {
        UserManagement.getInstance().requestUnlink(object : UnLinkResponseCallback() {
            override fun onSessionClosed(errorResult: ErrorResult) {
                Log.e(TAG, "카카오 로그아웃 onSessionClosed")
            }

            override fun onNotSignedUp() {
                Log.e(TAG, "카카오 로그아웃 onNotSignedUp")
            }

            override fun onSuccess(result: Long?) {
                Log.e(TAG, "카카오 로그아웃 onSuccess")
            }
        })
    }

    // 로그인 Callback들을 clear 해주는 함수.
    fun clearCallback() {
        //kakao callback 해제.
        if (kakao_session_callback != null) {
            Log.d(TAG, "kakao_session_callback를 해제합니다.")
            Session.getCurrentSession().removeCallback(kakao_session_callback)
        }

    }

    fun generalSignUp() {
        goToSignUp(null)
    }

    //ViewModel이 끝날 때 불림.(Activity LifeCycle이랑 생명주기가 다름)
    override fun onCleared() {
        super.onCleared()
        clearCallback()
    }

    fun goToSignUp(email: String?) {

        val su_intent = Intent(getApplication(), SignUp::class.java)

        su_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (email != null) {
            su_intent.putExtra("email", email)
            su_intent.putExtra("auth", true)
        } else {
            su_intent.putExtra("auth", false)
        }

        ContextCompat.startActivity(getApplication(), su_intent, null)

    }

    fun autoLogin(act:Activity){
        val sp = act.getSharedPreferences("auto_login", Activity.MODE_PRIVATE)

        val email = sp.getString("u_email", "")
        val pwd = sp.getString("u_pwd", "")

        if(!email.equals("") && !pwd.equals("")){
            WebService.client.create(UserAPI::class.java)
                .goLogin(LoginUser(email, pwd))
                .enqueue(callback)
        }
    }

    // 로그인 요청시 Retrofit 결과 리턴받는 곳.
    override fun onResponse(call: Call<ResponseResult<User>>?,
                            response: Response<ResponseResult<User>>?
    ) {
        progressDialog?.value = false

        var res_type:Int? = response?.body()?.res_type

        if(res_type == 1){
            receiveUser = response?.body()?.res_obj
            Log.d(LoginViewModel.TAG + "응답 객체 : ", receiveUser?.toString())

            if (receiveUser != null) {

                val ls_goLogin = Intent(getApplication(), MainTab::class.java)

                ls_goLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                ls_goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                ls_goLogin.putExtra("user", receiveUser);

                ContextCompat.startActivity(getApplication(), ls_goLogin, null)
            }
        }
    }

    //로그인 요청시 Retrofit 통신 실패시 호출..
    override fun onFailure(call: Call<ResponseResult<User>>?, t: Throwable?) {
        progressDialog?.value = false
    }

    companion object {
        val TAG: String = "Trav.LoginSelectVm"
    }
}