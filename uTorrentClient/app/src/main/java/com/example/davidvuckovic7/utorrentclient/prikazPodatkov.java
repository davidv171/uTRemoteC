package com.example.davidvuckovic7.utorrentclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class prikazPodatkov extends AppCompatActivity implements GestureDetector.OnGestureListener {
    SharedPreferences sharedpreferences;

    TabHost host;

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
    String uName;
    String pw;
    String ip;
    ImageView stanje;
    ImageView zbrisi;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    String port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_podatkov);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        stanje = (ImageView)findViewById(R.id.stanje);
        zbrisi = (ImageView)findViewById(R.id.zbrisi);
        //ZBRISI IMA VEDNO ENAKO IKONO
        //TODO:DINAMIČNO NASTAVLJANJE SLIKE ZA STANJE GLEDE NA DEJANSKO STANJE
//        stanje.setImageResource(R.drawable.check);
        sharedpreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        System.out.println(sharedpreferences.getAll());
        String ipC=null;
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
        String nPort = sharedpreferences.getString("nPort", "8080");
        String zPort = sharedpreferences.getString("zPort", "32610");
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
        System.out.println("UPLIST!" + uplList);


        if(!uplList.isEmpty()) {
            prepareTorrentData();
            prepareFilter();
        }
    }
//METODA S KATERO NASTAVIMO FILTER
    //PRIDOBIMO STATUSE TORRENTOV TER JIH NASTAVIMO POD PRAVE TAB HOSTE
    //torrentList3: SEM GREJO KONČANI TORRENTI
    //torrentList2: PAVZIRANI ALI USTAVLJENI TORRENTI
    //torrentList1: Aktivni torrenti
    //torrentList: VSI TORRENTI, SE NE FILTRIRA
    private void prepareFilter(){

        for(int i = 0;i<statusList.size();i++) {

            if(statusList.get(i).contains("Stopped")||statusList.get(i).contains("Paused")){
                torrent torrent = torrentList.get(i);
                torrentList2.add(torrent);
            }
            if(uplList.get(i)!=0 || dlList.get(i)!=0){
                torrent torrent = torrentList.get(i);
                torrentList1.add(torrent);
            }
            else{
                torrent torrent= torrentList.get(i);
                torrentList3.add(torrent);
            }



            }
        /*                    torrent = torrentList.get(i);
                    torrentList3.add(torrent);
                    mAdapter3.notifyItemInserted(torrentList3.size() - 1);*/
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
    ArrayList<String>hashList = new ArrayList<>();
    //SEZNAM IMENOV TORRENTOV, NI UPORABE V OPERACIJAH, UPORABI SE LE ZA PRIKAZ
    ArrayList<String>nameList = new ArrayList<>();
    //SEZNAM PROGRESSA, TOREJ KAKO DALEČ JE TORRENT
    ArrayList<Integer>progressList = new ArrayList<>();
    //SEZNAM HITROSTI DOWNLADA
    ArrayList<Integer>dlList = new ArrayList<>();
    //SEZNAM HITROSTI UPLOADA
    ArrayList<Integer>uplList= new ArrayList<>();
    //STATUS TORRENTA(KASNEJE BOMO STATUSE RAZDELILI NA AKTIVNE,USTAVLJENE IN KONČANE)
    //AKTIVNI: TRENUTNO IZVAJAJO OPERACIJO
    //USTAVLJENI: ROČNO USTAVLJENI TORRENTI, TRENUTNO SE NE IZVAJAJO KER JE TO UPORABNIK ŽELEL
    //KONČANI: TORRENTI, KI SE NE IZVAJAJO KER SO KONČANI
    ArrayList<String>statusList = new ArrayList<>();

    //ZA PREVERJANJE ALI JE TORRENT OD PREJŠNJEGA ZAGONA BIL KONČAN UPORABIMO DATOTEKO, TER JO PRIMERJAMO Z TRENUTNO
    //PRIDOBLJENIM JSONom IZ SPLETA
    String filename = "data.json";
    File file = new File(prikazPodatkov.this.getFilesDir(), filename);



    ;    private void updateTorrent() throws InterruptedException {
       Thread t = new Thread(){
            public void run(){
                String auth = uName + ":" + pw;
                System.out.println(auth);
                String link = ip + ":" + port;
               String authorization = "Basic " + new String(android.util.Base64.encode((auth).getBytes(), android.util.Base64.NO_WRAP));
//PRIDOBIMO COOKIE,TRENUTNO GA ŠE NE UPORABIMO2
                readWebsite.getCookie("http://" + link +"/gui/token.html");
                //PRIDOBITEV JSONA,PRED TEM JE TREBA PRIDOBITI STRING TOKEN IN GA UPORABITI V URLJU. PREKO INTENT.PUTEXTRA IZ GLAVNE
                //AKTIVNOSTI PRIDOBIMO IP,USERNAME IN PASSWORD. USERNAME IN PASSWORD ENKRIPTIRAMO Z BASE64 IN GA KASNEJE POŠLJEMO V HTTP HEADERJU
                //
                final JSONObject json = readWebsite.seznamTorrentov("http://" + link + "/gui/?list=0&token=" + readWebsite.pridobiToken("http://" + link +"/gui/token.html",authorization),authorization);
                System.out.println("JSON V UPDATETORRENT" + json);
                if(json == null){
                    System.out.println("PRIDOBLJEN NI BIL NOBEN JSON");

                }
                else{

                    try {
                        //ZNOTRAJ POLJA TORRENTI JE DOLOČENO ŠTEVILO POLIJ, KATERI VSEBUJEJO PODATKE, KI JIH POTEBUJEMO
                        JSONArray jsonTorrenti =  json.getJSONArray("torrents");
                        JSONArray jsonPodatki=null;

                        System.out.println("DOLZINA" + jsonTorrenti.length());
                        //PRIDOBIMO TORRENTE, VSAK TORRENT IMA SVOJO ŠTEVILKO HASH,KI JO KASNEJE UPORABIMO ZA IZVRŠEVANJE OPERACIJ NAD TORRENTOM
                        //SLEDI RAZČLENJEVANJE VSAKEGA TORRENTA NA SVOJE ATRIBUTE.
                        for(int i = 0;i<jsonTorrenti.length();i++){
                            System.out.println("TORRENTI" + jsonTorrenti.get(i));
                           jsonPodatki = jsonTorrenti.getJSONArray(i);
                            //NA SEZNAM HASH ŠTEVILK DODAMO 0.ti STRING VSAKEGA TORRENTA
                            hashList.add(jsonPodatki.getString(0));
                            //NA SEZNAM IMEN DODAMO 2. STRING VSAKEGA TORRENTA
                           nameList.add(jsonPodatki.getString(2));
                            //PROGRESS JE PODAN V MILI, TOREJ JE TREBA ŠE FORMATIRAT V PROCENTE
                            progressList.add(jsonPodatki.getInt(4)/10);
                            //
                            dlList.add(jsonPodatki.getInt(8));
                            uplList.add(jsonPodatki.getInt(9));
                            statusList.add(jsonPodatki.getString(21));
                        }
                        System.out.println("HASHI" + hashList);
                        System.out.println("IMENA" + nameList);
                        System.out.println("PROGRESSI"  + progressList);
                        System.out.println("UPL SPEED IN DL SPEED");
                        System.out.println(uplList);
                        System.out.println(dlList);
                        System.out.println("STATUSI"  + statusList);







                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
               t.start();
        //NE NADALJUJE DOKLER NE KONČA NITI
        t.join();


    }


//METODA S KATERO SPARSEAMO JSON, TER POSTAVIMO PODATKE NA PRAVO MESTO
    private void prepareTorrentData() {
  //TODO: GLEDE NA STATUS NASTAVIMO SLIKO S POMOČJO SPREMENLJIVKE STATUSST

        //    public torrent(double upSpeed,double downSpeed,String name,ImageView stanje, ImageView zbriši, double progress){
        /*

        torrent torrent = new torrent(13.5, 66.5, "Witcher 3", "downloading", zbrisi, 99.5);
     torrentList.add(torrent);

        torrent = new torrent(100, 99, "Witcher 4", "uploading", zbrisi, 45);
        torrentList.add(torrent);

        torrent = new torrent(0,0,"Hotline Bling","stopped",zbrisi,100);

       // mAdapter.notifyDataSetChanged();
        mAdapter.notifyItemInserted(torrentList.size() - 1);
        */
        System.out.println("UPLIST???" + uplList);
        torrent torrent = null;
        System.out.println("HASH SIZE" + hashList.size());
        for(int i =0;i<hashList.size();i++){

            System.out.println("XD");
           torrent = new torrent(uplList.get(i),dlList.get(i),nameList.get(i), statusList.get(i),zbrisi,progressList.get(i));
            torrentList.add(torrent);

        }


        mAdapter.notifyItemInserted(torrentList.size() - 1);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prikaz, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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


    public void startStop(View V) {

    }


}

