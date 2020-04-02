package jso.kpl.traveller.ui

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.iid.FirebaseInstanceId
import jso.kpl.traveller.App
import jso.kpl.traveller.R
import jso.kpl.traveller.databinding.LoginBinding
import jso.kpl.traveller.model.ResponseResult
import jso.kpl.traveller.network.UserAPI
import jso.kpl.traveller.network.WebService
import jso.kpl.traveller.util.GMailSender
import jso.kpl.traveller.util.RegexMethod
import jso.kpl.traveller.viewmodel.LoginViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.mail.MessagingException
import javax.mail.SendFailedException

class Login : AppCompatActivity() {

    var loginBinding: LoginBinding? = null
    var loginVm: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        INSTANCE = this
        loginBinding = DataBindingUtil.setContentView(this, R.layout.login)
        loginVm = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        loginBinding?.loginVm = loginVm
        loginBinding?.linear?.setOnClickListener(({
            var imm = getSystemService(INPUT_METHOD_SERVICE)
            if (imm is InputMethodManager) {
                imm.hideSoftInputFromWindow(loginBinding?.inputEmail?.windowToken, 0)
            }
        }))
        loginBinding?.findPwd?.setOnClickListener(({
            onFindPwdBtn()
        }))
        loginBinding?.backBtn?.setOnClickListener(({
            onFindPwdBtn()
        }))
        loginBinding?.findPwdBtn?.setOnClickListener(({
            loginBinding?.findPwdError?.visibility = View.GONE
            var emailTxt = loginBinding?.findPwdInputEmail?.text?.trim().toString()
            if (!emailTxt.equals("")) {
                if (RegexMethod.isEmailValid(emailTxt)) {
                    loginBinding?.findPwdBtn?.isClickable = false
                    loginBinding?.findPwdBtn?.text = "확인 중"
                    loginBinding?.loginVm?.findPwd(emailTxt) {
                        if (it) {
                            StrictMode.setThreadPolicy(
                                StrictMode.ThreadPolicy.Builder()
                                    .permitDiskReads()
                                    .permitDiskWrites()
                                    .permitNetwork().build()
                            )
                            try {
                                var gMailSender = GMailSender()
                                var tempPwd: String = gMailSender.emailCode
                                gMailSender.sendMail(
                                    "Traveller 임시 비밀번호 발급",
                                    "임시 비밀번호 : " + tempPwd,
                                    emailTxt
                                )
                                loginBinding?.loginVm?.updatePwdTemp(emailTxt, tempPwd)
                                App.sendToast("해당 이메일로 임시 비밀번호가 발급되었습니다.")
                                onFindPwdBtn()
                            } catch (e: SendFailedException) {
                                App.sendToast("이메일 형식이 잘못되었습니다.")
                                Log.d(TAG, e.message.toString())
                            } catch (e: MessagingException) {
                                App.sendToast("인터넷 연결을 확인 후 다시 시도해주세요.")
                                Log.d(TAG, e.message.toString())
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.d(TAG, e.message.toString())
                            }
                        } else {
                            loginBinding?.findPwdBtn?.isClickable = true
                            loginBinding?.findPwdBtn?.text = "확인"
                            loginBinding?.findPwdError?.text =
                                loginBinding?.loginVm?.findPwdInputError?.get(2)
                            loginBinding?.findPwdError?.visibility = View.VISIBLE
                        }
                    }
                } else {
                    loginBinding?.findPwdError?.text =
                        loginBinding?.loginVm?.findPwdInputError?.get(1)
                    loginBinding?.findPwdError?.visibility = View.VISIBLE
                }
            } else {
                loginBinding?.findPwdError?.text =
                    loginBinding?.loginVm?.findPwdInputError?.get(0)
                loginBinding?.findPwdError?.visibility = View.VISIBLE
            }
        }))

        onAutoLogin()
    }

    private fun onAutoLogin() {

        loginBinding?.loginVm?.isLogin?.observe(this, Observer { it ->
            if (it) {
                loginBinding?.loginVm?.autoSaveLogin(this)

                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->

                    Log.d(LoginViewModel.TAG, "토큰 발행: " + task.result!!.token)

                    WebService.client.create(UserAPI::class.java)
                        .uploadToken(
                            loginBinding?.loginVm?.receiveUser!!.u_userid,
                            task.result!!.token
                        )
                        .enqueue(object : Callback<ResponseResult<Int>> {
                            override fun onResponse(
                                call: Call<ResponseResult<Int>>,
                                response: Response<ResponseResult<Int>>
                            ) {
                                val ls_goLogin = Intent(getApplication(), MainTab::class.java)

                                ls_goLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                ls_goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                                ls_goLogin.putExtra("user", loginBinding?.loginVm?.receiveUser);

                                ContextCompat.startActivity(getApplication(), ls_goLogin, null)

                                loginBinding?.loginVm?.onCleared()
                                finish()
                            }

                            override fun onFailure(call: Call<ResponseResult<Int>>, t: Throwable) {
                            }
                        })
                }
            }
        })
    }

    fun onFindPwdBtn() {
        loginVm?.isFindPwd?.value = !(loginVm?.isFindPwd?.value!!)
        if (loginVm?.isFindPwd?.value!!) {
            loginBinding?.scroll?.visibility = View.GONE
            loginBinding?.constraintLayout?.visibility = View.VISIBLE
        } else {
            loginBinding?.scroll?.visibility = View.VISIBLE
            loginBinding?.constraintLayout?.visibility = View.GONE
            loginBinding?.findPwdBtn?.isClickable = true
            loginBinding?.findPwdInputEmail?.setText("")
            loginBinding?.findPwdBtn?.text = "확인"
            loginBinding?.findPwdError?.visibility = View.GONE
        }
    }

    companion object {
        private lateinit var INSTANCE: Login
        var TAG: String = "Trav.Login"

        fun getInstance(): Login {
            return INSTANCE;
        }
    }
}
