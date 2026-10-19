package com.example.pm1ea0285.model;

import java.io.Serializable;

public class Contact implements Serializable {
    private int id;
    private String name;
    private String countryCode;
    private String phone;
    private String note;
    private String photoPath;

    public Contact() {}

    public Contact(int id, String name, String countryCode, String phone, String note, String photoPath) {
        this.id = id;
        this.name = name;
        this.countryCode = countryCode;
        this.phone = phone;
        this.note = note;
        this.photoPath = photoPath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getFullPhoneNumber() {
        return countryCode + " " + phone;
    }
}
