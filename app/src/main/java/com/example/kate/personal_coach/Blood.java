package com.example.kate.personal_coach;

import android.text.Editable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by imsoyeong on 2018. 4. 7..
 */

public class Blood {


    public String time;
    public String type;
    public int blood_data;

    public Blood(){}

    public Blood(String time, String type, int blood_data) {
        this.time = time;
        this.type = type;
        this.blood_data = blood_data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBlood_data() {
        return blood_data;
    }

    public void setBlood_data(int blood_data) {
        this.blood_data = blood_data;
    }
    // Get a reference to our posts


    @Override
    public String toString() {
        return "Blood{" +
                "time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", blood_data=" + blood_data +
                '}';
    }
}
