package com.example.kate.personal_coach;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FitnessActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnDataPointListener {
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";

    private boolean authInProgress = false;
    private GoogleApiClient mClient;

    public static DatabaseReference Dlab_DB;
    private FirebaseAuth mAuth;
    static FirebaseUser user = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Dlab_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        //google fit을 사용하기 전 play service에 접속해야 함
        //GoogleApiClient instance 초기화
        //request Scope - addScope (데이터 접근 권한 동의 받기위해 )

        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes( DataType.TYPE_STEP_COUNT_CUMULATIVE,DataType.TYPE_HEART_RATE_BPM )
                .setDataSourceTypes( DataSource.TYPE_RAW )
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType() ) ) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);

        if (hasRuntimePermissions()) {
            findFitnessDataSourcesWrapper();
        } else {
            requestRuntimePermissions();
        }





    }
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        SensorRequest request = new SensorRequest.Builder()
                .setDataSource( dataSource )
                .setDataType( dataType )
                .setSamplingRate( 3, TimeUnit.SECONDS )
                .build();

        Fitness.SensorsApi.add( mClient, request, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e( "GoogleFit", "SensorApi successfully added" );
                        }
                    }
                });
    }
    private void requestRuntimePermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION);
    }
    private void findFitnessDataSourcesWrapper() {
        if (hasOAuthPermission()) {
            findFitnessDataSources();
        } else {
            requestOAuthPermission();
        }
    }
    private void findFitnessDataSources() {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                .setDataTypes(DataType.TYPE_WORKOUT_EXERCISE)
                .setDataTypes(DataType.TYPE_CYCLING_PEDALING_CUMULATIVE)
                .setDataTypes(DataType.TYPE_DISTANCE_DELTA)
                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                .setDataTypes(DataType.TYPE_ACTIVITY_SEGMENT)
                .setDataSourceTypes( DataSource.TYPE_RAW )
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType() ) ) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }else if(DataType.TYPE_STEP_COUNT_DELTA.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_DELTA);
                    }else if(DataType.TYPE_LOCATION_SAMPLE.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_LOCATION_SAMPLE);
                    }else if(DataType.TYPE_WORKOUT_EXERCISE.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_WORKOUT_EXERCISE);
                    }else if(DataType.TYPE_CYCLING_PEDALING_CUMULATIVE.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_CYCLING_PEDALING_CUMULATIVE);
                    }else if(DataType.TYPE_DISTANCE_DELTA.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_DISTANCE_DELTA);
                    }else if(DataType.TYPE_HEART_RATE_BPM.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_HEART_RATE_BPM);
                    }else if(DataType.TYPE_ACTIVITY_SEGMENT.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_ACTIVITY_SEGMENT);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);

    }

    private FitnessOptions getFitnessSignInOptions() {
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .addDataType(DataType.TYPE_LOCATION_SAMPLE)
                .addDataType(DataType.TYPE_WORKOUT_EXERCISE)
                .addDataType(DataType.TYPE_CYCLING_PEDALING_CUMULATIVE)
                .addDataType(DataType.TYPE_DISTANCE_DELTA)
                .addDataType(DataType.TYPE_HEART_RATE_BPM)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .build();
    }
    private boolean hasOAuthPermission() {
        FitnessOptions fitnessOptions = getFitnessSignInOptions();
        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions);
    }
    private boolean hasRuntimePermissions() {
        int permissionState =
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    private void requestOAuthPermission() {
        FitnessOptions fitnessOptions = getFitnessSignInOptions();
        GoogleSignIn.requestPermissions(
                this,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                findFitnessDataSources();
            }
        }
    }
    @Override
    public void onDataPoint(DataPoint dataPoint) {
        for( final Field field : dataPoint.getDataType().getFields() ) {
            final Value value = dataPoint.getValue( field );
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();

                    Log.i("FitnessActivity", "TEST" + field.getName() + ": "+value);
                    String today = getTimeStr();

                    user = mAuth.getCurrentUser();
                    Dlab_DB.child("Activity").child(user.getUid()).child(today.replace("/","")).setValue("field: " + field.getName() + " Value: " + value);

                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }
    public String getTimeStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM/dd");
        return sdfNow.format(date);
    }

}
