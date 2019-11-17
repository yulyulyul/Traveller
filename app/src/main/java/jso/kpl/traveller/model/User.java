package jso.kpl.traveller.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("u_userid")
    int u_userid;
    @SerializedName("u_email")
    String u_email;
    @SerializedName("u_pwd")
    String u_pwd;
    @SerializedName("u_nick_name")
    String u_nick_name;
    @SerializedName("u_device")
    String u_device;
    @SerializedName("u_profile_img")
    String u_profile_img;

    public User(String u_email, String u_pwd, String u_nick_name, String u_device) {
        this.u_email = u_email;
        this.u_pwd = u_pwd;
        this.u_nick_name = u_nick_name;
        this.u_device = u_device;
    }

    public User(int u_userid, String u_email, String u_pwd, String u_nick_name, String u_device, String u_profile_img) {
        this.u_userid = u_userid;
        this.u_email = u_email;
        this.u_pwd = u_pwd;
        this.u_nick_name = u_nick_name;
        this.u_device = u_device;
        this.u_profile_img = u_profile_img;
    }

    public int getU_userid() {
        return u_userid;
    }

    public void setU_userid(int u_userid) {
        this.u_userid = u_userid;
    }

    public String getU_email() {
        return u_email;
    }

    public void setU_email(String u_email) {
        this.u_email = u_email;
    }

    public String getU_pwd() {
        return u_pwd;
    }

    public void setU_pwd(String u_pwd) {
        this.u_pwd = u_pwd;
    }

    public String getU_nick_name() {
        return u_nick_name;
    }

    public void setU_nick_name(String u_nick_name) {
        this.u_nick_name = u_nick_name;
    }

    public String getU_device() {
        return u_device;
    }

    public void setU_device(String u_device) {
        this.u_device = u_device;
    }

    public String getU_profile_img() {
        return u_profile_img;
    }

    public void setU_profile_img(String u_profile_img) {
        this.u_profile_img = u_profile_img;
    }

    @Override
    public String toString() {
        return "User{" +
                "u_userid=" + u_userid +
                ", u_email='" + u_email + '\'' +
                ", u_pwd='" + u_pwd + '\'' +
                ", u_nick_name='" + u_nick_name + '\'' +
                ", u_device='" + u_device + '\'' +
                ", u_profile_img='" + u_profile_img + '\'' +
                '}';
    }
}

