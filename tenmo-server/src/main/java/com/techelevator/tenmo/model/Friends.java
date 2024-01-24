package com.techelevator.tenmo.model;

public class Friends {
    private int userA;
    private int userB;
    private boolean isConfirmed;
    private boolean isActive;

    public Friends() {}

    public Friends(int userA, int userB, boolean isConfirmed, boolean isActive) {
        this.userA = userA;
        this.userB = userB;
        this.isConfirmed = isConfirmed;
        this.isActive = isActive;
    }

    public int getUserA() {
        return userA;
    }

    public void setUserA(int userA) {
        this.userA = userA;
    }

    public int getUserB() {
        return userB;
    }

    public void setUserB(int userB) {
        this.userB = userB;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
