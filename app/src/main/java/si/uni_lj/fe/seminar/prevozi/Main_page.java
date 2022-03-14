package si.uni_lj.fe.seminar.prevozi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import si.uni_lj.fe.seminar.prevozi.ui.login.LoginActivity;


public class Main_page extends AppCompatActivity {

    Main_page activity;
    ListView listView;
    TextView textView;
    public String[] listItem;

    private final int MENU_MOJE_REZERVACIJE = 0;
    private final int MENU_MOJE_PONUDBE = 1;
    private final int MENU_MOJ_PROFIL = 2;
    private final int MENU_ODJAVA = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        this.activity = this;
        listView=(ListView)findViewById(R.id.listView);
        textView=(TextView)findViewById(R.id.textView);

        String auth_cookie = Authentication.getAccessToken(this, "AUTH_COOKIE");
        String current_user = Authentication.getAccessToken(this, "CURRENT_USER");

        new AsyncTaskExecutor().execute(new DobiRezervacije(current_user, auth_cookie, activity),
                (rezultat) -> {fun(rezultat);});

    }
    public String[] JSONString2StringArray(String JsonString){ //za prikaz rezervacije
        String podatki_rezervacije[] = {"Kraj odhoda", "Kraj prihoda", "Čas odhoda", "Št. oseb", "Način plačila", "Voznik"};
        String podatki_rezervacije_tag[] = {"kraj_odhoda", "kraj_prihoda", "cas_odhoda", "st_oseb", "nacin_placila", "voznik"};
        String rezervacija = "";

        try {
            JSONArray jsonArray = new JSONArray(JsonString);
            String seznam_rezervacij[] = new String[jsonArray.length()];
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

    public void fun (String rezultat) {
        listItem = JSONString2StringArray(rezultat);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.my_list, listItem);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, MENU_MOJE_REZERVACIJE, 0, "Moje rezervacije");
        menu.add(0, MENU_MOJE_PONUDBE, 0, "Moje ponudbe");
        menu.add(0, MENU_MOJ_PROFIL, 0, "Moj profil");
        menu.add(0, MENU_ODJAVA, 0, "Odjava");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // switch stavek z testiranjem menijske postavke,ki je bila izbrana
        switch (item.getItemId()) {
            case MENU_MOJE_REZERVACIJE:
                Intent intent_main = new Intent(this, Main_page.class); //preusmeri na glavno stran = stran z rezervacijami
                startActivity(intent_main);
                return true;

            case MENU_MOJE_PONUDBE:
                Intent intent_moje_ponudbe= new Intent(this, Moje_ponudbe.class);
                startActivity(intent_moje_ponudbe);
                return true;

            case MENU_MOJ_PROFIL:
                Intent intent_moj_profil= new Intent(this, Moj_profil.class);
                startActivity(intent_moj_profil);
                return true;

            case MENU_ODJAVA:
                Authentication.setAccessToken(this, "", "AUTH_COOKIE"); //pobrisi auth token
                Authentication.setAccessToken(this, "", "CURRENT_USER");
                finish(); // zaključi aktivnost
                Intent intent_odjava = new Intent(this, LoginActivity.class); //preusmeri na prijavo
                startActivity(intent_odjava);
                return true;
        }
        return true;
    }
}