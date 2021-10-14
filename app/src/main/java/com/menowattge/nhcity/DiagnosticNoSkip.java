package com.menowattge.nhcity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.menowattge.nhcity.nfcreadwrite.R;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class DiagnosticNoSkip extends AppCompatActivity {

    Tag myTag2;
    TextView tv;
    PendingIntent pendingIntent;
    String text = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_diagnostic_no_skip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Controllo Tag");

        tv= findViewById(R.id.nfc_contents);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFromIntent(getIntent());

                String message = "Lettura eseguita correttamente";

                if (text==null){
                    message = "Errore : Tag vuoto o corrotto";
                    tv.setText("Contenuto NFC : " + "ERRORE");
                }
                // quindi se leggo "A50^" indico il link al playstore per scaricare o aprire l'altra app NHC
                if(text!=null&&text.length()<10){
                    message = "Formato non supportato";

                    new AlertDialog.Builder(DiagnosticNoSkip.this)
                            .setTitle(message)
                            .setMessage("Installare NHC")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    final String appPackageName = "com.menowattge.nhcity";
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();
                }
                Snackbar.make(view, message, 5000)
                        .setAction("Action", null).show();
                text = null;
            }
        });
    }

    /**
     * Converte il dato della lettura in un valore leggibile es : 9|405030|5 -> 23ERP 400500300 mA
     * @param text ovvero la lettura es 9|405030|5
     * @return 23ERP 400500300 mA
     */
    public String getProfileName(String text){

        // divido basandomi sul pipe
        String [] appoggio = text.split("\\|");
        // il primo id non mi serve quindi skippo la posizione 0
        String corrente = appoggio[1];
        String idProfilo = appoggio[2];

        // basato sul JSON /api/Profili
        String nomeProfilo="";
        switch (idProfilo) {
            case "1" :nomeProfilo="23M2"; break;
            case "2" :nomeProfilo="23M3"; break;
            case "3" :nomeProfilo="22M2"; break;
            case "4" :nomeProfilo="22M3"; break;
            case "5" :nomeProfilo="ERP"; break;
            case "6" :nomeProfilo="EMX"; break;
            case "7" :nomeProfilo="23EMP"; break;
            case "8" :nomeProfilo="22EMP"; break;
            case "9" :nomeProfilo="23ERP"; break;
            case "10" :nomeProfilo="22ERP"; break;
            case "11" :nomeProfilo="23M2S2"; break;
            case "12" :nomeProfilo="23M3S2"; break;
            case "13" :nomeProfilo="22M2S2"; break;
            case "14" :nomeProfilo="22M3S2"; break;
            case "15" :nomeProfilo="LSM2"; break;
            case "16" :nomeProfilo="LSM3"; break;
            case "17" :nomeProfilo="LSM2S2"; break;
            case "18" :nomeProfilo="LSM3S2"; break;
            case "19" :nomeProfilo="R400"; break;
            case "20" :nomeProfilo="22DMP"; break;
        }
        // aggiungo gli zeri finali ad ogni coppia
        corrente = corrente.substring(0,2)+"0"+corrente.substring(2,4)+"0"+corrente.substring(4,6)+"0"+" mA";

        return nomeProfilo+"-"+corrente;
    }


    /******************************************************************************
     **********************************Read From NFC Tag***************************
     ******************************************************************************/
    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }
    @SuppressLint("SetTextI18n")
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;


//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        // mostro a video il nome del profilo solo con i dati "nuovi"
        String textToShow;
        if (text.length()>=10) {
            //textToShow = getProfileName(text);  // es 22M2 400500350 mA
            //tv.setTextColor(Color.parseColor("#0fb30c")); // verde
            //tv.setText("Contenuto NFC : " + textToShow);
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }


    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity, activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);

        adapter.enableForegroundDispatch(activity, pendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        Toast.makeText(getApplicationContext(),"Lettura eseguita : premere il pulsante ", Toast.LENGTH_LONG).show();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag2 = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this,NfcAdapter.getDefaultAdapter(this));
       // tv.setTextColor(Color.parseColor("#b23027"));
        tv.setText("Avvicinati al tag poi premi il pulsante");
    }


}