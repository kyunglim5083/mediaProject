package com.example.kate.personal_coach;

import java.util.HashMap;

/**
 * Created by imsoyeong on 2018. 5. 22..
 */

public class AnalysisVO {
    int blood_dif;
    String bloodType;
    String foodType;
    String food;
    int steps;
    float calories;

    HashMap<String, Long> activity;

    public AnalysisVO() {

    }

    public AnalysisVO(int blood_dif, String bloodType, String foodType, String food, int steps, float calories, HashMap<String, Long> activity) {
        this.blood_dif = blood_dif;
        this.bloodType = bloodType;
        this.foodType = foodType;
        this.food = food;
        this.steps = steps;
        this.calories = calories;
        this.activity = activity;
    }

    public int getBlood_dif() {
        return blood_dif;
    }

    public void setBlood_dif(int blood_dif) {
        this.blood_dif = blood_dif;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public HashMap<String, Long> getActivity() {
        return activity;
    }

    public void setActivity(HashMap<String, Long> activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "AnalysisVO{" +
                "blood_dif=" + blood_dif +
                ", bloodType='" + bloodType + '\'' +
                ", foodType='" + foodType + '\'' +
                ", food='" + food + '\'' +
                ", steps=" + steps +
                ", calories=" + calories +
                ", activity=" + activity +
                '}';
    }
}
