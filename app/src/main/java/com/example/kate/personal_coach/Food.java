package com.example.kate.personal_coach;

/**
 * Created by SeoYeon Choi on 2018-04-30.
 */

public class Food {
    private int No;
    private String Name;
    private double Kcal;
    private String Group;

    public Food() {}

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        this.No = no;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public double getKcal() {
        return Kcal;
    }

    public void setKcal(double kcal) {
        this.Kcal = kcal;
    }

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
    }

    @Override
    public String toString() {
        return Name;
    }
}
