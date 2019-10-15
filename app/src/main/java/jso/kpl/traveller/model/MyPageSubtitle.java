package jso.kpl.traveller.model;

public class MyPageSubtitle {

    int type;
    String subtitle;
    boolean isVisible;

    public MyPageSubtitle(int type, String subtitle, boolean isVisible) {
        this.type = type;
        this.subtitle = subtitle;
        this.isVisible = isVisible;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
