package com.example.kate.personal_coach;

/**
 * Created by imsoyeong on 2018. 5. 7..
 */

public class ActivityVO {
    String time;
    int step;
    float kcal;

    public ActivityVO() {
    }

    public ActivityVO(String time, int step, float kcal) {
        this.time = time;
        this.step = step;
        this.kcal = kcal;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal(float kcal) {
        this.kcal = kcal;
    }

    @Override
    public String toString() {
        return "ActivityVO{" +
                "time='" + time + '\'' +
                ", step=" + step +
                ", kcal=" + kcal +
                '}';
    }
}

