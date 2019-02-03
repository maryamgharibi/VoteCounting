package com.oracle.domain;

import java.util.ArrayList;
import java.util.List;

/*
    This class used for creating the tree structure , for keeping votes and their weights
 */
public class Node {
    private List<Node> children = new ArrayList<>();
    private String id;
    private boolean removed = false;
    private int weight = 0;
    private int ballotId = 0;


    public Node(String id) {
        this.id = id;
    }


    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }


    public void addChild(Node child) {
        children.add(child);

    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children.addAll(children);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBallotId() {
        return ballotId;
    }

    public void setBallotId(int ballotId) {
        this.ballotId = ballotId;
    }
}
