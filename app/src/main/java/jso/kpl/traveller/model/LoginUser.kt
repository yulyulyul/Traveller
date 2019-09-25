package jso.kpl.traveller.model

/*
    로그인 화면에서 이메일, 페스워드를 입력하고 서버랑 통신할 때
    쓰는 객체
 */
data class LoginUser(var lu_email:String, var lu_pwd:String) {}