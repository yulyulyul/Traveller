package jso.kpl.traveller.model;

public class ResponseResult<T> {

    int res_type;
    String res_msg;
    T res_obj;

    public int getRes_type() {
        return res_type;
    }

    public void setRes_type(int res_type) {
        this.res_type = res_type;
    }

    public String getRes_msg() {
        return res_msg;
    }

    public void setRes_msg(String res_msg) {
        this.res_msg = res_msg;
    }

    public T getRes_obj() {
        return res_obj;
    }

    public void setRes_obj(T res_obj) {
        this.res_obj = res_obj;
    }
}