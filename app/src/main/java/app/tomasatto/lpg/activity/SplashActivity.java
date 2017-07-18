package app.tomasatto.lpg.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import static app.tomasatto.lpg.activity.LoginActivity.CUSTOMER_ID;
import static app.tomasatto.lpg.activity.LoginActivity.MY_PREFS_NAME;

/**
 * Created by Megha on 11-07-2017.
 */

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(CUSTOMER_ID, null);
        if (restoredText != null) {
            Intent intentHome = new Intent(SplashActivity.this,HomeActivity.class);
            startActivity(intentHome);

        }else{
            Intent intentLogin = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(intentLogin);
        }
        SplashActivity.this.finish();
    }
}
