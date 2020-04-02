package jso.kpl.traveller.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.kakao.auth.Session
import jso.kpl.traveller.R
import jso.kpl.traveller.databinding.LoginSelectBinding
import jso.kpl.traveller.viewmodel.LoginSelectViewModel
import kotlinx.android.synthetic.main.login_select.*

/**
 * 카카오, 페이스북, 구글, 직접 회원가입을
 * 선택할 수 있는 뷰
 */
class LoginSelect : AppCompatActivity() {

    var loginSelectBinding: LoginSelectBinding? = null
    var loginSelectVm: LoginSelectViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        INSTANCE = this
        loginSelectBinding = DataBindingUtil.setContentView(this, R.layout.login_select)
        loginSelectVm = ViewModelProviders.of(this).get(LoginSelectViewModel::class.java)
        loginSelectBinding?.viewmodel = loginSelectVm

        //     loginSelectBinding?.viewmodel?.autoLogin(this)
        initObservables()

        window.setWindowAnimations(android.R.style.Animation_Toast)
    }

    private fun initObservables() {

        //'카카오로 시작하기' 버튼이 눌렸을때를 감지.
        loginSelectVm?.iskakaoLogin?.observe(this, Observer {
            /*
                btn_kakao_login => '카카오에서 제공하는 버튼'
                '카카오에서 제공하는 버튼'을 그냥 사용하기에는 앱에 디자인적으로 어울리지 않으니
                버튼을 커스텀하고 '카카오에서 제공하는 버튼'에게 클릭 이벤트를 줘서
                '카카오에서 제공하는 버튼'을 누르는 효과를 내게한다.
             */
            btn_kakao_login.performClick()
            loginSelectVm?.kakao_callback()
        })

        loginSelectVm?.isFacebookLogin?.observe(this, Observer {
            //            btn_facebook_login.performClick()
            Log.d(TAG, "viewmodel?.isFacebookLogin?.observe")
            loginSelectVm?.facebook_callback()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //카카오 로그인 세션
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        //페이스북 로그인 콜백
        else {
            loginSelectVm?.FacebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                loginSelectVm?.firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    companion object {
        private lateinit var INSTANCE: LoginSelect
        var TAG: String = "Trav.LoginSelect"
        val RC_SIGN_IN: Int = 900
        fun getInstance(): LoginSelect {
            return INSTANCE;
        }
    }

}
