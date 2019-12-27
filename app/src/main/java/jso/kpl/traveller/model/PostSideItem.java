package jso.kpl.traveller.model;

import android.view.View;

import java.io.Serializable;

public class PostSideItem implements Serializable {

    int p_id;
    String p_comment;
    String sp_imgs;
    String p_category;
    String like_cnt;

    public PostSideItem(int p_id, String p_comment, String sp_imgs, String p_category, String like_cnt) {
        this.p_id = p_id;
        this.p_comment = p_comment;
        this.sp_imgs = sp_imgs;
        this.p_category = p_category;
        this.like_cnt = like_cnt;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getP_comment() {
        return p_comment;
    }

    public void setP_comment(String p_comment) {
        this.p_comment = p_comment;
    }

    public String getSp_imgs() {
        return sp_imgs;
    }

    public void setSp_imgs(String sp_imgs) {
        this.sp_imgs = sp_imgs;
    }

    public String getP_category() {
        return p_category;
    }

    public void setP_category(String p_category) {
        this.p_category = p_category;
    }

    public String getLike_cnt() {
        return like_cnt;
    }

    public void setLike_cnt(String like_cnt) {
        this.like_cnt = like_cnt;
    }
}
