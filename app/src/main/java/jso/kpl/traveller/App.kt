package jso.kpl.traveller

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import com.facebook.AccessToken
import com.facebook.Profile
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import java.security.MessageDigest
import jso.kpl.traveller.thirdpartyapi.kakao.KakaoSDKAdapter
import com.kakao.auth.KakaoSDK
import com.kakao.auth.Session
import jso.kpl.traveller.databinding.CustomToastMsgBinding
import jso.kpl.traveller.model.Message
import jso.kpl.traveller.model.User
import jso.kpl.traveller.ui.MsgList
import jso.kpl.traveller.viewmodel.LoginSelectViewModel
import java.time.Duration
import kotlin.math.log


/**
    Application Class
 */
class App : Application()
{
    companion object
    {
        // Context와 같은 역활
        lateinit var INSTANCE: App
        private lateinit var googleAuth: FirebaseAuth

        var TAG :String = "Trav.App"

        lateinit var user: User

        fun sendToast(msg:String){
            Toast.makeText(App.INSTANCE, msg, Toast.LENGTH_SHORT).show();
        }

        fun floatingUpperSnackBAr(context : Context, msg : String, isClick : Boolean){

            val inflater = LayoutInflater.from(context)

            var binding : CustomToastMsgBinding = DataBindingUtil.inflate(inflater, R.layout.custom_toast_msg, null, false)

            binding.msg = msg

            var t : Toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);

            t.setGravity(Gravity.CENTER or Gravity.TOP, 0, 50)
            t.view = binding.root
            t.show()

            if(isClick){
                binding.onToastClickListener = View.OnClickListener {
                    var intent : Intent = Intent(context, MsgList::class.java);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    t.cancel()
                }
            }

        }

        fun getGoogleAuth() : FirebaseAuth
        {
            return googleAuth
        }
    }

    override fun onCreate()
    {
        super.onCreate()
        INSTANCE = this

        //카카오api를 위한 해쉬키 발급
        getHashKey(INSTANCE)

        // Kakao Sdk 초기화
        KakaoSDK.init(KakaoSDKAdapter())

        /** 카카오 토큰 만료시 갱신을 시켜준다**/
        if(Session.getCurrentSession().isOpenable())
        {
            Session.getCurrentSession().checkAndImplicitOpen();
        }
        else
        {
            Log.e(TAG, "토큰 : " + Session.getCurrentSession().getTokenInfo().getAccessToken());
            Log.e(TAG, "토큰 리프레쉬토큰 : " + Session.getCurrentSession().getTokenInfo().getRefreshToken());
            Log.e(TAG, "토큰 파이어데이트 : " + Session.getCurrentSession().getTokenInfo().getRemainingExpireTime());
        }

        // 페이스북 토큰 만료 확인하는 루틴
        if (AccessToken.getCurrentAccessToken() != null) {
            var myProf:Profile = Profile.getCurrentProfile()
            Log.d(TAG, "user id : " + AccessToken.getCurrentAccessToken().getUserId());
            Log.d(TAG, "firstName : " + myProf.firstName)
            Log.d(TAG, "lastName : " + myProf.lastName)
            Log.d(TAG, "linkUri : " + myProf.getProfilePictureUri(400,400).path)
        }
        //페이스북 토큰이 만료되어서 다시 로그인을 거쳐야함.
        else
        {
            Log.d(TAG, "로그인해야함.")
        }

        //현재 로그인 되어 있는지 확인
        googleAuth = FirebaseAuth.getInstance()
        var currentUser = App.getGoogleAuth().currentUser

        Log.d(TAG, "Google Login , Email : " + currentUser?.email)
        Log.d(TAG, "Google Login , displayName : " + currentUser?.displayName)
        Log.d(TAG, "Google Login , phoneNumber : " + currentUser?.phoneNumber)
        Log.d(TAG, "Google Login , photoUrl : " + currentUser?.photoUrl)
        Log.d(TAG, "Google Login , providerId : " + currentUser?.providerId)
        Log.d(TAG, "Google Login , uid : " + currentUser?.uid)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                val m = intent.getSerializableExtra("msg") as Message

                var msg : String = m.m_sender + "님께 쪽지가 도착했습니다."

                floatingUpperSnackBAr(App.INSTANCE, msg, true)

            }
        }

        registerReceiver(receiver, IntentFilter("com.example.limky.broadcastreceiver.gogo"))
    }

    /*
        프로젝트의 해쉬키를 반환하는 함수.
        카카오 api 사용하기 위하여 해쉬키값이 있어야함.
     */
    @Nullable
    fun getHashKey(context: Context): String?
    {
        var keyHash: String? = null

        try {
            val info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES)

            for (signature in info.signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                keyHash = String(Base64.encode(md.digest(), 0))
                Log.d(TAG, "키해쉬 : "+keyHash)
            }

        }
        catch (e: Exception)
        {
            Log.e(TAG, e.toString())
        }
        return keyHash
    }
}