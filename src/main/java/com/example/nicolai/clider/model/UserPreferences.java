package com.example.nicolai.clider.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



//Data object for user preferences, for sending to firebase
public class UserPreferences {

    public UserPreferences(int age, String sex, ArrayList<String> tags) {
        this.age = age;
        this.sex = sex;
        this.tags = tags;
    }



    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public int age;
    public String sex;
    public ArrayList<String>tags;

    public UserPreferences() {
    }

}
