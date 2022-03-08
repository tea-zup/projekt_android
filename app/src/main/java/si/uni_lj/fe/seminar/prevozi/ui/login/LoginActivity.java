package si.uni_lj.fe.seminar.prevozi.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import si.uni_lj.fe.seminar.prevozi.Main_page;
import si.uni_lj.fe.seminar.prevozi.R;

import si.uni_lj.fe.seminar.prevozi.databinding.ActivityLoginBinding;
import si.uni_lj.fe.seminar.prevozi.AsyncTaskExecutor;
import android.view.inputmethod.InputMethodManager;


import android.content.Intent;

import org.json.JSONObject;
import si.uni_lj.fe.seminar.prevozi.Authentication;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    // referenca na glavno aktivnost (za uporabo v notranjih razredih)
    LoginActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        this.activity = this;


        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

                //skrij tipkovnico
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                //Shrani ime trenutnega uporabnika
                Authentication.setAccessToken(activity, usernameEditText.getText().toString(), "CURRENT_USER");

                //preveri, ce sta user & pass ok
                new AsyncTaskExecutor().execute(new VpisPrijavnihPodatkov(usernameEditText.getText().toString(), passwordEditText.getText().toString(), activity),
                        (cookie) -> {auth_cookie_saved(cookie);});

            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void obvestiSToastom(String obvestilo){
        Context context = getApplicationContext();
        CharSequence text = obvestilo;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void auth_cookie_saved(String ac){ //sharni auth_cookie v lokalno shrambo in preusmeri uporabnika v aplikacijo

        if (ac.equals("404")){
            obvestiSToastom("Napačno ime / geslo."); //toast vcasih dela, ce ne dela naredi nov virutal device
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class); //osveži aktivnost
            startActivity(intent);
        }
        else {

            try {
                JSONObject ac_obj = new JSONObject(ac);
                String auth_cookie = ac_obj.getString("auth_cookie");
                Authentication.setAccessToken(this, auth_cookie, "AUTH_COOKIE");
            } catch (Throwable t) {
                Log.d("MyTag", "Could not parse malformed JSON: \"" + ac + "\"");
            }
            obvestiSToastom("Prijava uspešna.");
            Intent intent = new Intent(getApplicationContext(), Main_page.class); //pojdi na drugo aktivnost
            startActivity(intent);
        }
    }
}