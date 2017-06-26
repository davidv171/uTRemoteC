package com.example.davidvuckovic7.utorrentclient;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by davidvuckovic7 on 25.10.2016.
 */

class pridobitevPodatkov extends AsyncTask<String, Void, String> {
   private URLConnection connection=null;
    protected String doInBackground(String result) {

        return  null;
    }


    protected void onPostExecute() {



    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }
   private String cookie;


    //VPIS V  SPLETNO STRAN, S TO METODO BI NAJ DOBILI TUDI PIŠKOTKE


     public String getCookie(String url) {
         System.out.println();

         try {
             connection = (new URL(url)).openConnection();

         } catch (IOException e) {
             return "Malformed URL";
         }

         //BREZ TEGA NE DOBIŠ COOKIE-ja, pridobljeno iz sledenja HTTP pogovoru med strežnikom in browserjem.
         connection.setDoOutput(true);
         connection.setDoInput(true);


         System.out.println("COOOOOOOKIE");
         //  conn.setRequestProperty("Authorization", userpass);
         //conn.setInstanceFollowRedirects(true);
         System.out.println("URL CONNECTIONA " + connection.getURL());
                 cookie = connection.getHeaderField("Set-Cookie");

         System.out.println("HEADER FIELDS" +  connection.getHeaderFields());
         return cookie;
     }




    //metoda, s katero naj bi v ozadju pridobili Token
    // da nam ne vrne 401 moramo poskrbeti za piškote(TODO)


    protected String pridobiToken(String result,String authorization,String cookieW) {
        String token = null;
        try {


            //DRUGA POVEZAVA, KI DOBI TOKEN, PONOVNO NALOŽI TOKEN.HTML
            connection = (new URL(result)).openConnection();
            connection.setRequestProperty("Authorization", authorization);
            connection.setRequestProperty("Cookie",cookieW);
            System.out.println("COOKIE V PRIDOBI TOKEN" + cookieW);
            System.out.println("HEADER FIELDS" + connection.getHeaderFields());
            //Accept,accept-encoding,accept-language,cookie, upgrade insecure requests, user-agent



            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            //GET:

            //connection2.setRequestProperty("Authorization", "krnekaj");

        //PREBERI HTML STREAM
            InputStream in = null;
            try {
                in = (InputStream) connection.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStreamReader isw = new InputStreamReader(in);
            //POMOZNI STRING ZA LAZJE IZLUŠČEVANJE TOKENA,UPORABA KOT APPEND
            String token2=null;
            int html= 0;
            int i = 0;
            try {
                html= isw.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (-1 != html) {
                char current = (char) html;

                try {
                    i++;

                        html = isw.read();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(token!=null){token2 = token;}
                token = token2 + (String.valueOf(current));
                System.out.print(current);
            }
            System.out.println("\n");
            //PARSE THE HTML,IZLOČIMO SAMO TOKEN, ta se vedno nahaja med 48 in 112tim znakom, html, koda je prav tako stalna
            token = token.substring(48,112);
            System.out.println("TOKEN" + token);


            //
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return token;

    }
    public JSONObject seznamTorrentov(String url,String authorization){

        try {
           connection = (new URL(url)).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        //GET:
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Cookie", cookie);
       connection.setRequestProperty("Authorization", authorization);
        System.out.println("SEZNAMTORRENTOV" + connection.getHeaderFields());

        try {
            connection.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer json = new StringBuffer(1024);
        String tmp = "";
        try {
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject data = null;
        try {
            data = new JSONObject(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // This value will be 404 if the request was not
        // successful

        System.out.println(data.toString());
        return data;

    }
  //  http://[iP]:[PORT]/gui/?action=remove&hash=[TORRENT HASH]


    public void action(String url,String authorization,String cookieW){
        try {
            connection = (new URL(url)).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Cookie", cookieW);
        connection.setRequestProperty("Authorization", authorization);
        System.out.println("DELETE HEADER FIELDS" + connection.getHeaderFields());
        System.out.println("AUTHOR"+ authorization);


    }


    @Override
    protected void onPreExecute() {


    }

    @Override
    protected void onProgressUpdate(Void... values) {}
}

