package com.online.estore.models;

public class UserDomain {

    String name;
    String phone;
    String address;
    String pincode;
    String landmark;
    String password;

    public UserDomain(String name, String phone, String address, String pincode, String landmark, String password) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.pincode = pincode;
        this.landmark = landmark;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getPincode() {
        return pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "UserDomain{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", pincode='" + pincode + '\'' +
                ", landmark='" + landmark + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
