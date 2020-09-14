package com.menowattge.nhc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.menowattge.nhc.nfcreadwrite.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    public static final String ERROR_DETECTED = "Nessun tag NFC rilevato";
    public static final String WRITE_SUCCESS = "Operazione Completata";
    public static final String WRITE_ERROR = "Errore scrittura, il tag NFC deve essere a contatto con lo smartphone";

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;

    TextView tvNFCContent,info,info2;
    TextView message;
    FloatingActionButton btnWrite;
    FloatingActionButton btnDiagnostica;
    Spinner spinner1,spinner2;

    String payloadSpinner1,payloadSpinner2,m ;
    String payload="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = this;

        tvNFCContent = (TextView) findViewById(R.id.nfc_contents);
        info = findViewById(R.id.info);
        info2 = findViewById(R.id.info_2);
        //message = (TextView) findViewById(R.id.edit_message);
        btnWrite =  findViewById(R.id.button2);
        btnDiagnostica =  findViewById(R.id.button);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);

        info.setTypeface(null, Typeface.BOLD);
        info2.setTypeface(null, Typeface.BOLD);

        info.setText("Sezione diagnostica ");

        info2.setText("\n1) Seleziona il Programma e la Potenza" + "\n2) Premi il pulsante 'Salva'\n"+"3) Avvicina il telefono/tablet al sensore NFC dell'alimentatore per inviare i dati'");

        String [] listaProgrammi = {"Seleziona Programma","Programma 1:23M2","Programma 2:23M2","Programma 3:22M2","Programma 4:22M3","Programma 5:ERP", "Programma 6:EMX","Programma 7:23EMP","Programma 8:22EMP","Programma 9:23ERP",
                "Programma 10:22ERP", "Programma 11:23M2S2", "Programma 12:23M3S2","Programma 13:22M2S2","Programma 14:22M3S2","Programma 15:LSM2","Programma 16:LSM3", "Programma 17:LSM2S2", "Programma 18:LSM3S2","Programma 19:R400","Programma 20:P20"};

        String [] potenze ={"Seleziona Potenza","400 mA","450 mA","500 mA","550 mA","600 mA","650 mA","700 mA"};

        ArrayAdapter<String> powerAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, potenze);
        ArrayAdapter<String> programAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, listaProgrammi);
        spinner1.setAdapter(programAdapter);
        spinner2.setAdapter(powerAdapter);


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#4f9e33"));
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                switch (position) {
                    case 0:
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#4f9e33"));

                        payloadSpinner1="";
                        payloadSpinner2="";
                        break;
                    case 1:
                        payloadSpinner1 = "A";
                        break;
                    case 2:
                        payloadSpinner1 = "B";
                        break;
                    case 3:
                        payloadSpinner1 = "C";
                        break;
                    case 4:
                        payloadSpinner1 = "D";
                        break;
                    case 5:
                        payloadSpinner1 = "E";
                        break;
                    case 6:
                        payloadSpinner1 = "F";
                        break;
                    case 7:
                        payloadSpinner1 = "G";
                        break;
                    case 8:
                        payloadSpinner1 = "H";
                        break;
                    case 9:
                        payloadSpinner1 = "I";
                        break;
                    case 10:
                        payloadSpinner1 = "J";
                        break;
                    case 11:
                        payloadSpinner1 = "K";
                        break;
                    case 12:
                        payloadSpinner1 = "L";
                        break;
                    case 13:
                        payloadSpinner1 = "M";
                        break;
                    case 14:
                        payloadSpinner1 = "N";
                        break;
                    case 15:
                        payloadSpinner1 = "O";
                        break;
                    case 16:
                        payloadSpinner1 = "P";
                        break;
                    case 17:
                        payloadSpinner1 = "Q";
                        break;
                    case 18:
                        payloadSpinner1 = "R";
                        break;
                    case 19:
                        payloadSpinner1 = "S";
                        break;
                    case 20:
                        payloadSpinner1="T";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#4f9e33"));
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                switch (position){
                    case 0 :
                        payloadSpinner1="";
                        payloadSpinner2="";
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#4f9e33"));

                        break;
                    // apice serve come fine stringa per riccardo
                    case 1 : payloadSpinner2="40^";break;
                    case 2 : payloadSpinner2="45^";break;
                    case 3 : payloadSpinner2="50^";break;
                    case 4 : payloadSpinner2="55^";break;
                    case 5 : payloadSpinner2="60^";break;
                    case 6 : payloadSpinner2="65^";break;
                    case 7 : payloadSpinner2="70^";break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


/*
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (myTag == null) {
                        Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show();
                    } else {
                       if (payloadSpinner1.equals("") || payloadSpinner2.equals("")){
                           Toast.makeText(context,"Seleziona programma e potenza, poi premi OK ",Toast.LENGTH_LONG).show();
                       }else {
                           //write(message.getText().toString(), myTag);
                           write(payloadSpinner1 + payloadSpinner2, myTag);
                           Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG).show();
                       }
                    }
                } catch (IOException e) {
                    Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG ).show();
                    e.printStackTrace();
                } catch (FormatException e) {
                    Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG ).show();
                    e.printStackTrace();
                }
            }
        });*/


        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               payload=payloadSpinner1+payloadSpinner2;
                if(!payloadSpinner1.equals("") && !payloadSpinner2.equals("")) {
                    Toast.makeText(context, "OK, avvicina il telefono all'NFC dell'alimentatore per inviare il messaggio", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, "Selezione non valida", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnDiagnostica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //readFromIntent(getIntent());
                Intent intent = new Intent(getApplicationContext(),DiagnosticActivity.class);
                startActivity(intent);
            }
        });


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "NFC non supportato dal telefono.", Toast.LENGTH_LONG).show();
            finish();
        }
       // readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
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

        String text = "";
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

        tvNFCContent.setText("Contenuto NFC : " + text);
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
       // readFromIntent(intent);
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
        if (myTag!=null && payload!=null){
            try {
                write(payload,myTag);
                Toast.makeText(context, "Operazione Completata", Toast.LENGTH_LONG ).show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
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