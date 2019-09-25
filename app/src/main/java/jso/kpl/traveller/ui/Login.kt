package jso.kpl.traveller.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import jso.kpl.traveller.databinding.LoginBinding
import jso.kpl.traveller.R
import jso.kpl.traveller.viewmodel.LoginViewModel


class Login : AppCompatActivity() {

    var BindingLogin : LoginBinding? = null
    var viewmodel: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        INSTANCE = this
        BindingLogin = DataBindingUtil.setContentView(this, R.layout.login)
        viewmodel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        BindingLogin?.viewmodel = viewmodel

        initObservables()
    }

    private fun initObservables()
    {
        viewmodel?.muUser?.observe(this, Observer { user ->
            Toast.makeText(this, "welcome, ${user?.lu_email}", Toast.LENGTH_LONG).show()
        })
    }

    override fun onDestroy()
    {
        super.onDestroy()
    }

    companion object{
        private lateinit var INSTANCE: Login
        var TAG : String = "Trav.Login"

        fun getInstance() : Login
        {
            return INSTANCE;
        }
    }
}
