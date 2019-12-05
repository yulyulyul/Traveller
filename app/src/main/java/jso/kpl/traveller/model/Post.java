package jso.kpl.traveller.model;

import android.view.View;

import androidx.annotation.BoolRes;

import java.util.ArrayList;
import java.util.List;

public class Post {

    int p_id;
    String p_author;
    String p_place;
    String p_expenses;
    String p_comment;
    String p_start_date;
    String p_end_date;
    boolean p_is_open;

    List<String> p_category = new ArrayList<>();
    List<SmallPost> p_sp_list = new ArrayList<>();

    boolean p_is_like;
    boolean p_is_cart;

    public Post() {

    }

    public Post(String p_author, String p_place, boolean p_is_open) {
        this.p_author = p_author;
        this.p_place = p_place;
        this.p_is_open = p_is_open;
    }

    public Post(int p_id, String p_author, String p_place, String p_expenses, String p_comment, List<String> p_category, String p_start_date, String p_end_date, boolean p_is_open, List<SmallPost> p_sp_list) {
        this.p_id = p_id;
        this.p_author = p_author;
        this.p_place = p_place;
        this.p_expenses = p_expenses;
        this.p_comment = p_comment;
        this.p_category = p_category;
        this.p_start_date = p_start_date;
        this.p_end_date = p_end_date;
        this.p_is_open = p_is_open;
        this.p_sp_list = p_sp_list;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getP_author() {
        return p_author;
    }

    public void setP_author(String p_author) {
        this.p_author = p_author;
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

    public String getP_comment() {
        return p_comment;
    }

    public void setP_comment(String p_comment) {
        this.p_comment = p_comment;
    }

    public List<String> getP_category() {
        return p_category;
    }

    public void setP_category(List<String> p_category) {
        this.p_category = p_category;
    }

    public String getP_start_date() {
        return p_start_date;
    }

    public void setP_start_date(String p_start_date) {
        this.p_start_date = p_start_date;
    }

    public String getP_end_date() {
        return p_end_date;
    }

    public void setP_end_date(String p_end_date) {
        this.p_end_date = p_end_date;
    }

    public boolean isP_is_open() {
        return p_is_open;
    }

    public void setP_is_open(boolean p_is_open) {
        this.p_is_open = p_is_open;
    }

    public List<SmallPost> getP_sp_list() {
        return p_sp_list;
    }

    public void setP_sp_list(List<SmallPost> p_sp_list) {
        this.p_sp_list = p_sp_list;
    }

    public boolean isP_is_like() {
        return p_is_like;
    }

    public void setP_is_like(boolean p_is_like) {
        this.p_is_like = p_is_like;
    }

    public boolean isP_is_cart() {
        return p_is_cart;
    }

    public void setP_is_cart(boolean p_is_cart) {
        this.p_is_cart = p_is_cart;
    }

    @Override
    public String toString() {
        return "Post{" +
                "p_id=" + p_id +
                ", p_author='" + p_author + '\'' +
                ", p_place='" + p_place + '\'' +
                ", p_expenses='" + p_expenses + '\'' +
                ", p_comment='" + p_comment + '\'' +
                ", p_category='" + p_category + '\'' +
                ", p_start_date='" + p_start_date + '\'' +
                ", p_end_date='" + p_end_date + '\'' +
                ", p_is_open=" + p_is_open +
                ", p_sp_list=" + p_sp_list +
                '}';
    }
}
