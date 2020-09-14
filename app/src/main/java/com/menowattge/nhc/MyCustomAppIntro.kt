package com.menowattge.nhc

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Window
import android.view.WindowManager
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.menowattge.nhc.nfcreadwrite.R

class MyCustomAppIntro : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)

        // Make sure you don't call setContentView!

        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment

        addSlide(AppIntroFragment.newInstance(
                "1) Attivare l'NFC ",
                "NHC : NFC Hixos Configurator",
                R.drawable.ge_splash_sfocatura,
                Color.parseColor("#9aa098")
        )
        )
        addSlide(AppIntroFragment.newInstance("2) Selezionare Programma e Potenza ",
                "NHC : NFC Hixos Configurator",
                R.drawable.ge_splash_sfocatura,
                Color.parseColor("#9aa098")

        ))
        addSlide(AppIntroFragment.newInstance("3) Salvare premendo il pulsante a forma di Floppy... ",
                "...avvicinarsi ad Hixos per ultimare la configurazione",
                R.drawable.ge_splash_sfocatura,
                Color.parseColor("#d0d3c6")

        ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        val intent = Intent(applicationContext, MainActivity::class.java)  //aa
        startActivity(intent)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
