package com.papblfire.imamf.papbl_fire.Model;

public class User {
    private String nama, noTelp, id;


    public User(String nama, String noTelp, String id) {
        this.nama = nama;
        this.noTelp = noTelp;
        this.id = id;

    }

    public String getNama() {
        return nama;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getId() {
        return id;
    }

}
