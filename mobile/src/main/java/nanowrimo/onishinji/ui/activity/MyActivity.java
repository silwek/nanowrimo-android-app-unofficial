package nanowrimo.onishinji.ui.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.crashlytics.android.Crashlytics;
import org.json.JSONObject;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.adapter.SectionsPagerAdapter;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.fragment.UserFragment;
import nanowrimo.onishinji.utils.StringUtils;


public class MyActivity extends FragmentActivity implements UserFragment.OnRemoveListener {

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
        Crashlytics.start(this);
        setContentView(R.layout.activity_my);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mDatabase = new Database(this);

        checkEmptyDatabase();


        mSectionsPagerAdapter = new SectionsPagerAdapter(mDatabase, this, getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        for (String user : mDatabase.getUsers()) {
            onAddTab(user, false);
        }
        HttpClient.getInstance().setContext(this);

        if (getIntent() != null) {
            Log.d("widget", "3) alors y a " + getIntent().getStringExtra("username"));

            int index = mDatabase.getUsers().indexOf(getIntent().getStringExtra("username"));
            Log.d("MyActivity", " found tab at " + index);
            if (index != -1) {
                mViewPager.setCurrentItem(index);
            }
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

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.default_username));
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.follow), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String value = input.getText().toString();

                final ProgressDialog progressDialog = ProgressDialog.show(MyActivity.this, "", getString(R.string.please_loading), true);
                progressDialog.show();

                // Test username
                RequestQueue queue = Volley.newRequestQueue(MyActivity.this);
                final String url = StringUtils.getUserUrl(value);
                JSONObject params = new JSONObject();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        User user = new User(response);

                        mDatabase.addUser(user.getId(), user.getName());
                        onAddTab(user.getId(), true);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        Toast alert = Toast.makeText(MyActivity.this, getString(R.string.name_invalid), Toast.LENGTH_SHORT);
                        alert.show();

                        if (!canCloseDialog) {
                            displayAddUserDialog(canCloseDialog);
                        }
                    }
                });
                queue.add(request);

            }
        });

        if (canCloseDialog) {
            alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
        //checkEmptyDatabase();
    }

    @Override
    public String getNiceTitle(String username) {
        return mDatabase.getNiceTitle(username);
    }


    public void onAddTab(String text, Boolean selectLastTab) {

        mSectionsPagerAdapter.setDatabase(mDatabase);
        mSectionsPagerAdapter.notifyDataSetChanged();

        // mViewPager.setAdapter(mSectionsPagerAdapter);

        if (selectLastTab) {
            mViewPager.setCurrentItem(mDatabase.getUsers().size());
        }
    }

    public void onRemoveTab(int position) {

        // Quick and dirty way
        finish();
        Intent i = new Intent(this, MyActivity.class);

        if (position - 1 >= 0 && position - 1 <= mDatabase.getUsers().size()) {
            i.putExtra("username", mDatabase.getUsers().get(position - 1));
        }

        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);

        //mSectionsPagerAdapter.setDatabase(mDatabase);
        //mSectionsPagerAdapter.notifyDataSetChanged();

        //mViewPager.removeAllViews();

        //mViewPager.setAdapter(mSectionsPagerAdapter);
        //mViewPager.setCurrentItem(position - 1);
    }

}
