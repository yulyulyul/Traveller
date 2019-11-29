package jso.kpl.traveller.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListItem implements Serializable {

    int u_userid;
    String u_profile_img;
    int p_id;
    String p_place;
    String p_expenses;
    String sp_imgs;

    public ListItem(int u_userid, String u_profile_img, int p_id, String p_place, String p_expenses, String sp_imgs) {
        this.u_userid = u_userid;
        this.u_profile_img = u_profile_img;
        this.p_id = p_id;
        this.p_place = p_place;
        this.p_expenses = p_expenses;
        this.sp_imgs = sp_imgs;
    }

    public int getU_userid() {
        return u_userid;
    }

    public void setU_userid(int u_userid) {
        this.u_userid = u_userid;
    }

    public String getU_profile_img() {
        return u_profile_img;
    }

    public void setU_profile_img(String u_profile_img) {
        this.u_profile_img = u_profile_img;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getP_place() {
        return p_place;
    }

    public void setP_place(String p_place) {
        this.p_place = p_place;
    }

    public String getP_expenses() {
        return p_expenses;
    }

    public void setP_expenses(String p_expenses) {
        this.p_expenses = p_expenses;
    }

    public String getSp_imgs() {
        return sp_imgs;
    }

    public void setSp_imgs(String sp_imgs) {
        this.sp_imgs = sp_imgs;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "u_userid='" + u_userid + '\'' +
                ", u_profile_img='" + u_profile_img + '\'' +
                ", p_id='" + p_id + '\'' +
                ", p_place='" + p_place + '\'' +
                ", p_expenses='" + p_expenses + '\'' +
                '}';
    }
}
