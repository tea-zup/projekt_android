package si.uni_lj.fe.seminar.prevozi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;


public class DobiRezervacije implements Callable<String> {
    private final String uporabnisko_ime;
    private final String auth_cookie;
    private final String urlStoritve;
    private final Activity callerActivity;

    public DobiRezervacije(String uporabnisko_ime, String auth_cookie, Activity callerActivity) {
        this.uporabnisko_ime = uporabnisko_ime;
        this.auth_cookie = auth_cookie;
        this.callerActivity = callerActivity;
        urlStoritve = "http://10.0.2.2/projekt-api/api/rezervacije.php"; //127.0.0.1 je emulator, 10.0.2.2 je bridge na local machine
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
                return connect(uporabnisko_ime, auth_cookie);
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
    private String connect(String uporabnisko_ime, String auth_cookie) throws IOException {
        URL url = new URL(urlStoritve);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000 /* milliseconds */);
        conn.setConnectTimeout(10000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("auth-user", uporabnisko_ime);
        conn.setRequestProperty("auth-cookie", auth_cookie);
        conn.setDoInput(true);

        try {
            conn.connect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myTag", String.valueOf(e));
        }
        int response = conn.getResponseCode();
        String tmp = convertStreamToString(conn.getInputStream());
        return JSONString2String(tmp);
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

    public String JSONString2String(String JsonString){ //za prikaz rezervacije
        String seznam_rezervacij = "";
        String podatki_rezervacije[] = {"Kraj odhoda", "Kraj prihoda", "Čas odhoda", "Št. oseb", "Način plačila", "Voznik"};
        String podatki_rezervacije_tag[] = {"kraj_odhoda", "kraj_prihoda", "cas_odhoda", "st_oseb", "nacin_placila", "voznik"};
        try {
            JSONArray jsonArray = new JSONArray(JsonString);
            for(int i=0; i<jsonArray.length()-2; i++){ //zadnji dve polji sta nacin id in voznik, tega ne prikazemo
                for(int j=0; j<podatki_rezervacije.length; j++) {
                    seznam_rezervacij += podatki_rezervacije[j];
                    seznam_rezervacij += ": ";
                    seznam_rezervacij += jsonArray.getJSONObject(i).getString(podatki_rezervacije_tag[j]);
                    seznam_rezervacij += System.getProperty("line.separator");
                }
                seznam_rezervacij += System.getProperty("line.separator");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return seznam_rezervacij;
    }
}