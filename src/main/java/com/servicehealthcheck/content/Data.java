package com.servicehealthcheck.content;

public class Data {
    private String name1;
    private int id1;
    public Data(){
        name1 = "Hello Sai" ;
        id1 = 9 ;
    }

    public String getName1() {
        return name1;
    }

    public int getId1() {
        return id1;
    }

    public void setName1(String name) {
        this.name1 = name ;
    }

    public void setId1(int num) {
        this.id1 = num;
    }
}
