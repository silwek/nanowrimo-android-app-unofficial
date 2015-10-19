package nanowrimo.onishinji.ui.activity;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import nanowrimo.onishinji.R;

/**
 * Created by Silwek on 11/10/15.
 */
public class ToolbarActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ViewCompat.setElevation(mToolbar, 3);
        }
    }
}
