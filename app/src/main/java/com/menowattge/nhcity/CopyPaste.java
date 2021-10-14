package com.menowattge.nhcity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.menowattge.nhcity.nfcreadwrite.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class CopyPaste extends Activity {

    public static final String ERROR_DETECTED = "Nessun tag NFC rilevato";
    public static final String WRITE_SUCCESS = "Operazione Completata";
    public static final String WRITE_ERROR = "Errore scrittura, il tag NFC deve essere a contatto con lo smartphone";

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;

    TextView message;
    FloatingActionButton btnWrite;

    String payload="";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_copy_paste);
        context = this;

        btnWrite = findViewById(R.id.fab_copy);

        payload = getIntent().getStringExtra("payload");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "NFC non supportato dal telefono.", Toast.LENGTH_LONG).show();
            finish();
        }


        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };


        /**
         * Pulsante INVIO
         */
        btnWrite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    Log.d("payload",payload);

                    if (payload!="") {
                        Snackbar.make(v, "OK, avvicina il telefono all'NFC dell'alimentatore per inviare il messaggio", 5000)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(v, "Selezione non valida", 5000)
                                .setAction("Action", null).show();
                    }
                }

        });




    }




    /******************************************************************************
     **********************************Write to NFC Tag****************************
     ******************************************************************************/
    private void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();

    }
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes(StandardCharsets.US_ASCII);
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }




    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
        if (myTag!=null && !payload.equals("")){
            try {
                write(payload,myTag);
                Toast.makeText(context, "Operazione Completata", Toast.LENGTH_LONG ).show();


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Errore : Tag Corrotto", Toast.LENGTH_LONG ).show();

            } catch (FormatException e) {
                e.printStackTrace();
                Toast.makeText(context, "Errore : Tag Corrotto", Toast.LENGTH_LONG ).show();
            }
        }
    }



    /******************************************************************************
     **********************************Enable Write********************************
     ******************************************************************************/
    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    /******************************************************************************
     **********************************Disable Write*******************************
     ******************************************************************************/
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }



}