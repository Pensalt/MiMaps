package com.shlompie.mimaps;

public class User {

    private boolean metric;

    public boolean isMetric() {
        return metric;
    }

    public void setMetric(boolean metric) {
        this.metric = metric;
    }

    public User(boolean metric) {
        this.metric = metric;
    }

    public User() {} // This empty constructor is needed to create an empty user object to populate with data from firebase.



}
