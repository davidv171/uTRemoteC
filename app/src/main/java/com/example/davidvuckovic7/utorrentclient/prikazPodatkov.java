package com.example.davidvuckovic7.utorrentclient;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class prikazPodatkov extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private    TabHost host;
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

    private List<torrent> torrentList = new ArrayList<>();
    private List<torrent> torrentList1 = new ArrayList<>();

    private List<torrent> torrentList2 = new ArrayList<>();
    private List<torrent> torrentList3 = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView3;

    private torrentAdapter mAdapter;
    private torrentAdapter mAdapter1;
    private torrentAdapter mAdapter2;
    private torrentAdapter mAdapter3;
    pridobitevPodatkov readWebsite=new pridobitevPodatkov();
//STRINGI KATERE PRIDOBIMO IZ GLAVNE AKTIVNOSTI IN JIH UPORABIMO V PRIDOBIVANJU JSONA,METODI updateTorrent, VREDNOSTI NASTAVIMO PRED KLICANJEM TE METODE
    // V ON CREATE
    private  String uName;
    private  String pw;
    private  String ip;
     ImageView stanje;
    private  ImageView zbrisi;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //POMOŽNE GLOBALNE SPREMENLJIVKE ZA MANJ PISANJA PRI KLICANJU METOD
            //TOKEN NE MORE BITI GLOBALNA SPREMENLJIVKA SAJ POTREBUJEMO ZA VSAKI REQUEST NOVI TOKEN!!!!!!!!!!!!!!!!!!!
            //AUTHG STA PASSWORD IN USERNAME ENKRIPTIRANA V BASE64
            //LING JE IP IN PORT TER DODAN /GUI/
            //cookieG je cookie, ki ga je treba poslat ob vsakem requestu
   private static String authG=null;
   private static String linkG=null;
   private static String cookieG=null;
   private String port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_podatkov);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        System.out.println("SPREFERENCES" + sharedPreferences.getAll());

        stanje = (ImageView)findViewById(R.id.stanje);
        zbrisi = (ImageView)findViewById(R.id.zbrisi);
        String ipC=null;
        //V PRIMERU DA SE UPORABLJA 3G/4G JE ipC 0.0.0.0, avtomatsko se privzame port 32610
        //TRENUTNO NI PODPORE V PRIMERU DA BI IZ KATEREGA KOLI RAZLOGA TELEFON IMEL NASLOV, KI BI SPADAL
        //V ENAKO DOMENO KOT RAČUNALNIK.
        //NI PREDVIDENO; DA BI SE TO KDARKOLI IMPLEMENTIRALO
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        ipC = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


        host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("Vsi");
        spec.setContent(R.id.tab0);
        spec.setIndicator("Vsi");
        host.addTab(spec);

        spec = host.newTabSpec("Aktivni");
        spec.setContent(R.id.tab1);
        spec.setIndicator("", getResources().getDrawable(R.drawable.next));

        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Ustavljeni");
        spec.setContent(R.id.tab2);
        spec.setIndicator("", getResources().getDrawable(R.drawable.stop2));
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Končani");
        spec.setContent(R.id.tab3);
        spec.setIndicator("", getResources().getDrawable(R.drawable.check));
        host.addTab(spec);

        //recyclerview, ki vsebuje vse torrente
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new torrentAdapter(torrentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //S TEM BI NAJ REŠILI TEŽAVO UTRIPANJA ZADNJEGA ELEMENTA
        recyclerView.getItemAnimator().setChangeDuration(0);



        recyclerView.setAdapter(mAdapter);



        //recyclerview, ki vsebuje aktivne torrente, kliče metodo prepareFilter(), katera filtrira po stanjih, ter pomeče v ustrezen tab holder
        recyclerView1 = (RecyclerView) findViewById(R.id.recycler_view1);

        mAdapter1 = new torrentAdapter(torrentList1);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        recyclerView1.setLayoutManager(mLayoutManager1);
        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.setAdapter(mAdapter1);


        recyclerView2 = (RecyclerView) findViewById(R.id.recycler_view2);

        mAdapter2 = new torrentAdapter(torrentList2);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        recyclerView2.setLayoutManager(mLayoutManager2);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(mAdapter2);

        recyclerView3 = (RecyclerView) findViewById(R.id.recycler_view3);

        mAdapter3 = new torrentAdapter(torrentList3);
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        recyclerView3.setLayoutManager(mLayoutManager3);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setAdapter(mAdapter3);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

     //  readWebsite.getCookie("http://192.168.1.5:8080/gui/token.html");
       // view-source:192.168.1.5:8080/gui/?list=1&token=FFzVxYyD0R-ppeGVTl1ZCWHF3TbxcFL2eQmQEcRUlvK1gbhG19or_2V-H1gAAAAA
     //   String urlCombine=null;
     //  String token = readWebsite.pridobiToken("http://192.168.1.5:8080/gui/token.html");
      //  System.out.println("TOKEN V KLICU " + token);
        //        //view-source:192.168.1.5:8080/gui/?list=1&token=MA1BiOcDPsddZkTwMQHoqdL5nyAcuZTXmCU7Jycydg8RIvK2m0He26mtIFgAAAAA

     //   urlCombine = "http://192.168.1.5:8080/gui/?list=0&token=" + token;
     //   readWebsite.seznamTorrentov(urlCombine);
        Intent intent = getIntent();
         uName = intent.getExtras().getString("username");
         pw = intent.getExtras().getString("password");
         ip = intent.getExtras().getString("ip");
        System.out.println("IP " + ip + "USERNAME " + uName + "PASSWORD " + pw);
        System.out.println("IP PRED SPLIT" + ip);
        String [] split1 = ip.split("\\.");
        String [] split2 = ipC.split("\\.");
        System.out.println("NOTRANJI PORT" + sharedPreferences.getString("nPort","8080"));

        String nPort = sharedPreferences.getString("nPort","8080");
        String zPort = sharedPreferences.getString("zPort","41301");
        System.out.println("IP naprave"  + ipC);

//PRIDOBIMO IP OD NAPRAVE TER GLEDE NA UJEMANJE IP NASTAVIMO ZUNANJI ALI NOTRANJI PORT
        // PRIMERJAMO TAKO DA POGLEDAMO ŠTEVILKE, LOČENE Z PIKO TER PRIMERJAMO  Z ŠTEVILOM NA ISTEM INDEKSU
        //TOREJ NPR. IPJA: 192.168.1.5 IN 192.168.1.3 PREPOZNAMO KOT DA STA V ISTEM OMREŽJU
        //PRIMERJALI SMO 192 IN 192 , 168 IN 168 TER 1 IN 1
        System.out.println("IPJA V PRKZ" + ipC  + " " + ip );

        for(int i = 0;i<3;i++){
            if(Objects.equals(split1[i], split2[i])){

                port = nPort;
            }
            else{
                port = zPort;
            }
        }
            System.out.println("UPORABLJEN PORT" + port);
        try {
            updateTorrent();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    //METODA S KATERO PARSEAMO JSON, NAMENENJENA JE PREDVSEM IZLUŠČENJU TORRENTOV,TER PODATKOV ZA TORRENT V ARRAYLISTE ATRIBUTOV. TOREJ BO USTVARJENO POLJE HASH-ov
    //TER POLJE STANJ, POLJE PROGRESSA, POLJE IMEN,ATTRIBUTI Z ISTIM INDEKSOM PRIPADAJO ISTEM TORRENTU
/*JSON TEMPLATE:{
"build": BUILD NUMBER (integer),

"label": [

[

LABEL (string),

TORRENTS IN LABEL (integer)

],

...

],

"torrents": [

[

1   0HASH (string),

2     0STATUS* (integer), 201 = Seeding 100%  136 = Stopped 100%

3   1NAME (string),

4   1SIZE (integer in bytes),

5   2PERCENT PROGRESS (integer in per mils),

6   3DOWNLOADED (integer in bytes),

7   4UPLOADED (integer in bytes),

8   5RATIO (integer in per mils),

9   6UPLOAD SPEED (integer in bytes per second),

10  7DOWNLOAD SPEED (integer in bytes per second),

11  8ETA (integer in seconds),

12  2LABEL (string),

13  9PEERS CONNECTED (integer),

14  10PEERS IN SWARM (integer),

15  11SEEDS CONNECTED (integer),

16  12SEEDS IN SWARM (integer),

17  13AVAILABILITY (integer in 1/65536ths),

18  14TORRENT QUEUE ORDER (integer),

19  15REMAINING (integer in bytes)

],

...

],

"torrentc": CACHE ID** (string integer)

}*/




    //SEZNAM HASH ŠTEVILK TORRENTOV, TO SE UPORABI ZA VSE OPERACIJE
  private  ArrayList<String>hashList = new ArrayList<>();
    //SEZNAM IMENOV TORRENTOV, NI UPORABE V OPERACIJAH, UPORABI SE LE ZA PRIKAZ
   private ArrayList<String>nameList = new ArrayList<>();
    //SEZNAM PROGRESSA, TOREJ KAKO DALEČ JE TORRENT
   private ArrayList<Double>progressList = new ArrayList<>();
    //SEZNAM HITROSTI DOWNLADA
   private ArrayList<Integer>dlList = new ArrayList<>();
    //SEZNAM HITROSTI UPLOADA
   private ArrayList<Integer>uplList= new ArrayList<>();
    //STATUS TORRENTA(KASNEJE BOMO STATUSE RAZDELILI NA AKTIVNE,USTAVLJENE IN KONČANE)
    //AKTIVNI: TRENUTNO IZVAJAJO OPERACIJO
    //USTAVLJENI: ROČNO USTAVLJENI TORRENTI, TRENUTNO SE NE IZVAJAJO KER JE TO UPORABNIK ŽELEL
    //KONČANI: TORRENTI, KI SE NE IZVAJAJO KER SO KONČANI
  private  ArrayList<String>statusList = new ArrayList<>();


    //ZA PREVERJANJE ALI JE TORRENT OD PREJŠNJEGA ZAGONA BIL KONČAN UPORABIMO DATOTEKO, TER JO PRIMERJAMO Z TRENUTNO
    //PRIDOBLJENIM JSONom IZ SPLETA
    //Z handler.postDelayed(x) se pojavi rekurzija vsaki x milisekund
    //TODO: S SHARED PREFERENCES KRMILI REKURZIJO,TRENUTNO IZPISUJE LE DEFAULT VREDNOST
    //ZA USTVARJANJE NOTIFICATION-ov USTVARIMO ZAČASNE ARRAYLISTE, S KATERIMI KASNEJE PRIMERJAMO VREDNOSTI, TOREJ ČE JE PREJ PROGRESSLIST BIL NA MANJ KOT 100%
    //OB NASLEDNJEM PREGLEDU PA JE ISTI TORRENT IMEL 100% NA PROGRESSU, POTEM LAHKO JAVIMO NOTIFICATION BUILDERJU, DA JE TORRENT KONČAN
    //OPOMBA: "ISTI" TORRENT JE TISTI TORRENT, KI IMA ISTO HASH ŠTEVILKO
    //TRENUTNO PREVERJAMO VSE TORRENTE, TUDI ŽE PREJ ZAKLJUČENE, SAJ LAHKO TUDI USTAVLJENI TORRENTI OB NEAKTIVNOSTI APLIKACIJE DOSEŽEJO 100%
   private ArrayList<String>tempHashList = new ArrayList<>();
    private  ArrayList<Double>tempProgressList=new ArrayList<>();
   private int stevecRekurzij = 0;
    Handler handler = new Handler();
    ;    private void updateTorrent() throws InterruptedException {
      final  int delay = Integer.parseInt(getApplicationContext().getSharedPreferences("Settings",0).getString("sRefresh","5"))*1000;

        Thread t = new Thread(){
            public void run(){
                System.out.println("DELAY " + delay);
                System.out.println("TEMP NA ZAČETKU NITI" + tempProgressList);
                System.out.println("NON TEMP NA ZAČETKU NITI" + progressList);
                                 String auth = uName + ":" + pw;
                    String authorization = "Basic " + new String(android.util.Base64.encode((auth).getBytes(), android.util.Base64.NO_WRAP));
                    authG = authorization;
                    //PRIDOBIMO COOKIE,TRENUTNO GA ŠE NE UPORABIMO2
                linkG = "http://" + ip + ":" + port + "/gui/";
                cookieG = readWebsite.getCookie(linkG + "token.html");

                    //PRIDOBITEV JSONA,PRED TEM JE TREBA PRIDOBITI STRING TOKEN IN GA UPORABITI V URLJU. PREKO INTENT.PUTEXTRA IZ GLAVNE
                    //AKTIVNOSTI PRIDOBIMO IP,USERNAME IN PASSWORD. USERNAME IN PASSWORD ENKRIPTIRAMO Z BASE64 IN GA KASNEJE POŠLJEMO V HTTP HEADERJU
                    //

                    //LOKALNA SPREMENLJIVKA TOKEN
                    String tokenL;

                    tokenL = readWebsite.pridobiToken(linkG + "token.html",authG,cookieG);
                System.out.println("TOKEN L " + tokenL);
                JSONObject json=null;
                    if(tokenL!=null) {
                        json = readWebsite.seznamTorrentov(linkG + "?list=0&token=" + tokenL, authorization);
                    }

                    //System.out.println("JSON V UPDATETORRENT" + json);
                    if (json == null) {
                        System.out.println("PRIDOBLJEN NI BIL NOBEN JSON");

                    } else {

                        try {
                            //ZNOTRAJ POLJA TORRENTI JE DOLOČENO ŠTEVILO POLIJ, KATERI VSEBUJEJO PODATKE, KI JIH POTEBUJEMO
                            JSONArray jsonTorrenti = json.getJSONArray("torrents");
                            JSONArray jsonPodatki = null;

                            System.out.println("DOLZINA" + jsonTorrenti.length());
                            //PRIDOBIMO TORRENTE, VSAK TORRENT IMA SVOJO ŠTEVILKO HASH,KI JO KASNEJE UPORABIMO ZA IZVRŠEVANJE OPERACIJ NAD TORRENTOM
                            //SLEDI RAZČLENJEVANJE VSAKEGA TORRENTA NA SVOJE ATRIBUTE.
                            for (int i = 0; i < jsonTorrenti.length(); i++) {
                                jsonPodatki = jsonTorrenti.getJSONArray(i);
                                //NA SEZNAM HASH ŠTEVILK DODAMO 0.ti STRING VSAKEGA TORRENTA
                                hashList.add(jsonPodatki.getString(0));
                                //NA SEZNAM IMEN DODAMO 2. STRING VSAKEGA TORRENTA
                                nameList.add(jsonPodatki.getString(2));
                                //PROGRESS JE PODAN V MILI, TOREJ JE TREBA ŠE FORMATIRAT V PROCENTE
                                progressList.add(jsonPodatki.getDouble(4) / 10);
                                //
                                dlList.add(jsonPodatki.getInt(8));
                                uplList.add(jsonPodatki.getInt(9));
                                statusList.add(jsonPodatki.getString(21));

                            }

                            System.out.println("STATUS LIST MED PRIDOBIVANJEM" + statusList);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                if(stevecRekurzij!=0){
                    torrentList.clear();
                    torrentList1.clear();
                    torrentList2.clear();
                    torrentList3.clear();
                }

                prepareTorrentData();

                nameList.clear();
                hashList.clear();
                uplList.clear();
                dlList.clear();
                statusList.clear();
                progressList.clear();



                stevecRekurzij++;
                System.out.println("STEVEC REKURZIJ " + stevecRekurzij);
                mAdapter.notifyDataSetChanged();
                mAdapter1.notifyDataSetChanged();
                mAdapter2.notifyDataSetChanged();
                mAdapter3.notifyDataSetChanged();


                handler.postDelayed(this, delay);



            }

        };
               t.start();

                t.join();
    }



//METODA S KATERO SPARSEAMO JSON, TER POSTAVIMO PODATKE NA PRAVO MESTO
    private void prepareTorrentData() {
        //VREDNOSTI SHRANIMO NA TEMP ARRAYLISTA, PO TEM KO SHRANIMO, SE VREDNOSTI NA OSTALIH ARRAYLISTIH IZBRIŠE
        //ČE SE NIT NE IZVAJA PRVIČ, POMENI DA IMAMO OD PREJ SHRANJENE VREDNOSTI, TORREJ ARRAYLISTA NISTA PRAZNA

        //KASNEJE DOBITA ARRAYLISTA NOVE VREDNOSTI, PRIDOBLJENE IZ JSONA
        //PRVO PREVERIMO ALI STA DVA HASHA ENAKA
        //SKLEPAMO DA SE LAHKO MED DVEMA ITERACIJAMA SPREMENI INDEKS HASHOV
        //ČE STA DVA HASHA ENAKA, PREVERIMO ALI JE STARA VREDNOST PROGRESSA TORRENTA Z ENAKIM HASHOM
        // DRUGAČNA KOT 100 TER ALI JE OBENEM NOVA VREDNOST TORRENTA Z ENAKIM HASHOM ENAKA 100
        //TO POMENI DA JE OB PREJŠNJEM PREGLEDU TORRENT BIL NEDOKONČAN, NATO PA SE JE KONČAL

        if(stevecRekurzij!=0) {
            for(int i=0;i<tempHashList.size()-1;i++){
                System.out.println("TEMP HASH LIST" + tempHashList.get(i));
                for(int j=0;j<tempHashList.size()-1;j++){
                    System.out.println("HASH LIST" + hashList.get(j));
                    if(tempHashList.get(i).equals(hashList.get(j))){
                        if(tempProgressList.get(i)!=100&&progressList.get(j)==100){
                            System.out.println("NOTIFICATION TEMP" + tempProgressList.get(i));
                            System.out.println("NOTIFICATION HASH" + progressList.get(j));

                            System.out.println("XD");
                            System.out.println("PRIŠLO NAJ BI DO NOTIFICATIONA");
                            notification(nameList.get(j).substring(0,20));
                            System.out.println("NOTIFICATION!?");
                        }
                    }
                }
            }
        }
        System.out.println("TEMP PROGRESS LIST PREDEN NASTAVIMO VREDNOSTI" + tempProgressList);

        tempProgressList = (ArrayList<Double>)progressList.clone();
        tempHashList = (ArrayList<String>)hashList.clone();
        System.out.println("TEMP PROGRESS LIST PO TEM KO NASTAVIMO VREDNOSTI" + tempProgressList);
        System.out.println("NON-TEMP HASH" + progressList);
        System.out.println("UPLIST???" + uplList);
        System.out.println("HASH SIZE" + hashList.size());
        for(int i =0;i<hashList.size();i++){
          torrent torrent = new torrent(hashList.get(i),uplList.get(i),dlList.get(i),nameList.get(i), statusList.get(i),zbrisi,progressList.get(i));
            torrentList.add(torrent);

        }
        System.out.println("PREPARE DATA KONCANO");
        //BREZ TEGA SE NE POSODABLJA
        mAdapter.notifyItemRangeChanged(0, torrentList.size());

        mAdapter.notifyItemInserted(torrentList.size() - 1);

        System.out.println("PROBLEM Z PREPARETORRENTDATA");
        prepareFilter();
    }
    //METODA S KATERO NASTAVIMO FILTER
    //PRIDOBIMO STATUSE TORRENTOV TER JIH NASTAVIMO POD PRAVE TAB HOSTE
    //torrentList3: SEM GREJO KONČANI TORRENTI
    //torrentList2: PAVZIRANI ALI USTAVLJENI TORRENTI
    //torrentList1: Aktivni torrenti,torej tisti ki imajo download ali upload speed različen od 0
    //torrentList: VSI TORRENTI, SE NE FILTRIRA
    private void prepareFilter(){

        for(int i = 0;i<statusList.size();i++) {

            if(uplList.get(i)!=0 || dlList.get(i)!=0){
                torrent torrent = torrentList.get(i);
                torrentList1.add(torrent);
            }

            if(statusList.get(i).contains("Stopped")||statusList.get(i).contains("Paused")){
                torrent torrent = torrentList.get(i);
                torrentList2.add(torrent);
            }
            if(statusList.get(i).equals("Finished 100.0 %")||(statusList.get(i).equals("Seeding 100.0 %")&&uplList.get(i)==0)){
                torrent torrent = torrentList.get(i);
                torrentList3.add(torrent);
            }



        }
        System.out.println("PROBLEM Z ADAPTERJI");
        mAdapter.notifyItemRangeChanged(0, torrentList1.size());

        mAdapter.notifyItemInserted(torrentList1.size() - 1);

        mAdapter1.notifyItemRangeChanged(0, torrentList1.size());

        mAdapter1.notifyItemInserted(torrentList1.size() - 1);

        mAdapter2.notifyItemRangeChanged(0, torrentList2.size());

        mAdapter2.notifyItemInserted(torrentList2.size() - 1);
        mAdapter3.notifyItemRangeChanged(0, torrentList3.size());

        mAdapter3.notifyItemInserted(torrentList3.size() - 1);
        System.out.println("KONCANO FILTRIRANJE");
        /*                    torrent = torrentList.get(i);
                    torrentList3.add(torrent);
                   */
    }
        //METODA KI SE IZVRŠI OB KLIKU NA KOŠ
        //PRIDOBIMO INDEX TORRENTA NA KATEREGA SMO KLIKNILI,NATO SLEDIMO URL-ju, KI MU PRIPIŠEMO HASH ŠTEVILKO TORRENTA
    //  http://[iP]:[PORT]/gui/?action=remove&hash=[TORRENT HASH]
    //Ok: http://localhost:1234/gui/?token=PF6L4VWuwWvyzYNh6Bng7ZeQxLOSG4f5zA7sK78bSqNjN4J7wZYXrLDUgA&action=start&hash=66021660DAEF1EAE71F8BDAA29CA9C153984A421

    //PRIMER: http://192.168.1.6:8080/gui/?token=cU_eutPMxFXoonWy7ZP0KMlTwoNvs_GMNbTP47-aCSDiVDZ2WwB7hmhEYVgAAAAA&action=stop&hash=5B3690B3DC450E46D75C3A4D417CD94B1D0B7572

    public void deleteTorrent(String hash){


        String tokenL;
        System.out.println("COOKIE Z KLICOM METODE" + readWebsite.getCookie("http://" + linkG + "/gui/token.html"));
        tokenL = readWebsite.pridobiToken(linkG + "token.html",authG,cookieG);
        System.out.println("TOKEN V DELETETORRENT" + tokenL);
        readWebsite.action(linkG + "?token="+ tokenL + "&action=removedata&hash="+hash,authG,cookieG);

    }
    public void changeStatus(String hash,String status){
        String tokenL;
        tokenL= readWebsite.pridobiToken(linkG + "token.html",authG,cookieG);
        if(status.equals("active")){
            readWebsite.action(linkG + "?token=" + tokenL + "&action=stop&hash=" + hash,authG,cookieG);
        }
        else{
            readWebsite.action(linkG + "?token=" + tokenL + "&action=start&hash=" + hash,authG,cookieG);
        }
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prikaz, menu);
        return true;
    }

    public void notification(String x){
        mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        mBuilder.setContentTitle("Torrent je končal z prenašanjem!");
        mBuilder.setContentText(x + "Je končal z prenosom.");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY)
    {


        if(velocityX == 0.0 && velocityY > 0.0001)
        {
            System.out.println("++++++++");

        }
        else if(velocityY == 0 && velocityX > 0.0001)
        {
         System.out.println("++++++++++++++++++++++++++++++");
        }

        return false;
    }




}

