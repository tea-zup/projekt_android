package si.uni_lj.fe.seminar.prevozi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class Moje_ponudbe extends Main_page {

    Moje_ponudbe activity;
    ListView listView;
    TextView textView;
    public String[] listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moje_ponudbe);

        this.activity = this;
        listView=(ListView)findViewById(R.id.listView);
        textView=(TextView)findViewById(R.id.textView);


        String auth_cookie = Authentication.getAccessToken(this, "AUTH_COOKIE");
        String current_user = Authentication.getAccessToken(this, "CURRENT_USER");

        new AsyncTaskExecutor().execute(new DobiPonudbe(current_user, auth_cookie, activity),
                (rezultat) -> {fun(rezultat);});

    }
    @Override //moje_ponudbe extends main_activity, ki ima tudi to funkcijo
    public String[] JSONString2StringArray(String JsonString){ //za prikaz rezervacije
        String podatki_rezervacije[] = {"Kraj odhoda", "Kraj prihoda", "Čas odhoda", "Cena", "Prosta mesta", "Zasedena mesta"};
        String podatki_rezervacije_tag[] = {"kraj_odhoda", "kraj_prihoda", "cas_odhoda", "cena", "prosta_mesta", "zasedena_mesta"};
        String rezervacija = "";

        try {
            JSONArray jsonArray = new JSONArray(JsonString);
            String seznam_rezervacij[] = new String[jsonArray.length()]; //id nas ne zanima

            for(int i=0; i<jsonArray.length(); i++){ //zadnji dve polji sta id rezervacije in prevoza, tega ne prikazemo
                for(int j=0; j<podatki_rezervacije.length; j++) {
                    rezervacija += podatki_rezervacije[j];
                    rezervacija += ": ";
                    rezervacija += jsonArray.getJSONObject(i).getString(podatki_rezervacije_tag[j]);
                    rezervacija += System.getProperty("line.separator");
                }
                seznam_rezervacij[i] = rezervacija;
                rezervacija = "";
            }
            return seznam_rezervacij;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new String[1]; //dummy value
    }
    @Override //moje_ponudbe extends main_activity, ki ima tudi to funkcijo
    public void fun (String rezultat) {
        listItem = JSONString2StringArray(rezultat);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.my_list, listItem);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }
}