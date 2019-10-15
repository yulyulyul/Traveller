package jso.kpl.traveller.model;

public class CountryFlag {

    int cf_no;
    int cf_name;
    int cf_img;

    public CountryFlag(int cf_no, int cf_name, int cf_img) {
        this.cf_no = cf_no;
        this.cf_name = cf_name;
        this.cf_img = cf_img;
    }

    public int getCf_no() {
        return cf_no;
    }

    public void setCf_no(int cf_no) {
        this.cf_no = cf_no;
    }

    public int getCf_name() {
        return cf_name;
    }

    public void setCf_name(int cf_name) {
        this.cf_name = cf_name;
    }

    public int getCf_img() {
        return cf_img;
    }

    public void setCf_img(int cf_img) {
        this.cf_img = cf_img;
    }
}
