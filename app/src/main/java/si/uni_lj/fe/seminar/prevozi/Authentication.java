package si.uni_lj.fe.seminar.prevozi;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class Authentication { //set / get authentication cookies
    public static void setAccessToken(@NonNull Context context, String token, String TokenName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TokenName, token); //name & value
        editor.apply();
    }

    public static String getAccessToken(@NonNull Context context, String TokenName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        return sharedPreferences.getString(TokenName, null);
    }
}