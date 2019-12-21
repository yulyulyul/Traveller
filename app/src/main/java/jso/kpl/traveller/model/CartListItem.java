package jso.kpl.traveller.model;

import java.util.List;

public class CartListItem {
    public int p_id;
    public String p_place;
    public String p_expenses;
    public String p_period;
    public String sp_imgs;
    public String p_category;

   // List<String> sp_imgss;
    
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

    public String getP_period() {
        return p_period;
    }

    public void setP_period(String p_period) {
        this.p_period = p_period;
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

    @Override
    public String toString() {
        return "CartListItem{" +
                "p_id=" + p_id +
                ", p_place='" + p_place + '\'' +
                ", p_expenses='" + p_expenses + '\'' +
                ", p_period='" + p_period + '\'' +
                ", sp_imgs='" + sp_imgs + '\'' +
                ", p_category='" + p_category + '\'' +
                '}';
    }
}
