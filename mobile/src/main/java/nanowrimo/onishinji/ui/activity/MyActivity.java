package nanowrimo.onishinji.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONObject;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.adapter.SectionsPagerAdapter;
import nanowrimo.onishinji.adapter.SectionsPagerAdapter;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.fragment.PlaceholderFragment;


public class MyActivity extends FragmentActivity implements PlaceholderFragment.OnRemoveListener {

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private Database mDatabase;
    private ActionBar.Tab mLastTab;
    private boolean mTurnOff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // Set up the action bar.
        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
      //  bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mDatabase = new Database(this);

        checkEmptyDatabase();


        mSectionsPagerAdapter = new SectionsPagerAdapter(mDatabase, this, getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        for (String user : mDatabase.getUsers()) {
            onAddTab(user, false);
        }

    }

    private void checkEmptyDatabase() {
        ArrayList<String> users = mDatabase.getUsers();
        if (users.size() == 0) {
            displayAddUserDialog(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.add_user) {
            displayAddUserDialog(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayAddUserDialog(final boolean canCloseDialog) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.dialog_add_user_title));
        alert.setMessage(getString(R.string.dialog_add_user_message));

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String value = input.getText().toString();

                final ProgressDialog progressDialog = ProgressDialog.show(MyActivity.this, "", getString(R.string.please_loading), true);
                progressDialog.show();

                // Test username
                RequestQueue queue = Volley.newRequestQueue(MyActivity.this);
                final String url = getString(R.string.base_url) + value;
                JSONObject params = new JSONObject();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        User user = new User(response);

                        mDatabase.addUser(user.getName());
                        onAddTab(user.getName(), true);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (true) {
                            mDatabase.addUser(value);
                            onAddTab(value, true);
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            Toast alert = Toast.makeText(MyActivity.this, getString(R.string.name_invalid), Toast.LENGTH_SHORT);
                            alert.show();

                            if (!canCloseDialog) {
                                displayAddUserDialog(canCloseDialog);
                            }
                        }

                    }
                });
                queue.add(request);

            }
        });

        if (canCloseDialog) {
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });
        }

        alert.setCancelable(canCloseDialog);

        alert.show();
    }

    @Override
    public void remove(String username) {

        int oldPos = mDatabase.getUsers().indexOf(username);
        mDatabase.deleteUser(username);
        onRemoveTab(oldPos);
        checkEmptyDatabase();
    }


    public void onAddTab(String text, Boolean selectLastTab) {
        final ActionBar bar = getActionBar();
        final int tabCount = bar.getTabCount();

        mSectionsPagerAdapter.notifyDataSetChanged();

        ActionBar.Tab newTab = bar.newTab()
                .setText(text)
                .setTabListener(new TabListener());
        bar.addTab(newTab);

        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (selectLastTab) {
            bar.selectTab(newTab);
            mViewPager.setCurrentItem(bar.getTabCount());
        }

    }

    public void onRemoveTab(int position) {
        final ActionBar bar = getActionBar();
        if (bar.getTabCount() > 0) {

            mSectionsPagerAdapter.notifyDataSetChanged();
            bar.removeTabAt(position);

            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(position - 1);
        }

    }

    /**
     * A TabListener receives event callbacks from the action bar as tabs
     * are deselected, selected, and reselected.
     */
    private class TabListener implements ActionBar.TabListener {
        private PlaceholderFragment mFragment;

        public TabListener() {
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if(tab.getPosition() >= 0) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
           // mViewPager.set
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            Toast.makeText(MyActivity.this, "Reselected!", Toast.LENGTH_SHORT).show();
        }
    }
}
