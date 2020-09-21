package com.menowattge.nhc;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.menowattge.nhc.nfcreadwrite.R;

/**
 * Propone a video una schermata di presentazione
 */
// TODO cambiare splashscreen introdurne uno idoneo


public class SplashScreenActivity extends AppCompatActivity {

    public void quit(){

        finishAffinity();
        System.exit(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        ConnectivityManager mgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();
        final boolean isConnected = netInfo != null && netInfo.isConnectedOrConnecting();

        final Thread timeout = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    sleep(2000);

                    if(isConnected){
                        Intent intent = new Intent(getApplicationContext(), MyCustomAppIntro.class);
                        startActivity(intent);
                        finish();
                    }

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        timeout.start();



    }

}




