package com.example.kate.personal_coach;

/**
 * Created by kate on 2018. 3. 28..
 */

public class User {
    public String name;
    public String email;//id
    public int height;
    public int weight;
    public int age;
    public String sex; //남,녀로입력

    public User(){}
    public User(String email,String name,String sex,int age,int height,int weight){
        this.email=email;
        this.name=name;
        this.sex=sex;
        this.height=height;
        this.weight=weight;
        this.age=age;
    }
}
