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

        //123

        addSlide(AppIntroFragment.newInstance(
                "1) Attivare l'NFC ",
                "Dalle impostazioni del proprio smartphone",
                R.drawable.nfc,
                Color.parseColor("#87b5b7")
        )
        )
        addSlide(AppIntroFragment.newInstance("2) Selezionare Programma e Potenza ",
                "Dal menù a tendina, come da immagine",
                R.drawable.seleziona,
                Color.parseColor("#87b5b7")

        ))
        addSlide(AppIntroFragment.newInstance("3) Salvare premendo il pulsante a forma di Floppy... ",
                "..poi avvicinarsi al TAG NFC di Hixos finchè non compare il messaggio : 'Operazione Completata' ",
                R.drawable.pulsante_red,
                Color.parseColor("#87b5b7")

        ))
        addSlide(AppIntroFragment.newInstance("4a) Verificare il contenuto del TAG ",
                "Utile per controllare il contenuto del TAG NFC",
                R.drawable.diagnostica_1,
                Color.parseColor("#87b5b7")

        ))
        addSlide(AppIntroFragment.newInstance("4b) Avvicinare lo smartphone al TAG ",
                "..poi premere il tasto info per visualizzare i dati",
                R.drawable.diagnostica_2,
                Color.parseColor("#87b5b7")

        ))
        addSlide(AppIntroFragment.newInstance("4c) Dati NFC Hixos ",
                "In rosso il contenuto del TAG",
                R.drawable.diagnostica_3,
                Color.parseColor("#87b5b7")

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
