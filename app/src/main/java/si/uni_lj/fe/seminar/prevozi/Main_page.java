package si.uni_lj.fe.seminar.prevozi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import si.uni_lj.fe.seminar.prevozi.ui.login.LoginActivity;
import si.uni_lj.fe.seminar.prevozi.DobiRezervacije;


public class Main_page extends AppCompatActivity {

    Main_page activity;
    ListView listView;
    TextView textView;
    public String[] listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        this.activity = this;
        listView=(ListView)findViewById(R.id.listView);
        textView=(TextView)findViewById(R.id.textView);

        String auth_cookie = Authentication.getAccessToken(this, "AUTH_COOKIE");
        //Log.d("MyTag", auth_cookie);
        String current_user = Authentication.getAccessToken(this, "CURRENT_USER");
        //Log.d("Mytag", current_user);

        new AsyncTaskExecutor().execute(new DobiRezervacije(current_user, auth_cookie, activity),
                (rezultat) -> {fun(rezultat);});

    }
    public String[] JSONString2StringArray(String JsonString){ //za prikaz rezervacije
        String podatki_rezervacije[] = {"Kraj odhoda", "Kraj prihoda", "Čas odhoda", "Št. oseb", "Način plačila", "Voznik"};
        String podatki_rezervacije_tag[] = {"kraj_odhoda", "kraj_prihoda", "cas_odhoda", "st_oseb", "nacin_placila", "voznik"};
        String rezervacija = "";
        String seznam_rezervacij[] = new String[7];

        try {
            JSONArray jsonArray = new JSONArray(JsonString);
            for(int i=0; i<7; i++){ //zadnji dve polji sta id rezervacije in prevoza, tega ne prikazemo
                for(int j=0; j<podatki_rezervacije.length; j++) {
                    rezervacija += podatki_rezervacije[j];
                    rezervacija += ": ";
                    rezervacija += jsonArray.getJSONObject(i).getString(podatki_rezervacije_tag[j]);
                    rezervacija += System.getProperty("line.separator");
                }
                seznam_rezervacij[i] = rezervacija;
                rezervacija = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return seznam_rezervacij;
    }

    public void fun (String rezultat) {
        listItem = JSONString2StringArray(rezultat);

//        try {
//            Log.d("Mytag3", listItem[0]);
//        }
//        catch (Exception e) {
//            Log.d("myTag", String.valueOf(e));
//        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.my_list, listItem);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }
}