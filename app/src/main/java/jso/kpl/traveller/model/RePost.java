package jso.kpl.traveller.model;

public class RePost {

    String rp_profile;
    Post post;

    public RePost(String rp_profile, Post post) {
        this.rp_profile = rp_profile;
        this.post = post;
    }

    public String getRp_profile() {
        return rp_profile;
    }

    public void setRp_profile(String rp_profile) {
        this.rp_profile = rp_profile;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
