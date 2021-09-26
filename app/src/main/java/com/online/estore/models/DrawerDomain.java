package com.online.estore.models;

public class DrawerDomain {

    int image;
    String name;

    public DrawerDomain(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
