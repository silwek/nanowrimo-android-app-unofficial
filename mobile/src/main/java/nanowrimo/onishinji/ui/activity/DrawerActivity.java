package nanowrimo.onishinji.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.utils.StringUtils;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 29/03/2015.
 */
public abstract class DrawerActivity extends ActionBarActivity {

    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;

    protected Database mDatabase;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initDrawer();
    }

    protected void initDrawer(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ViewCompat.setElevation(toolbar, 3);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.syncState();

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        getSupportActionBar().setHomeButtonEnabled(true);

        ((TextView) mDrawerLayout.findViewById(R.id.drawer_user_name)).setText(WritingSessionHelper.getInstance().getUserName());
        ((TextView) mDrawerLayout.findViewById(R.id.drawer_session_name)).setText(WritingSessionHelper.getInstance().getSessionName());
        ((Button) mDrawerLayout.findViewById(R.id.bt_follow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAddUserDialog(true);
            }
        });
        ((Button) mDrawerLayout.findViewById(R.id.bt_playstore)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaystore();
            }
        });



        mDatabase = new Database(this);
    }

    public void displayAddUserDialog(final boolean canCloseDialog) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.dialog_add_user_title));

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.default_username));
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.follow), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String value = input.getText().toString().trim();

                final ProgressDialog progressDialog = ProgressDialog.show(DrawerActivity.this, "", getString(R.string.please_loading), true);
                progressDialog.show();

                // Test username
                RequestQueue queue = Volley.newRequestQueue(DrawerActivity.this);
                final String url = StringUtils.getUserUrl(value);
                JSONObject params = new JSONObject();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        User user = new User(response);

                        mDatabase.addUser(user.getId(), user.getName());
                        mDrawerLayout.closeDrawers();
                        onAddUser(user);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("error", error.toString());

                        progressDialog.dismiss();
                        Toast alert = Toast.makeText(DrawerActivity.this, getString(R.string.name_invalid), Toast.LENGTH_SHORT);
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

    protected abstract void onAddUser(User user);

    protected void showPlaystore(){
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
