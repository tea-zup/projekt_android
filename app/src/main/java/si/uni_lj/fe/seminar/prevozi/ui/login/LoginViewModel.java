package si.uni_lj.fe.seminar.prevozi.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;
import android.content.Intent;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import si.uni_lj.fe.seminar.prevozi.Main_page;
import si.uni_lj.fe.seminar.prevozi.data.LoginRepository;
import si.uni_lj.fe.seminar.prevozi.data.Result;
import si.uni_lj.fe.seminar.prevozi.data.model.LoggedInUser;
import si.uni_lj.fe.seminar.prevozi.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            //loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName()))); //exits the app
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!username.isEmpty() && !password.isEmpty()) {
            loginFormState.setValue(new LoginFormState(true));
        }
        else {
            loginFormState.setValue(new LoginFormState(false));
        }
    }

}