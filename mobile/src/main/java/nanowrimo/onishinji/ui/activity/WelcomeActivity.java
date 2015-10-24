package nanowrimo.onishinji.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.ui.fragment.PrepareSessionFragment;
import nanowrimo.onishinji.ui.fragment.SlidingFragment;
import nanowrimo.onishinji.ui.fragment.UserFormFragment;
import nanowrimo.onishinji.ui.fragment.WelcomeFragment;
import nanowrimo.onishinji.utils.PreferencesHelper;

/**
 * Created by Silwek on 29/03/2015.
 */
public class WelcomeActivity extends AppCompatActivity implements SlidingFragment.SlidingListener {

    ViewGroup mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mFragmentContainer = (ViewGroup) findViewById(R.id.content);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SlidingFragment frag = new WelcomeFragment();
        fragmentTransaction.add(R.id.content, frag);
        fragmentTransaction.commit();
    }

    @Override
    public void onNextSlide(SlidingFragment fragment) {
        if (fragment instanceof WelcomeFragment) {
            SlidingFragment frag = new UserFormFragment();
            showNewFragment(frag);
        } else if (fragment instanceof UserFormFragment) {
            SlidingFragment frag = new PrepareSessionFragment();
            showNewFragment(frag);
        } else if (fragment instanceof PrepareSessionFragment) {
            PreferencesHelper.setFirstLaunch(this, false);
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPreviousSlide(SlidingFragment fragment) {
        if (fragment instanceof PrepareSessionFragment) {
            onBackPressed();
        }
    }

    protected void showNewFragment(SlidingFragment newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        ft.replace(R.id.content, newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
