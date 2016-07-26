package com.finalproject.yourdoctor.servicehandler;

/**
 * Created by Prasaanth on 06-02-2015.
 */
public class Location {

    private String name;

    public Location(){}

    public Location(String name){
        this.name = name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
