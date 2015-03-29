package nanowrimo.onishinji.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
public class SplashscreenActivity extends Activity {

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

        final Intent intent;
        if (users.size() == 0 || PreferencesHelper.isFirstLaunch(this)) {
            intent = new Intent(SplashscreenActivity.this,WelcomeActivity.class);
        }else{
            //init WritingSessionHelper
            WritingSessionHelper.getInstance().setSessionName(PreferencesHelper.getSessionName(this));
            WritingSessionHelper.getInstance().setSessionStart(PreferencesHelper.getSessionStart(this));
            WritingSessionHelper.getInstance().setUserName(PreferencesHelper.getUserName(this));

            intent = new Intent(SplashscreenActivity.this,MyActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
