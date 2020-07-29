package com.example.nicolai.clider.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

//Used for converting from JSON into a java object - clothe.
public class Clothe implements Serializable{
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("url")
    @Expose
    private String imageUrl;

    @SerializedName("age")
    @Expose
    private Integer age;

    @SerializedName("location")
    @Expose
    private String location;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @SerializedName("id")
    @Expose
    private UUID id;



    @SerializedName("tag")
    @Expose
    private String tag;

    @SerializedName("webshopurl")
    @Expose
    private String webshopUrl;

    @SerializedName("sexTag")
    @Expose
    private String sexTag;

    @SerializedName("price")
    @Expose
    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSexTag() {
        return sexTag;
    }

    public void setSexTag(String sexTag) {
        this.sexTag = sexTag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebshopUrl() {
        return webshopUrl;
    }

    public void setWebshopUrl(String webshopUrl) {
        this.webshopUrl = webshopUrl;
    }
}
