package si.uni_lj.fe.seminar.prevozi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import si.uni_lj.fe.seminar.prevozi.ui.login.LoginActivity;
import si.uni_lj.fe.seminar.prevozi.DobiRezervacije;


public class Main_page extends AppCompatActivity {

    Main_page activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        this.activity = this;
        TextView izpis = findViewById(R.id.izpis);

        String auth_cookie = Authentication.getAccessToken(this, "AUTH_COOKIE");
        //Log.d("MyTag", auth_cookie);
        String current_user = Authentication.getAccessToken(this, "CURRENT_USER");
        //Log.d("Mytag", current_user);

        new AsyncTaskExecutor().execute(new DobiRezervacije(current_user, auth_cookie, activity),
                (rezultat) -> {izpis.setText(rezultat);});

    }
}