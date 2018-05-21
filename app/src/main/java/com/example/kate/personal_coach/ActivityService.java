package com.example.kate.personal_coach;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActivityService extends Service  {

    private boolean authInProgress = false;
    private static final String AUTH_PENDING = "auth_state_pending";
    public static DatabaseReference Dlab_DB;
    private static FirebaseAuth mAuth;
    static FirebaseUser user = null;

    public ActivityService() {
        Dlab_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

        //Service 객체와 화면단 Activity에서 객체 주고받을 때 사용하는 메서드

    }
    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)

        Log.d("test", "서비스의 onCreate");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d("test", "서비스의 onStartCommand");

        recordData();
        readData();



//        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                .subscribe(DataType.TYPE_ACTIVITY_SAMPLES)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.i("onSuccess", "Successfully subscribed!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i("onFailure", "There was a problem subscribing.");
//                        Log.i("Exception", "ee" +e);
//                    }
//                });
//
//        OnDataPointListener mListener =
//                new OnDataPointListener() {
//                    @Override
//                    public void onDataPoint(DataPoint dataPoint) {
//                        for (Field field : dataPoint.getDataType().getFields()) {
//                            Value val = dataPoint.getValue(field);
//                            Log.i("Field Name", "Detected DataPoint field: " + field.getName());
//                            Log.i("Field Value", "Detected DataPoint value: " + val);
//                        }
//                    }
//                };
//
//        Fitness.getSensorsClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                .add(
//                        new SensorRequest.Builder()
//                                .setDataType(DataType.TYPE_STEP_COUNT_DELTA) // Can't be omitted.
//                                .setDataType(DataType.TYPE_ACTIVITY_SAMPLES)
//                                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
//                                .setSamplingRate(10, TimeUnit.SECONDS)
//                                .build(),
//                        mListener)
//                .addOnCompleteListener(
//                        new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.i("Listener Registered", "Listener registered!");
//                                } else {
//                                    Log.e("Listener not Registered", "Listener not registered.", task.getException());
//                                }
//                            }
//                        });
//
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        long endTime = cal.getTimeInMillis();
//
//        cal.setTime(now);
//        cal.set(Calendar.MILLISECOND, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.HOUR, 0);
//
//        long startTime = cal.getTimeInMillis();
//
//        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Log.i("STARTTime", "Range Start: " + dateFormat.format(startTime));
//        Log.i("ENTTime", "Range End: " + dateFormat.format(endTime));
//
//        DataReadRequest readRequest =
//                new DataReadRequest.Builder()
//                        // The data request can specify multiple data types to return, effectively
//                        // combining multiple data queries into one call.
//                        // In this example, it's very unlikely that the request is for several hundred
//                        // datapoints each consisting of a few steps and a timestamp.  The more likely
//                        // scenario is wanting to see how many steps were walked per day, for 7 days.
//                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
//                        .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
//                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
//                        // bucketByTime allows for a time span, whereas bucketBySession would allow
//                        // bucketing by "sessions", which would need to be defined in code.
//                        .bucketByTime(1, TimeUnit.SECONDS)
//                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//                        .build();
//
//
//        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
//                .readData(readRequest)
//                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
//                    @Override
//                    public void onSuccess(DataReadResponse dataReadResponse) {
//                        Log.d("Success^^^", "onSuccess()");
//                        List<DataSet> dataSets = dataReadResponse.getDataSets();
//
//                        for(DataSet dataset : dataSets){
//
//                            //dumpDataSet(dataset);
//                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                            Dlab_DB = FirebaseDatabase.getInstance().getReference();
//                            mAuth = FirebaseAuth.getInstance();
//                            user = mAuth.getCurrentUser();
//                            for (DataPoint dp : dataset.getDataPoints()) {
//
//
//                                Log.i("point", "Data point:");
//                                Log.i("DataType", "\tType: " + dp.getDataType().getName());
//                                Log.i("StartTime", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
//                                Log.i("EndTime", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
//
//                                Toast.makeText(ActivityService.this, "Data"+dp.getDataType().getName(), Toast.LENGTH_SHORT).show();
//                                for (Field field : dp.getDataType().getFields()) {
//                                    Log.i("Field", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
//
//
//                                    ActivityVO avo = new ActivityVO();
//                                    avo.setType(dp.getDataType().getName());
//                                    avo.setStartTime(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
//                                    avo.setEndTime(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
//                                    avo.setField(field.getName());
//                                    avo.setValue(dp.getValue(field).toString());
//
//                                    Toast.makeText(ActivityService.this, "avo" + avo.toString(),Toast.LENGTH_LONG).show();
//                                    Dlab_DB.child("Activity").child(user.getUid()).child(getDateStr()).child(getTimeStr()).setValue(avo);
//
//                                }
//                            }
//
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("Fail^^", "onFailure()", e);
//                    }
//                })
//                .addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        Log.d("Complete^^", "onComplete()");
//                    }
//                });
//

        return super.onStartCommand(intent, flags, startId);
    }

    private static void dumpDataSet(DataSet dataSet) {
        Log.i("dumpDataSet", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Dlab_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        for (DataPoint dp : dataSet.getDataPoints()) {


            Log.i("point", "Data point:");
            Log.i("DataType", "\tType: " + dp.getDataType().getName());
            Log.i("StartTime", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i("EndTime", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i("Field", "\tField: " + field.getName() + " Value: " + dp.getValue(field));


                ActivityVO avo = new ActivityVO();
                avo.setType(dp.getDataType().getName());
                avo.setStartTime(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                avo.setEndTime(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                avo.setField(field.getName());
                avo.setValue(dp.getValue(field).toString());


                Dlab_DB.child("Activity").child(user.getUid()).child(getDateStr()).child(getTimeStr()).setValue(avo);

            }
        }


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행

        Log.d("test", "서비스의 onDestroy");
    }

    public void recordData(){
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("onSuccess", "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("onFailure", "There was a problem subscribing.");
                    }
                });


        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_ACTIVITY_SEGMENT)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("onSuccess", "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("onFailure", "There was a problem subscribing.");
                    }
                });

        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("onSuccess", "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("onFailure", "There was a problem subscribing.");
                    }
                });

    }

    public void readData(){
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();

        cal.setTime(now);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);

        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Log.i("STARTTime", "Range Start: " + dateFormat.format(startTime));
        Log.i("ENTTime", "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                        .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        //.read(DataType.TYPE_STEP_COUNT_CADENCE )
                        //.read(DataType.TYPE_ACTIVITY_SEGMENT)
                        //.read(DataType.TYPE_CALORIES_EXPENDED)
                        .bucketByTime(1, TimeUnit.SECONDS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();


        Log.i("BeforeTry", "Before");

        try{
            Task<DataReadResponse> response = Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(getApplicationContext()))
                    .readData(readRequest)
                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                        @Override
                        public void onSuccess(DataReadResponse dataReadResponse) {
                            List<DataSet> dataSets = dataReadResponse.getDataSets();
                            Log.d("Success", "dataReadResponse" + dataReadResponse.getDataSets());
                            for(DataSet dataset : dataSets){

                                //dumpDataSet(dataset);
                                Dlab_DB = FirebaseDatabase.getInstance().getReference();
                                mAuth = FirebaseAuth.getInstance();
                                user = mAuth.getCurrentUser();
                                java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                                for (DataPoint dp : dataset.getDataPoints()) {


                                    Log.i("point", "Data point:");
                                    Log.i("DataType", "\tType: " + dp.getDataType().getName());
                                    Log.i("StartTime", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                                    Log.i("EndTime", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));

                                    Toast.makeText(ActivityService.this, "Data"+dp.getDataType().getName(), Toast.LENGTH_SHORT).show();
                                    for (Field field : dp.getDataType().getFields()) {
                                        Log.i("Field", "\tField: " + field.getName() + " Value: " + dp.getValue(field));


                                        ActivityVO avo = new ActivityVO();
                                        avo.setType(dp.getDataType().getName());
                                        avo.setStartTime(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                                        avo.setEndTime(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                                        avo.setField(field.getName());
                                        avo.setValue(dp.getValue(field).toString());

                                        Toast.makeText(ActivityService.this, "avo" + avo.toString(),Toast.LENGTH_LONG).show();
                                        Dlab_DB.child("Activity").child(user.getUid()).child(getDateStr()).child(getTimeStr()).setValue(avo);

                                    }
                                }

                            }

                        }
                    });





        }catch(Exception e){
            Log.i("Exception%%%%", "e" + e);
        }

    }
    public static String getDateStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");

        return sdfNow.format(date);
    }

    public static String getTimeStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss");
        return sdfNow.format(date);
    }





}
