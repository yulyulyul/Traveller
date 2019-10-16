package jso.kpl.traveller.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

public class RouteOtherDetailVO implements Serializable {
    @NotNull
    private int idx; // index
    private ArrayList<String> category; // 카테고리
    private String period; // 기간
    private String cost; // 비용
    private String author; // 작성자
    private String text; // 작성자 코멘트

    public RouteOtherDetailVO(int idx, ArrayList<String> category, String period, String cost, String author, String text) {
        this.idx = idx;
        this.category = category;
        this.period = period;
        this.cost = cost;
        this.author = author;
        this.text = text;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
