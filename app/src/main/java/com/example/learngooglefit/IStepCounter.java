package com.example.learngooglefit;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;

public interface IStepCounter {
    void StartSensor();
    FitnessOptions getFitActivity();
    GoogleSignInAccount getGoogleAccount();
}
