package jso.kpl.traveller.ui

import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import jso.kpl.traveller.App
import jso.kpl.traveller.R
import jso.kpl.traveller.databinding.LoginBinding
import jso.kpl.traveller.util.GMailSender
import jso.kpl.traveller.util.RegexMethod
import jso.kpl.traveller.viewmodel.LoginViewModel
import javax.mail.MessagingException
import javax.mail.SendFailedException

class Login : AppCompatActivity() {

    var BindingLogin: LoginBinding? = null
    var viewmodel: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        INSTANCE = this
        BindingLogin = DataBindingUtil.setContentView(this, R.layout.login)
        viewmodel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        BindingLogin?.viewmodel = viewmodel
        BindingLogin?.linear?.setOnClickListener(({
            var imm = getSystemService(INPUT_METHOD_SERVICE)
            if (imm is InputMethodManager) {
                imm.hideSoftInputFromWindow(BindingLogin?.inputEmail?.windowToken, 0)
            }
        }))
        BindingLogin?.findPwd?.setOnClickListener(({
            onFindPwdBtn()
        }))
        BindingLogin?.backBtn?.setOnClickListener(({
            onFindPwdBtn()
        }))
        BindingLogin?.findPwdBtn?.setOnClickListener(({
            BindingLogin?.findPwdError?.visibility = View.GONE
            var emailTxt = BindingLogin?.findPwdInputEmail?.text?.trim().toString()
            if (!emailTxt.equals("")) {
                if (RegexMethod.isEmailValid(emailTxt)) {
                    BindingLogin?.findPwdBtn?.isClickable = false
                    BindingLogin?.findPwdBtn?.text = "확인 중"
                    BindingLogin?.viewmodel?.findPwd(emailTxt) {
                        if (it) {
                            StrictMode.setThreadPolicy(
                                StrictMode.ThreadPolicy.Builder()
                                    .permitDiskReads()
                                    .permitDiskWrites()
                                    .permitNetwork().build()
                            )
                            try {
                                var gMailSender: GMailSender =
                                    GMailSender("traveller.sup@gmail.com", "1a2s3d4f!@")
                                var tempPwd: String = gMailSender.emailCode
                                gMailSender.sendMail(
                                    "Traveller 임시 비밀번호 발급",
                                    "임시 비밀번호 : " + tempPwd,
                                    emailTxt
                                )
                                BindingLogin?.viewmodel?.updatePwdTemp(emailTxt, tempPwd)
                                App.sendToast("해당 이메일로 임시 비밀번호가 발급되었습니다.")
                                onFindPwdBtn()
                            } catch (e: SendFailedException) {
                                App.sendToast("이메일 형식이 잘못되었습니다.")
                            } catch (e: MessagingException) {
                                App.sendToast("인터넷 연결을 확인 후 다시 시도해주세요.")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            BindingLogin?.findPwdBtn?.isClickable = true
                            BindingLogin?.findPwdBtn?.text = "확인"
                            BindingLogin?.findPwdError?.text =
                                BindingLogin?.viewmodel?.findPwdInputError?.get(2)
                            BindingLogin?.findPwdError?.visibility = View.VISIBLE
                        }
                    }
                } else {
                    BindingLogin?.findPwdError?.text =
                        BindingLogin?.viewmodel?.findPwdInputError?.get(1)
                    BindingLogin?.findPwdError?.visibility = View.VISIBLE
                }
            } else {
                BindingLogin?.findPwdError?.text =
                    BindingLogin?.viewmodel?.findPwdInputError?.get(0)
                BindingLogin?.findPwdError?.visibility = View.VISIBLE
            }
        }))
    }

    fun onFindPwdBtn() {
        viewmodel?.isFindPwd?.value = !(viewmodel?.isFindPwd?.value!!)
        if (viewmodel?.isFindPwd?.value!!) {
            BindingLogin?.scroll?.visibility = View.GONE
            BindingLogin?.constraintLayout?.visibility = View.VISIBLE
        } else {
            BindingLogin?.scroll?.visibility = View.VISIBLE
            BindingLogin?.constraintLayout?.visibility = View.GONE
            BindingLogin?.findPwdBtn?.isClickable = true
            BindingLogin?.findPwdInputEmail?.setText("")
            BindingLogin?.findPwdBtn?.text = "확인"
            BindingLogin?.findPwdError?.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()

        BindingLogin?.viewmodel?.isLogin?.observe(this, Observer { it ->
            if (it) {
                BindingLogin?.viewmodel?.autoSaveLogin(this)
            }
        })

        BindingLogin?.viewmodel?.onCleared()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private lateinit var INSTANCE: Login
        var TAG: String = "Trav.Login"

        fun getInstance(): Login {
            return INSTANCE;
        }
    }
}
