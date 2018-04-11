package com.example.kate.personal_coach;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BloodActivity extends AppCompatActivity {

    public static DatabaseReference Dlab_DB;
    private FirebaseAuth mAuth;
    static FirebaseUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);

        Dlab_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        final EditText bloodInput = (EditText)findViewById(R.id.bloodInput);
        Button sendButton = (Button)findViewById(R.id.sendButton);

        final String bloodTime;
        final String blood = "";
        Spinner s = (Spinner) findViewById(R.id.spinner);

        final Blood b = new Blood();
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object itemAtPosition = parent.getItemAtPosition(position);
                b.setType(itemAtPosition.toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.setTime(getTimeStr());
                b.setBlood_data(Integer.parseInt(bloodInput.getText().toString()));
                user = mAuth.getCurrentUser();
                Dlab_DB.child("Blood").child(user.getUid()).child(getTimeStr().replace("/", "")).setValue(b);

                Toast.makeText(BloodActivity.this, "혈당입력완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BloodActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
    public String getTimeStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM-dd HH:mm:ss");
        return sdfNow.format(date);
    }

}
