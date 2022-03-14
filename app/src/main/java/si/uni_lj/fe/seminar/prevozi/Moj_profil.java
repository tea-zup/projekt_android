package si.uni_lj.fe.seminar.prevozi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class Moj_profil extends AppCompatActivity {

    Moj_profil activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_profil);

        this.activity = this;
        TextView izpis = findViewById(R.id.izpis);


        String auth_cookie = Authentication.getAccessToken(this, "AUTH_COOKIE");
        String current_user = Authentication.getAccessToken(this, "CURRENT_USER");

        new AsyncTaskExecutor().execute(new DobiProfil(current_user, auth_cookie, activity),
                (rezultat) -> {izpis.setText(JSONString2String(rezultat));});

    }

    public String JSONString2String(String JsonString){ //za prikaz rezervacije

        String podatki_profila[] = {"Uporabniško ime", "Ime", "Priimek", "Email"};
        String podatki_profila_tag[] = {"uporabnisko_ime", "ime", "priimek", "email"};
        String info = "";

        try {
            JSONObject jsonArray = new JSONObject(JsonString);

            for (int j=0; j<podatki_profila.length; j++){ //one, 'hard coded' array
                info += podatki_profila[j];
                info += ": ";
                info += jsonArray.getString(podatki_profila_tag[j]);
                info += System.getProperty("line.separator");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }
}