package jso.kpl.traveller.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import jso.kpl.traveller.databinding.LoginBinding
import jso.kpl.traveller.R
import jso.kpl.traveller.viewmodel.LoginViewModel
import kotlin.math.log


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
