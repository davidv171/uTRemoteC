package com.example.davidvuckovic7.utorrentclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class mainA extends AppCompatActivity {
 EditText username;
    EditText password;
    EditText ip;
    //S TEMI SPREMENLJIVKAMI BOMO PRENESELI VREDNOSTI V PRIDOBITEV PODATKOV, SAJ SE UPORABIJO V URL-jih ali headerjih
    String uName;
    String pw;
    String ipA;
    //TE SPREMENLJIVKE SE BOJO SHRANJEVALE V SHARED PREFERENCES


    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        ip = (EditText)findViewById(R.id.ip);
        //TODO: PRIDOBI VREDNOSTI IZ SETTINGSOV, TRENUTNO SE VZAME DEFAULT VREDNOST
        if(sharedpreferences.getBoolean("sUname",true)==true){
            System.out.println("RES JE XD");
        }
        else{
            System.out.println("XD pni res");
        }
    username.setText(sharedpreferences.getString("username", ""));
      password.setText(sharedpreferences.getString("password", ""));
       ip.setText(sharedpreferences.getString("ip", ""));
        //V EDIT TEXT ZA IP LAHKO VNESEMO SAMO ŠTEVILKE TER . , ZA TO SMO POSKRBELI V
        System.out.println("Spreferences" + sharedpreferences.getAll());
        //JAVIMO ČE UPORABNIK NIMA POVEZAVE
        if(preveriPovezavo()==0){
            Toast toast = Toast.makeText(mainA.this, "NI IP POVEZAVE!", Toast.LENGTH_LONG);
            toast.show();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        if (id == R.id.action_pobrisi){
            ip.setText("");
            username.setText("");
            password.setText("");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        System.exit(0);
    }


    public void naprej(View V){

        /*Intent myintent=new Intent(Info.this, GraphDiag.class).putExtra("<StringName>", value);
startActivity(myintent);
use the below code in child activity

String s= getIntent().getStringExtra(<StringName>);*/
        Bundle extras = new Bundle();
        // S TO KODO POŠLJEMO AKTIVNOSTI PRIDOBITEV PODATKOV PODATKE O GESLU, IPJU TER UPORABNIŠKEMU IMENU
        //ODSTRANIMO SPACE-E V UPORABNIŠKEM VNOSU

        uName = String.valueOf(username.getText());
        uName.replaceAll(" ","");
        pw = String.valueOf(password.getText());
        pw.replaceAll(" ","");
        ipA = String.valueOf(ip.getText());
        ipA.replaceAll(" ","");
        System.out.println("IPA" + ipA);
        extras.putString("ip",ipA);
        extras.putString("username",uName);
        extras.putString("password", pw);
        //PREDEN SE POSTAVIMO V NOVO AKTIVNOST SHRANIMO VSE TRI VREDNOSTI V SHARED PREFERENCES
        //Z NAMENOM DA OB VSAKEM ZAGONU UPORABNIK NE BO RABIL PONOVNO VPISOVATI
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("ip", ipA);
        editor.putString("username",uName);
        editor.putString("password", pw);
        System.out.println("SETTINGS:" + sharedpreferences.getAll());
        //BREZ TEGA NE SHRANI
        editor.commit();
        //TODO: UPORABNIK SI BO LAHKO IZBRAL KATERE VREDNOSTI SE MU BODO SHRANILE


        //PRIDOBIMO IP NAPRAVE, TER TAKO PREVERIMO ALI IMA POVEZAVO Z INTERNETOM
        //ČE NI IP-JA TOASTER TO UPORABNIKU JAVI
        //PRAV TAKO PREVERJAMO, ALI SMO VNESLI VSE PODATKE V USTREZNE EDIT-TEXT-BOXE
        if((preveriPovezavo()==0) && (username.equals(""))||password.equals("")||ip.equals("")) {
            Toast toast = Toast.makeText(mainA.this, "NIMATE POVEZAVE ALI PA NISTE VNESLI ENEGA IZMED OKVIRJEV", Toast.LENGTH_LONG);
            toast.show();
        }
        else{
            Intent intent = new Intent(this, prikazPodatkov.class).putExtras(extras);
            startActivity(intent);

        }

    }
    //METODA KI SKRIJE TIPKOVNICO KLICANA VEDNO KO KLIKNEMO OZADJE
    public void skrijTipkovnico(View v){

        hideSoftKeyboard(mainA.this);
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
public int preveriPovezavo(){
    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    String ipACTIVITY = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    if (ipACTIVITY.equals("")||ipACTIVITY.equals("null")){
        return 0;
    }
    else {
        return 1;
    }
}

}
