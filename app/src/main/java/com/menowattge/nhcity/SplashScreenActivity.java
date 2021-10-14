package com.menowattge.nhcity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.menowattge.nhcity.nfcreadwrite.R;

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

        final Thread timeout = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    sleep(2000);

                        Intent intent = new Intent(getApplicationContext(), MyCustomAppIntro.class);
                        startActivity(intent);
                        finish();

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        timeout.start();



    }

}




