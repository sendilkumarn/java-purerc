package com.sendilkumarn.domain;

import com.sendilkumarn.impl.Concurrent;

import java.util.ArrayList;
import java.util.HashMap;

public class GCObject {
    private String name;
    private int rc;
    private int crc;
    private Color color;
    private boolean buffered;
    private ArrayList<GCObject> children;
    private boolean freed;

    public GCObject(String name) {
        this.name = name;
        this.rc = 0;
        this.crc = 0;
        this.color = Color.BLACK;
        this.children = new ArrayList<>();
        Concurrent.count++;

        System.out.println("Creating new object "+ name);
    }

    @Override
    public String toString() {
        return "GCObject{" +
                "count=" + Concurrent.count +
                ", name='" + name + '\'' +
                ", rc=" + rc +
                ", crc=" + crc +
                ", color=" + color +
                ", buffered=" + buffered +
                ", freed=" + freed +
                ", acyclic=" + isAcyclic() +
                '}';
    }

    public void printChild() {
        for (GCObject o : this.children) {
            System.out.println(o.toString());
        }
    }

    public boolean isAcyclic() {
        return !this.cyclesTo(this, new HashMap<>());
    }

    private boolean cyclesTo(GCObject gcObject, HashMap<GCObject, Boolean> except) {
        if (except.get(this) == Boolean.TRUE) {
            return false;
        }
        except.put(this, Boolean.TRUE);
        if(this.children != null && this.children.size() != 0) {
            for (GCObject child : this.children) {
                if (gcObject.equals(child) || child.cyclesTo(gcObject, except)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void add(GCObject gcObject) {
        Concurrent.increment(gcObject);
        this.children.add(gcObject);
    }

    public void remove(GCObject gcObject) {
        int index = -1;
        this.children.removeIf(o -> o.getName().equals(gcObject.name));
        Concurrent.decrement(gcObject);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public int getCrc() {
        return crc;
    }

    public void setCrc(int crc) {
        this.crc = crc;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isBuffered() {
        return buffered;
    }

    public void setBuffered(boolean buffered) {
        this.buffered = buffered;
    }

    public ArrayList<GCObject> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<GCObject> children) {
        this.children = children;
    }

    public boolean isFreed() {
        return freed;
    }

    public void setFreed(boolean freed) {
        this.freed = freed;
    }
}
