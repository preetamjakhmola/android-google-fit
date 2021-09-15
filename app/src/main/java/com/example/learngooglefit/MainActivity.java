package com.example.learngooglefit;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements IStepCounter {
    private static final String TAG = "FitActivity";
    private GoogleApiClient mClient = null;
    private OnDataPointListener mListener;
    TextView steps;
    TextView historysteps;

    FitnessOptions fitnessOptions;

    GoogleSignInAccount googleSignInAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FitActivity fitActivity = new FitActivity(this);
        fitActivity.checkPermision();
        steps = findViewById(R.id.textview_number_of_steps);
        historysteps = findViewById(R.id.steps);
        //StartSensor();

    }

    @Override
    public FitnessOptions getFitActivity() {
        if (fitnessOptions == null)
            fitnessOptions = FitnessOptions.builder()
                    .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                    .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_WRITE)
                    .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                    .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                    .build();
        return fitnessOptions;
    }

    @Override
    public GoogleSignInAccount getGoogleAccount() {
        if (googleSignInAccount == null)
            googleSignInAccount = GoogleSignIn.getAccountForExtension(this, getFitActivity());
        return googleSignInAccount;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String ss = "";
        StartSensor();
    }

    @Override
    public void StartSensor() {
        //googleSignInAccount =_googleSignInAccount;

        DataType dataType = DataType.TYPE_STEP_COUNT_DELTA;

        Fitness.getHistoryClient(this, googleSignInAccount)

                .readDailyTotalFromLocalDevice(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(dataSource -> {
                    //derived:com.google.step_count.delta:com.google.android.gms:aggregated
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    DateFormat timeFormat = DateFormat.getTimeInstance();

                    for (DataPoint dp : dataSource.getDataPoints()) {

                        String type = dp.getDataType().getName();
                        String tStart = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                        String End = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));

                        for (Field field : dp.getDataType().getFields()) {
                            //Log.e("History", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                            String sfield = field.getName();
                            Value svalue = dp.getValue(field);

                            String ss="";
                        }
                    }

                    String totalSteps = !dataSource.isEmpty() ?
                            dataSource.getDataPoints().get(0).getValue(Field.FIELD_STEPS).toString() : "0";
                    // historysteps.setText(totalSteps);
                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "There was a problem getting steps.", e);
                });

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();

        cal.set(cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(cal.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long startTime = cal.getTimeInMillis();
       /* ZonedDateTime startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());//cal.getTimeInMillis();
        ZonedDateTime endTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
*/

        DataSource datasource = new DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                //.setDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(datasource, dataType)
                //.aggregate(datasource,DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, googleSignInAccount)

                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {

                    int strs = 0;
                    List<String> list = new ArrayList<String>();
                    List<String> agsteps = new ArrayList<String>();
                    String totalSteps = "0";

                    List<Bucket> bucketList = dataReadResponse.getBuckets();

                    for (Bucket bucket : bucketList) {

                        DataSet stepsDs = bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);
                        DataSet caloriesDataSet = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);
                        totalSteps = stepsDs.getDataPoints().isEmpty() ? "0" : stepsDs.getDataPoints().get(0).getValue(Field.FIELD_STEPS).toString();
                        list.add(totalSteps);
                        agsteps.add(caloriesDataSet.getDataPoints().isEmpty() ? "0" : caloriesDataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).toString());
                    }
                    historysteps.setText(totalSteps);
                    String ss = "";
                    /*String totalSteps = !dataSource.getBuckets().isEmpty() ?
                            dataSource.getBuckets().get(0)..getDataPoints().get(0).getValue(Field.FIELD_STEPS).toString() : "0";
                    historysteps.setText(totalSteps);*/
                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "There was a problem getting steps.", e);
                });


        Fitness.getRecordingClient(this, googleSignInAccount)
                // This example shows subscribing to a DataType, across all possible
                // data sources. Alternatively, a specific DataSource can be used.
                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(unused ->
                        Log.i(TAG, "Successfully subscribed!"))
                .addOnFailureListener(e ->
                        Log.w(TAG, "There was a problem subscribing.", e));


        Fitness.getRecordingClient(this, googleSignInAccount)
                .listSubscriptions()
                .addOnSuccessListener(subscriptions -> {
                    for (Subscription sc : subscriptions) {
                        DataType dt = sc.getDataType();

                        Log.i(TAG, "Active subscription for data type: ${dt.name}");
                    }
                });


        Fitness.getSensorsClient(this, googleSignInAccount)
                .findDataSources(
                        new DataSourcesRequest.Builder()
                                .setDataTypes(dataType)
                                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                                .build())

                .addOnSuccessListener(dataSources -> {
                    try {

                        for (DataSource dataSource : dataSources) {
                            Log.i(TAG, "Data source found:");
                            Log.i(TAG, "Data Source type: ");
                            String sss = dataSource.getStreamIdentifier();
                            String name = dataSource.getDataType().getName();
                            if (dataSource.getDataType().equals(dataType)) {
                                Log.i(TAG, "Data source for STEP_COUNT_DELTA found!");
                                registerFitnessDataListener(dataSource, dataType);
                            }
                        }
                    } catch (Exception ex) {
                        String es = ex.getMessage();
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Find data sources request failed", e));


    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {

        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);

                    // Log.i(TAG, "Difference in steps: " + (val.asInt()-previousValue));

                    String previousValue = String.valueOf(Integer.parseInt(historysteps.getText().toString()) + val.asInt());
                    steps.setText(previousValue);
                    historysteps.setText(previousValue);
                }
            }
        };

        Fitness.getSensorsClient(this, googleSignInAccount)
                .add(
                        new SensorRequest.Builder()
                                .setDataSource(dataSource)
                                .setDataType(dataType)
                                .setSamplingRate(2, TimeUnit.SECONDS)  // sample once per minute
                                .build(),
                        mListener)
                .addOnSuccessListener(unused ->
                        Log.i(TAG, "Listener registered!"))
                .addOnFailureListener(task ->
                        Log.e(TAG, "Listener not registered.", task.getCause()));

    }

}

