package nanowrimo.onishinji.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;

/**
 * Created by Silwek on 29/03/2015.
 */
public class SplashscreenActivity extends Activity {

    Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase = new Database(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                checkEmptyDatabase();
            }

        }, 2000);//ms
    }



    private void checkEmptyDatabase() {
        new Thread() {
            public void run() {
                ArrayList<String> users = mDatabase.getUsers();

                final Intent intent;
                if (users.size() == 0) {
                    intent = new Intent(SplashscreenActivity.this,WelcomeActivity.class);
                }else{
                    //TODO init WritingSessionHelper
                    intent = new Intent(SplashscreenActivity.this,MyActivity.class);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }.start();
    }
}
