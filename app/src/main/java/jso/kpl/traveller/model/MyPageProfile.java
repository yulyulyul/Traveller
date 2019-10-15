package jso.kpl.traveller.model;

public class MyPageProfile {

    String profileUri;
    String email;
    String nickname;
    String password;
    String device_id;

    public MyPageProfile(String profileUri, String email) {
        this.profileUri = profileUri;
        this.email = email;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "MyPageProfile{" +
                "profileUri='" + profileUri + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
