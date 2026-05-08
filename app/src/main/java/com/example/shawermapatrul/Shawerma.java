package com.example.shawermapatrul;

import java.io.Serializable;

public class Shawerma implements Serializable {
    private int id;
    private String name;
    private String placeName;
    private int rating;
    private double price;
    private String address;
    private String commet;

    public Shawerma(int id, String name, String placeName, int rating, double price, String address, String commet){
        this.id = id;
        this.name = name;
        this.placeName = placeName;
        this.rating = rating;
        this.price = price;
        this.address = address;
        this.commet = commet;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlaceName() {
        return placeName;
    }

    public int getRating() {
        return rating;
    }

    public double getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public String getCommet() {
        return commet;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCommet(String commet) {
        this.commet = commet;
    }
}