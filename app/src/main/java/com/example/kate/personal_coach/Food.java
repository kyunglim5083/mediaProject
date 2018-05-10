package com.example.kate.personal_coach;

/**
 * Created by SeoYeon Choi on 2018-04-30.
 */

public class Food {
    private int No;
    private String Name;
    private double Kcal;

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

    @Override
    public String toString() {
        return Name;
    }
}
