package com.example.learngooglefit;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

//import com.fithjoyapp.googlefittest.R;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.*; //DataPoint;
/*import com.google.android.gms.fitness.DataSource;
import com.google.android.gms.fitness.DataSourceListener;
import com.google.android.gms.fitness.DataSourcesRequest;
import com.google.android.gms.fitness.DataSourcesResult;
import com.google.android.gms.fitness.DataType;
import com.google.android.gms.fitness.DataTypes;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessScopes;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.SensorRequest;
import com.google.android.gms.fitness.Value;*/
import java.util.concurrent.TimeUnit;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.DataType;

enum FitActionRequestCode {
    SUBSCRIBE,
    READ_DATA
}

public class FitActivity {

    Context _this;
    private FitnessOptions fitnessOptions;
    private IStepCounter _stepCounter;
    GoogleSignInAccount googleSignInAccount;
    public FitActivity(Context context){ //,FitnessOptions _fitnessOptions,GoogleSignInAccount _googleSignInAccount) {
        _this = context;
        _stepCounter = (IStepCounter)context;

        fitnessOptions =_stepCounter.getFitActivity();//_fitnessOptions;
        googleSignInAccount=_stepCounter.getGoogleAccount(); //_googleSignInAccount;
                /*FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();*/
    }

    public void checkPermision() {
        checkPermissionsAndRun(FitActionRequestCode.SUBSCRIBE);
    }

    private void checkPermissionsAndRun(FitActionRequestCode fitActionRequestCode) {
        if (permissionApproved()) {
            fitSignIn(fitActionRequestCode);
            String ss = "";
            _stepCounter.StartSensor();
            //TODO after permission approved
        } else {

            requestRuntimePermissions(fitActionRequestCode);

        }
    }

    private void fitSignIn(FitActionRequestCode requestCode) {
        if (this.oAuthPermissionsApproved()) {
            this.performActionForRequestCode(requestCode);
        } else {
            GoogleSignIn.requestPermissions((Activity) _this, requestCode.ordinal(), this.getGoogleAccount(), fitnessOptions);
        }
    }

    private void performActionForRequestCode(FitActionRequestCode requestCode) {
       /* if (FitActionRequestCode.READ_DATA == requestCode)
            readData();
        if (FitActionRequestCode.SUBSCRIBE == requestCode)
            subscribe();*/
    }

    private boolean oAuthPermissionsApproved() {
        return GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions);
    }

    private GoogleSignInAccount getGoogleAccount() {
        return  googleSignInAccount; // GoogleSignIn.getAccountForExtension(_this, fitnessOptions);
    }

    private Boolean permissionApproved() {

        boolean approved = false;
        boolean runningQOrLater =
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

        if (runningQOrLater) {
            approved = (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(_this, Manifest.permission.ACTIVITY_RECOGNITION));
        } else {
            approved = true;
        }
        return approved;
    }

    private final void requestRuntimePermissions(FitActionRequestCode requestCode) {

        boolean shouldProvideRationale = false;//ActivityCompat.shouldShowRequestPermissionRationale((Activity) _this, Manifest.permission.ACTIVITY_RECOGNITION);
        boolean var4 = false;
        boolean var5 = false;
        boolean var7 = false;
        if (shouldProvideRationale) {
            Log.i("StepCounter", "Displaying permission rationale to provide additional context.");
            //Snackbar.make(this.findViewById(1000124), 1900078, -2).setAction(1900093, (OnClickListener)(new MainActivity$requestRuntimePermissions$$inlined$let$lambda$1(this, shouldProvideRationale, requestCode))).show();
        } else {
            Log.i("StepCounter", "Requesting permission");
            ActivityCompat.requestPermissions((Activity) _this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, requestCode.ordinal());
        }
        //_stepCounter.StartSensor(getGoogleAccount());
    }

    /*private void requestRuntimePermissions(requestCode: FitActionRequestCode) {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale((Activity) _this, Manifest.permission.ACTIVITY_RECOGNITION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        requestCode.let {
            if (shouldProvideRationale) {
                //Log.i(TAG, "Displaying permission rationale to provide additional context.");
                Snackbar.make(
                        findViewById(R.id.main_activity_view),
                        R.string.permission_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                            requestCode.ordinal)
                }
                        .show()
            } else {
                Log.i(TAG, "Requesting permission")
                // Request permission. It's possible this can be auto answered if device policy
                // sets the permission in a given state or the user denied the permission
                // previously and checked "Never ask again".
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                        requestCode.ordinal)
            }
        }
    }*/


}

/*
public class FitActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "FitActivity";
    private static final int REQUEST_OAUTH = 1;
    private GoogleApiClient mClient = null;

    int mInitialNumberOfSteps = 0;
    private TextView mStepsTextView;

    private boolean mFirstCount = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStepsTextView = (TextView) findViewById(R.id.textview_number_of_steps);
    }


    @Override
    protected void onStart() {
        super.onStart();

        mFirstCount = true;
        mInitialNumberOfSteps = 0;

        if (mClient == null || !mClient.isConnected()) {
            connectFitness();
        }
    }

    private void connectFitness() {
        Log.i(TAG, "Connecting...");

        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                // select the Fitness API
                .addApi(Fitness.API)
                // specify the scopes of access
                .addScope(Scopes.FITNESS_ACTIVITY_READ)
                .addScope(Scopes.FITNESS_BODY_READ_WRITE)
                .addScope(Scopes.FITNESS_LOCATION_READ)
                // provide callbacks
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Connect the Google API client
        mClient.connect();
    }

    // Manage OAuth authentication
    @Override
    public void onConnectionFailed(ConnectionResult result) {

        // Error while connecting. Try to resolve using the pending intent returned.
        if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED ||
                result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                // Request authentication
                result.startResolutionForResult(this, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception connecting to the fitness service", e);
            }
        } else {
            Log.e(TAG, "Unknown connection issue. Code = " + result.getErrorCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            if (resultCode == RESULT_OK) {
                // If the user authenticated, try to connect again
                mClient.connect();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // If your connection gets lost at some point,
        // you'll be able to determine the reason and react to it here.
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i(TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.i(TAG, "Connected!");

        // Now you can make calls to the Fitness APIs.
        invokeFitnessAPIs();

    }

    private void invokeFitnessAPIs() {

        // 1. Create a listener object to be called when new data is available
        DataSourceListener listener = new DataSourceListener() {
            @Override
            public void onEvent(DataPoint dataPoint) {

                for (DataType.Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    updateTextViewWithStepCounter(val.asInt());
                }
            }
        };

        // 1. Specify what data sources to return
        DataSourcesRequest req = new DataSourcesRequest.Builder()
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .setDataTypes(DataTypes.STEP_COUNT_DELTA)
                .build();

        // 2. Invoke the Sensors API with:
        // - The Google API client object
        // - The data sources request object
        PendingResult<DataSourcesResult> pendingResult =
                Fitness.SensorsApi.findDataSources(mClient, req);



        // 2. Build a sensor registration request object
        SensorRequest sensorRequest = new SensorRequest.Builder()
                .setDataType(DataTypes.STEP_COUNT_CUMULATIVE)
                .setSamplingRate(1, TimeUnit.SECONDS)
                .build();

        // 3. Invoke the Sensors API with:
        // - The Google API client object
        // - The sensor registration request object
        // - The listener object
        PendingResult<Status> regResult =
                Fitness.SensorsApi.register(mClient, sensorRequest, listener);

        // 4. Check the result asynchronously
        regResult.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.d(TAG, "listener registered");
                    // listener registered
                } else {
                    Log.d(TAG, "listener not registered");
                    // listener not registered
                }
            }
        });
    }

    private void updateTextViewWithStepCounter(final int numberOfSteps) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "On Datapoint!", Toast.LENGTH_SHORT);

                if(mFirstCount && (numberOfSteps != 0)) {
                    mInitialNumberOfSteps = numberOfSteps;
                    mFirstCount = false;
                }
                if(mStepsTextView != null){
                    mStepsTextView.setText(String.valueOf(numberOfSteps - mInitialNumberOfSteps));
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mClient.isConnected() || mClient.isConnecting()) mClient.disconnect();
        mInitialNumberOfSteps = 0;
        mFirstCount = true;
    }

}*/
