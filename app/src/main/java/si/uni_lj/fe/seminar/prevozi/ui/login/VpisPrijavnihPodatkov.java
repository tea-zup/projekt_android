package si.uni_lj.fe.seminar.prevozi.ui.login;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import si.uni_lj.fe.seminar.prevozi.R;
import android.util.Log;

import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

class VpisPrijavnihPodatkov implements Callable<String> {
    private final String uporabnisko_ime;
    private final String geslo;
    private final String urlStoritve;
    private final Activity callerActivity;

    public VpisPrijavnihPodatkov(String uporabnisko_ime, String geslo, Activity callerActivity) {
        this.uporabnisko_ime = uporabnisko_ime;
        this.geslo = geslo;
        this.callerActivity = callerActivity;
        urlStoritve = "http://10.0.2.2/projekt-api/api/uporabniki.php"; //127.0.0.1 je emulator, 10.0.2.2 je bridge na local machine
    }

    @Override
    public String call() {
        ConnectivityManager connMgr = (ConnectivityManager) callerActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        try {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        catch (Exception e){
            //je v manifestu dovoljenje za uporabo omrezja?
            return callerActivity.getResources().getString(R.string.napaka_omrezje);
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                return connect(uporabnisko_ime, geslo);
            } catch (IOException e) {
                Log.d("myTag", String.valueOf(e));
                return callerActivity.getResources().getString(R.string.napaka_storitev);
            }
        }
        else{
            return callerActivity.getResources().getString(R.string.napaka_omrezje);
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the content as a InputStream, which it returns as a string.
    private String connect(String uporabnisko_ime, String geslo) throws IOException {
        URL url = new URL(urlStoritve);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000 /* milliseconds */);
        conn.setConnectTimeout(10000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setRequestProperty("Content-type", "application/json");

        // blokira, dokler ne dobi odgovora

        try {
            JSONObject json = new JSONObject();
            json.put("uporabnisko_ime", uporabnisko_ime);
            json.put("geslo", geslo);
            json.put("tip", "prijava");
            // Starts the query
            OutputStream os = conn.getOutputStream(); //tocno tukaj fail-a
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(json.toString());
            writer.flush();
            writer.close();
            conn.connect();	// Starts the query
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myTag", String.valueOf(e));
        }

        int response = conn.getResponseCode();
        if (response == 200){
            return convertStreamToString(conn.getInputStream()); // Convert the InputStream into a string
        }
        else {
            return String.valueOf(response);
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (IOException e) {
            Log.d("myTag", String.valueOf(e));
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

//    public String JSONString2String(String JsonString){
//        String lestivca = "";
//        try {
//            JSONArray jsonArray = new JSONArray(JsonString);
//            for(int i=0; i<jsonArray.length(); i++){
//                lestivca += jsonArray.getJSONObject(i).getString("vzdevek");
//                lestivca += ": ";
//                lestivca += jsonArray.getJSONObject(i).getString("MAX(rezultat)");
//                lestivca += System.getProperty("line.separator");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return lestivca;
//    }
}

//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import org.json.JSONObject;
//
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.concurrent.Callable;
//
//import si.uni_lj.fe.seminar.prevozi.R;
//
//
//public class VpisPrijavnihPodatkov implements Callable<String> {
//    private final String uporabnisko_ime;
//    private final String geslo;
//    private final String urlStoritve;
//    private final Activity callerActivity;
//
//    public VpisPrijavnihPodatkov(String uporabnisko_ime, String geslo, Activity callerActivity) {
//        this.uporabnisko_ime = uporabnisko_ime;
//        this.geslo = geslo;
//        this.callerActivity = callerActivity;
//        urlStoritve = "http://10.0.2.2/projekt-api/api/uporabniki.php"; //127.0.0.1 je emulator, 10.0.2.2 je bridge na local machine
//    }
//
//    @Override
//    public String call() {
//
//        ConnectivityManager connMgr = (ConnectivityManager) callerActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo;
//
//        try {
//            networkInfo = connMgr.getActiveNetworkInfo();
//        }
//        catch (Exception e){
//            //je v manifestu dovoljenje za uporabo omrezja?
//            return callerActivity.getResources().getString(R.string.napaka_omrezje);
//        }
//        if (networkInfo != null && networkInfo.isConnected()) {
//            try {
//                int responseCode = connect(uporabnisko_ime, geslo);
//                if(responseCode == 200){
//                    return callerActivity.getResources().getString(R.string.rest_rezultat_dodan);
//                }
//                else{
//                    return callerActivity.getResources().getString(R.string.rest_nepricakovan_odgovor)+" "+responseCode;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d("myTag", String.valueOf(e));
//                return callerActivity.getResources().getString(R.string.napaka_storitev); //to dobis
//            }
//        }
//        else{
//            return callerActivity.getResources().getString(R.string.napaka_omrezje);
//        }
//    }
//
//    // Given a URL, establishes an HttpUrlConnection and retrieves
//    // the content as a InputStream, which it returns as a string.
//    private int connect(String uporabnisko_ime, String geslo) throws IOException {
//        URL url = new URL(urlStoritve);
//
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setReadTimeout(5000 /* milliseconds */);
//        conn.setConnectTimeout(10000 /* milliseconds */);
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Content-Type", "application/json");
//        conn.setDoInput(true);
//
//        try {
//            JSONObject json = new JSONObject();
//            json.put("uporabnisko_ime", uporabnisko_ime);
//            json.put("geslo", geslo);
//            json.put("tip", "prijava");
//            // Starts the query
//            OutputStream os = conn.getOutputStream(); //tocno tukaj fail-a
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//            writer.write(json.toString());
//            writer.flush();
//            writer.close();
//            os.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            //Log.d("myTag", String.valueOf(e));
//        }
//        return conn.getResponseCode();
//    }
//}
