package com.example.knowyourgovernment;

import java.io.Serializable;

public class Official implements Serializable {

    private String name;
    private String title;
    private String party;
    private String address;
    private String phone;
    private String url;
    private String email;
    private String photoURL;
    private String facebook;
    private String twitter;
    private String youtube;

    public Official() {
        this.name = "";
        this.title = "";
        this.party = "";
        this.address = "";
        this.phone = "";
        this.url = "";
        this.email = "";
        this.photoURL = "";
        this.facebook = "";
        this.twitter = "";
        this.youtube = "";
    }

    public Official(String name, String title, String party, String address, String phone, String url, String email, String photoURL, String facebook, String twitter, String youtube) {
        this.name = name;
        this.title = title;
        this.party = party;
        this.address = address;
        this.phone = phone;
        this.url = url;
        this.email = email;
        this.photoURL = photoURL;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youtube = youtube;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }
}
