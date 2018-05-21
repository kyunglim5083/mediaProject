package com.example.kate.personal_coach;

/**
 * Created by imsoyeong on 2018. 5. 7..
 */

public class ActivityVO {
    String type;
    String startTime;
    String endTime;
    String field;
    String value;

    public ActivityVO() {
    }

    public ActivityVO(String type, String startTime, String endTime, String field, String value) {
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.field = field;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ActivityVO{" +
                "type='" + type + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", field='" + field + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

