package jso.kpl.traveller.model;

import android.net.Uri;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MultipartBody;

public class SmallPost implements Serializable {

    int sp_id;
    String sp_comment;
    String sp_place;
    String sp_expenses;
    String sp_start_date;
    String sp_end_date;
    List<String> sp_imgs;
    List<String> sp_category;

//    ArrayList<MultipartBody.Part> sp_img_body = new ArrayList<>();

    public SmallPost() {
    }

    public SmallPost(@NotNull String sp_place, @Nullable String sp_expenses) {
        this.sp_place = sp_place;
        this.sp_expenses = sp_expenses;
    }

    public SmallPost(@NotNull int sp_id, @NotNull String sp_place, @Nullable String sp_expenses) {
        this.sp_id = sp_id;
        this.sp_place = sp_place;
        this.sp_expenses = sp_expenses;
    }

    public SmallPost(@Nullable String sp_comment, @Nullable String sp_place, @Nullable String sp_expenses, @Nullable String sp_start_date, @Nullable String sp_end_date, @Nullable List<String> sp_category, @Nullable List<String> sp_imgs) {
        this.sp_comment = sp_comment;
        this.sp_place = sp_place;
        this.sp_expenses = sp_expenses;
        this.sp_start_date = sp_start_date;
        this.sp_end_date = sp_end_date;
        this.sp_category = sp_category;
        this.sp_imgs = sp_imgs;
    }

    public int getSp_id() {
        return sp_id;
    }

    public void setSp_id(int sp_id) {
        this.sp_id = sp_id;
    }

    public String getSp_comment() {
        return sp_comment;
    }

    public void setSp_comment(String sp_comment) {
        this.sp_comment = sp_comment;
    }

    public String getSp_place() {
        return sp_place;
    }

    public void setSp_place(String sp_place) {
        this.sp_place = sp_place;
    }

    public String getSp_expenses() {
        return sp_expenses;
    }

    public void setSp_expenses(String sp_expenses) {
        this.sp_expenses = sp_expenses;
    }

    public String getSp_start_date() {
        return sp_start_date;
    }

    public void setSp_start_date(String sp_start_date) {
        this.sp_start_date = sp_start_date;
    }

    public String getSp_end_date() {
        return sp_end_date;
    }

    public void setSp_end_date(String sp_end_date) {
        this.sp_end_date = sp_end_date;
    }

    public List<String> getSp_category() {
        return sp_category;
    }

    public void setSp_category(List<String> sp_category) {
        this.sp_category = sp_category;
    }

    public List<String> getSp_imgs() {
        return sp_imgs;
    }

    public void setSp_imgs(List<String> sp_imgs) {
        this.sp_imgs = sp_imgs;
    }

//    public ArrayList<MultipartBody.Part> getSp_img_body() {
//        return sp_img_body;
//    }
//
//    public void setSp_img_body(ArrayList<MultipartBody.Part> sp_img_body) {
//        this.sp_img_body = sp_img_body;
//    }

    @Override
    public String toString() {
        return "SmallPost{" +
                "sp_id=" + sp_id +
                ", sp_comment='" + sp_comment + '\'' +
                ", sp_place='" + sp_place + '\'' +
                ", sp_expenses='" + sp_expenses + '\'' +
                ", sp_start_date='" + sp_start_date + '\'' +
                ", sp_end_date='" + sp_end_date + '\'' +
                ", sp_category=" + sp_category +
                ", sp_imgs=" + sp_imgs +
                '}';
    }
}
