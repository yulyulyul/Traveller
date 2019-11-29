package jso.kpl.traveller.model;

import java.io.Serializable;

public class MyPageItem implements Serializable {

    Object o;
    int type;

    public MyPageItem(Object o, int type) {
        this.o = o;
        this.type = type;
    }

    public Object getO() {
        return o;
    }

    public void setO(Object o) {
        this.o = o;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MyPageItem{" +
                "o=" + o +
                ", type=" + type +
                '}';
    }
}
