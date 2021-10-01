package com.shiqh.elasticsearch.demo.model;

/**
 * @author shiqianghai
 * @date 2021/10/1 13:38
 */
public class User {

    private String name;

    private String gender;

    private String address;

    public User() {
    }

    public User(String name, String gender, String address) {
        this.name = name;
        this.gender = gender;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
