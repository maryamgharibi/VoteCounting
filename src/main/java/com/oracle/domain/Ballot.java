package com.oracle.domain;

/*
    this class is equal to each paper that contains candidates'votes
 */
public class Ballot {
    private String name;
    private boolean exhausted = false;

    private int id = 0;


    public Ballot(int id, String name, boolean exhausted) {
        this.name = name;
        this.exhausted = exhausted;
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExhausted() {
        return exhausted;
    }

    public void setExhausted(boolean exhausted) {
        this.exhausted = exhausted;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
