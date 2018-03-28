package com.example.kate.personal_coach;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by kate on 2018. 3. 29..
 */

public class GetUserInfo extends AppCompatActivity{

    EditText sex,age,height,weight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Button button;

        button=(Button)findViewById(R.id.done);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                sex=(EditText)findViewById(R.id.sex);
                age=(EditText)findViewById(R.id.age);
                height=(EditText)findViewById(R.id.height);
                weight=(EditText)findViewById(R.id.weight);
                Intent intent = new Intent();
                intent.putExtra("sex", sex.getText().toString());
                intent.putExtra("age", age.getText().toString());
                intent.putExtra("height", height.getText().toString());
                intent.putExtra("weight", weight.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }


}
