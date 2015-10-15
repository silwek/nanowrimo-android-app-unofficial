package nanowrimo.onishinji.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.ui.fragment.FavsFragment;
import nanowrimo.onishinji.ui.fragment.HotStuffFragment;
import nanowrimo.onishinji.ui.fragment.UserSummaryFragment;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 11/10/15.
 */
public class DashboardActivity extends ToolbarActivity {

    protected Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        setTitle(WritingSessionHelper.getInstance().getUserName());

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mDatabase = new Database(this);

        final String user = mDatabase.getUsers().get(0);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        HotStuffFragment hotStuffFragment = new HotStuffFragment();
        transaction.add(R.id.container_hotstuff, hotStuffFragment);

        FavsFragment favsFragment = new FavsFragment();
        transaction.add(R.id.container_favs, favsFragment);

        UserSummaryFragment userSummaryFragment = new UserSummaryFragment();
        userSummaryFragment.setId(user);
        userSummaryFragment.setUsername(mDatabase.getNiceTitle(user));
        transaction.add(R.id.container_user_summary, userSummaryFragment);

        transaction.commit();

        View v = findViewById(R.id.container);
        v.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_params:
                onActionParams();
                return true;
            case R.id.action_vote:
                onActionVote();
                return true;
            case R.id.action_about:
                onActionAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActionParams() {
        startActivity(new Intent(this, ParamsActivity.class));
    }

    protected void onActionVote() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    protected void onActionAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }
}
