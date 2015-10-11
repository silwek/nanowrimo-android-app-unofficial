package nanowrimo.onishinji.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.BusManager;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.utils.PreferencesHelper;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 29/03/2015.
 */
public class SplashscreenActivity extends AppCompatActivity {

    Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        Crashlytics.start(getApplicationContext());
        BusManager.getInstance();
        HttpClient.getInstance().setContext(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkEmptyDatabase();
    }

    private void checkEmptyDatabase() {
        mDatabase = new Database(this);
        ArrayList<String> users = mDatabase.getUsers();

        if (users.size() == 0 || PreferencesHelper.isFirstLaunch(this)) {
            newSession();
        } else {
            //init WritingSessionHelper
            WritingSessionHelper.getInstance().restoreConfig(this);

            if (WritingSessionHelper.getInstance().isSessionExpired()) {
                newSession();
            } else {
                mainActivity();
            }
        }
    }

    protected void newSession() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    protected void mainActivity() {
        Intent intent = new Intent(this, MyActivity.class);
        startActivity(intent);
        finish();
    }
}
