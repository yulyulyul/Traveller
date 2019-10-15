package jso.kpl.traveller.model;

public class Post {

    int p_id;
    String p_title;
    String p_author;
    String p_cost;
    String p_country;

    public Post(int p_id, String p_title, String p_author, String p_cost, String p_country) {
        this.p_id = p_id;
        this.p_title = p_title;
        this.p_author = p_author;
        this.p_cost = p_cost;
        this.p_country = p_country;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getP_title() {
        return p_title;
    }

    public void setP_title(String p_title) {
        this.p_title = p_title;
    }

    public String getP_author() {
        return p_author;
    }

    public void setP_author(String p_author) {
        this.p_author = p_author;
    }

    public String getP_cost() {
        return p_cost;
    }

    public void setP_cost(String p_cost) {
        this.p_cost = p_cost;
    }

    public String getP_country() {
        return p_country;
    }

    public void setP_country(String p_country) {
        this.p_country = p_country;
    }

    @Override
    public String toString() {
        return "Post{" +
                "p_id=" + p_id +
                ", p_title='" + p_title + '\'' +
                ", p_author='" + p_author + '\'' +
                ", p_cost='" + p_cost + '\'' +
                ", p_country='" + p_country + '\'' +
                '}';
    }
}
