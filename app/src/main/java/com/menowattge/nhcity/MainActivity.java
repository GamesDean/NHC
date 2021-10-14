package com.menowattge.nhcity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.menowattge.nhcity.nfcreadwrite.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

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

    String text;

    private ProgressDialog pd;

    // API login
    String username="tecnico@citymonitor.it";
    String password="tecnico";

    private List<String> IDDI   = new ArrayList<>();
    private List<String> Corrente   = new ArrayList<>();
    private List<String> CorrenteSpinner   = new ArrayList<>();

    public String [] potenze; // per lo spinner dove visualizzo es: 350 400 500
    public String [] potenzeTrue; // per inviare i dati dove invece tronco l'ultimo zero es : 35 40 50

    private List<String> idProfili   = new ArrayList<>();
    private List<String> nomeProfili   = new ArrayList<>();

    private ProgressDialog pg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = this;

        tvNFCContent = (TextView) findViewById(R.id.nfc_contents);
        info = findViewById(R.id.info);
        btnWrite =  findViewById(R.id.button2);
        btnDiagnostica =  findViewById(R.id.button);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);

        pg = new ProgressDialog(new ContextThemeWrapper(MainActivity.this,R.style.ProgressDialogCustom));


        info.setTypeface(null, Typeface.BOLD);

        info.setText("Sezione diagnostica ");

        checkConnection();

        // -------------------------DEBUG API -----------------------


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://citymonitor.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();


        String token = LoginCredentials.getAuthToken(username,password);

        getConfigurazioneProfili(retrofit,token);

        getProfili(retrofit,token);


        // -------------------------------END-DEBUG-----------------------------------

        pd = new ProgressDialog(new ContextThemeWrapper(MainActivity.this,R.style.ProgressDialogCustom));

        pg.setMessage("Caricamento Profili...");
        pg.show();
        pg.setCanceledOnTouchOutside(false);

        /**
         * Spinner per selezionare il Profilo, l'utente sceglie es - "22ERP" ed il payload sarà "10" ovvero l'IdProfilo
         * parsato dal JSON. Tutto ciò è trasparente all'utente.
         */
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
                        payloadSpinner1 = idProfili.get(0); // es : 9
                        break;
                    case 2:
                        payloadSpinner1 = idProfili.get(1);
                        break;
                    case 3:
                        payloadSpinner1 = idProfili.get(2);
                        break;
                    case 4:
                        payloadSpinner1 = idProfili.get(3);
                        break;
                    case 5:
                        payloadSpinner1 = idProfili.get(4);
                        break;
                    case 6:
                        payloadSpinner1 = idProfili.get(5);
                        break;
                    case 7:
                        payloadSpinner1 = idProfili.get(6);
                        break;
                    case 8:
                        payloadSpinner1 = idProfili.get(7);
                        break;
                    case 9:
                        payloadSpinner1 = idProfili.get(8);
                        break;
                    case 10:
                        payloadSpinner1 = idProfili.get(9);
                        break;
                    case 11:
                        payloadSpinner1 = idProfili.get(10);
                        break;
                    case 12:
                        payloadSpinner1 = idProfili.get(11);
                        break;
                    case 13:
                        payloadSpinner1 = idProfili.get(12);
                        break;
                    case 14:
                        payloadSpinner1 = idProfili.get(13);
                        break;
                    case 15:
                        payloadSpinner1 = idProfili.get(14);
                        break;
                    case 16:
                        payloadSpinner1 = idProfili.get(15);
                        break;
                    case 17:
                        payloadSpinner1 = idProfili.get(16);
                        break;
                    case 18:
                        payloadSpinner1 = idProfili.get(17);
                        break;
                    case 19:
                        payloadSpinner1 = idProfili.get(18);
                        break;
                    case 20:
                        payloadSpinner1=idProfili.get(19);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }
        });


        /**
         * Spinner per selezionare le potenze - l'id è statico, le potenze visualizzate hanno 3 decimali
         * le potenze inviate, per risparmiare spazio, ne hanno due di decimali es : 35 40 50
         */
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

                    case 1 : payloadSpinner2="1"+"|"+potenzeTrue[1];break; // 1 400 400 400 (1 è l'ID, è statico per ora)
                    case 2 : payloadSpinner2="2"+"|"+potenzeTrue[2];break;// 2 350 400 500
                    case 3 : payloadSpinner2="3"+"|"+potenzeTrue[3];break;
                    case 4 : payloadSpinner2="4"+"|"+potenzeTrue[4];break;
                    case 5 : payloadSpinner2="5"+"|"+potenzeTrue[5];break;
                    case 6 : payloadSpinner2="6"+"|"+potenzeTrue[6];break;
                    case 7 : payloadSpinner2="7"+"|"+potenzeTrue[7];break;
                    case 8 : payloadSpinner2="8"+"|"+potenzeTrue[8];break;
                    case 9 : payloadSpinner2="9"+"|"+potenzeTrue[9];break;
                    case 10 : payloadSpinner2="10"+"|"+potenzeTrue[10];break;
                    case 11 : payloadSpinner2="11"+"|"+potenzeTrue[11];break;
                    case 12 : payloadSpinner2="12"+"|"+potenzeTrue[12];break;
                    case 13 : payloadSpinner2="13"+"|"+potenzeTrue[13];break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        /**
         * Pulsante INVIO
         */
        btnWrite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean nfc_enabled = checkNfc();
                if (nfc_enabled) {

                    // rimuovo gli spazi es : 3 450 450 600 -> 3450450600
                    payloadSpinner2 = payloadSpinner2.replaceAll("\\s+","");

                    payload = payloadSpinner2+"|"+ payloadSpinner1; // pipe come separatore, definito con Riccardo

                    Log.d("payload",payload);

                    if (!payloadSpinner1.equals("") && !payloadSpinner2.equals("")) {
                        spinner1.setEnabled(false);
                        spinner2.setEnabled(false);
                        Snackbar.make(v, "OK, avvicina il telefono all'NFC dell'alimentatore per inviare il messaggio", 5000)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(v, "Selezione non valida", 5000)
                                .setAction("Action", null).show();
                    }
                }
            }


        });

        /**
         * Pulsante Diagnostica che legge i dati dall'alimentatore e li mostra a video
         */
        btnDiagnostica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //readFromIntent(getIntent());
                boolean nfc_enabled = checkNfc();
                if(nfc_enabled) {
                    Intent intent = new Intent(getApplicationContext(), DiagnosticActivity.class);
                    startActivity(intent);
                }
            }
        });


        /**
         * Controllo funzionalità NFC
         */

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

        // checkTag();
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

        //tvNFCContent.setText("Contenuto NFC : " + text);
        Log.d("CONTENUTO NFC : ",text);
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


    public boolean checkNfc(){
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            // adapter exists and is enabled.
            return true;
        }else{
            Toast.makeText(this,"ATTENZIONE : Attivare l'NFC",Toast.LENGTH_LONG).show();
            return false;
        }
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
        checkNfc();
        WriteModeOn();
        if (myTag!=null && !payload.equals("")){
            try {
                write(payload,myTag);
                Toast.makeText(context, "Operazione Completata", Toast.LENGTH_LONG ).show();
                Intent intent = new Intent(this,MainActivity.class);
                //startActivity(intent);

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

    /************************************** API ****************************************/


    /**
     * Controlla la connessione internet
     */
    public void checkConnection(){
        final Thread timeout = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            ConnectivityManager mgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo netInfo = mgr.getActiveNetworkInfo();
                            boolean isConnected = netInfo != null &&
                                    netInfo.isConnectedOrConnecting();

                            if (isConnected ) {

                            }
                            else {
                                //No internet
                                Toast.makeText(getApplicationContext(),"NO INTERNET-IMPOSSIBILE PROSEGUIRE-\nCONNETTERSI E RIAVVIARE L'APP".toUpperCase(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    sleep(4000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timeout.start();
    }


    /**
     * Ottiene la lista dei profili ovvero le CORRENTI es : min: 350mA,rid: 400mA,max:550mA
     * @param retrofit
     * @param token
     */
    public void getConfigurazioneProfili(Retrofit retrofit, String token) {
        JsonApi jsonApi = retrofit.create(JsonApi.class);
        Call<JsonArray> call = jsonApi.getConfigProfili(token);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String rc = String.valueOf(response.code());
                if (!response.isSuccessful()) {
                    Log.d("http_get_conf_ko_rc : ", rc);

                }
                else{
                    Log.d("http_get_conf_ok_rc : ", rc);
                    //JSON in risposta, lo salvo in una stringa unica

                    String data = response.body().toString();
                    // divido gli elementi sfruttando la virgola
                    String[] pairs = data.split(",");
                    try {
                        for (String item : pairs){
                            // scremo gli ID
                            if (!item.substring(item.length()-1).equals("}") && !item.substring(item.length()-1).equals("]")){
                                // prendo gli ultimi due valori es :6 e 13
                                String tempID = item.substring(item.length()-2);
                                // isolo il numero ad una cifra
                                if (tempID.startsWith(":")){
                                    IDDI.add(tempID.substring(1,2));
                                }else{
                                    IDDI.add(tempID);
                                }
                                // scremo le correnti
                            }else{
                                String[] pairs2 = item.split(";");

                                for (String item2 : pairs2){
                                    // isolo i dati
                                    String[] pairs3 = item2.split(":");

                                    for( String item3 : pairs3){
                                        // prendo solo i valori numerici
                                        if(item3.trim().startsWith("2")||item3.trim().startsWith("3")
                                                || item3.trim().startsWith("4") ||item3.trim().startsWith("5")
                                                || item3.trim().startsWith("6")|| item3.trim().startsWith("7")) {

                                            if(!(item3.endsWith("}") || item3.endsWith("]"))) {
                                                CorrenteSpinner.add(item3.substring(0,4)); // senza mA
                                                Corrente.add(item3.substring(0,3)); // senza ultimo 0 ed mA

                                            } else{
                                                String tempitem = item3.substring(1,6);
                                                // Log.d("ITEMSHZ",tempitem); // con mA
                                                CorrenteSpinner.add(tempitem.substring(0,3)); // senza mA
                                                Corrente.add(tempitem.substring(0,2)); // senza ultimo 0 ed mA

                                                // riempio lo spinner con i valori delle correnti
                                                fillSpinner();

                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }catch (Error e){e.printStackTrace();

                    }
                }
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.d("http_rc_fail : ", t.getMessage());
            }
        });

    }



    public void getProfili(Retrofit retrofit, String token) {
        JsonApi jsonApi = retrofit.create(JsonApi.class);
        Call<JsonArray> call = jsonApi.getProfili(token);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String rc = String.valueOf(response.code());
                if (!response.isSuccessful()) {
                    Log.d("http_get_profili_ko_rc : ", rc);
                }
                else{
                    Log.d("http_get_profili_ok_rc : ", rc);
                    //JSON in risposta, lo salvo in una stringa unica

                    String data = response.body().toString();
                    // divido gli elementi sfruttando la virgola
                    String[] pairs = data.split(",");

                    for (int i=0; i<pairs.length;i++){
                        // prendo solo quelli a LED
                        if (pairs[i].substring(pairs[i].length()-5).equals("\"LED\"")){
                            // individuati i LED, prendo il Nome e l IdProfilo
                            //Log.d("CCC",pairs[i+1]);
                            // Log.d("CCC",pairs[i+2]);

                            // divido  per isolare gli ID e salvo in un array d'appoggio
                            String []idProfilo = pairs[i+2].split(":");

                            //idProfili = new String[19]; // inizializzo l'array, vedere la dimensione

                            // il log CCC fa capire il perche faccio questa cosa, per isolare gli ID togliendo le parentesi
                            // riempio l'arraylist idProfili con gli ID
                            for (int j=0; j<idProfilo.length;j++ ){
                                if(idProfilo[j].endsWith("}")) {
                                    idProfili.add(idProfilo[j].substring(0, idProfilo[j].length() - 1));
                                    Log.d("idprof", idProfili.toString());
                                }
                                else if (idProfilo[j].endsWith("}]")){
                                    idProfili.add(idProfilo[j].substring(0, idProfilo[j].length() - 2));
                                    Log.d("idprofz", idProfili.toString());
                                }
                            }

                            // divido per isolare i nomi e salvo in un array d'appoggio
                            String []nomeProfilo = pairs[i+1].split(":");
                            // inizializzo l'array dove salverò i nomi dei profili
                            // isolo i nomi dei profili e li salvo nell'arraylist non considerando la scritta "Nome" del json
                            for (int k=0;k<nomeProfilo.length;k++){
                                if(!nomeProfilo[k].equals("\"Nome\"")){
                                    nomeProfili.add(nomeProfilo[k].replace("\"",""));
                                }
                            }
                        }
                    }
                    // riempio lo spinner con i dati
                    fillSpinnerProfili();
                }
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.d("http_rc_fail : ", t.getMessage());
            }
        });

    }

    public void dismissProgressDialog(){
        final Thread timeout = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    sleep(2000);

                    pg.dismiss();

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        timeout.start();
    }

    /**
     * Riempio lo spinner con i dati delle potenze
     */
    public void fillSpinner(){
        // 39/3 = 13 dato che ho bisogno di raggruppare in triple + 1 perche nella prima posizione c'è l'hint
        potenze = new String[CorrenteSpinner.size()/3+1];
        potenzeTrue = new String[Corrente.size()/3+1];

        potenze[0]= new String("Seleziona Potenza"); // hint
        potenzeTrue[0]= new String("test"); // segnaposto
        int z=1;
        // scorro l'array ed aumento di tre ogni volta
        for (int i=0;i<CorrenteSpinner.size();i+=3 ){
            potenze[z] = CorrenteSpinner.get(i)+CorrenteSpinner.get(i+1)+" "+CorrenteSpinner.get(i+2)+" mA";
            z++; // 13 volte in pratica
        }
        int j=1;
        for (int i=0;i<Corrente.size();i+=3 ){
            potenzeTrue[j] = Corrente.get(i)+Corrente.get(i+1)+" "+Corrente.get(i+2);
            j++; // 13 volte in pratica
        }

        Log.d("CORRENTE_POT", Arrays.toString(potenzeTrue));
        ArrayAdapter<String> powerAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item, potenze);
        spinner2.setAdapter(powerAdapter);

        dismissProgressDialog();
    }

    /**
     * Riempio lo spinner con i dati dei profili
     */
    public void fillSpinnerProfili(){

        String[]nomiProgrammi = new String[nomeProfili.size()+1]; // +1 altrimenti mi perdo l'ultimo
        nomiProgrammi[0]= "Seleziona Profilo"; // hint
        for(int i=1; i<nomeProfili.size()+1;i++) { // +1 altrimenti mi perdo l'ultimo

            nomiProgrammi[i]=nomeProfili.get(i-1); // i-1 perche parto da 1 causa hint ed altrimenti mi skippa il primo elemento
        }
        ArrayAdapter<String> programAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, nomiProgrammi);
        spinner1.setAdapter(programAdapter);
        dismissProgressDialog();
    }

    // quando premo back, chiude l'app
    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }



}