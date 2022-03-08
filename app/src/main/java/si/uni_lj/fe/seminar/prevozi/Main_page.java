package si.uni_lj.fe.seminar.prevozi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class Main_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        String auth_cookie = Authentication.getAccessToken(this, "AUTH_COOKIE");
        Log.d("MyTag", auth_cookie);
        String current_user = Authentication.getAccessToken(this, "CURRENT_USER");
        Log.d("Mytag", current_user);


    }
}