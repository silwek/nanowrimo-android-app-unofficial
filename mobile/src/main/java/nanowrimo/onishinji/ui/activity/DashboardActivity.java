package nanowrimo.onishinji.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.event.UserEvent;
import nanowrimo.onishinji.event.WordcountUpdateEvent;
import nanowrimo.onishinji.model.BusManager;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.fragment.FavsFragment;
import nanowrimo.onishinji.ui.fragment.HotStuffFragment;
import nanowrimo.onishinji.ui.fragment.UserSummaryFragment;
import nanowrimo.onishinji.utils.URLUtils;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 11/10/15.
 */
public class DashboardActivity extends ToolbarActivity {

    public static final String EXTRA_SHOW_USER_ID = "nanowrimo.onishinji.ui.activity.DashboardActivity.EXTRA_SHOW_USER_ID";

    private String mId;
    protected User mUser;
    protected boolean mIsUserLoading = false;


    protected String mShowUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        setTitle(WritingSessionHelper.getInstance().getUserName());

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        HotStuffFragment hotStuffFragment = new HotStuffFragment();
        transaction.add(R.id.container_hotstuff, hotStuffFragment);

        FavsFragment favsFragment = new FavsFragment();
        transaction.add(R.id.container_favs, favsFragment);

        UserSummaryFragment userSummaryFragment = new UserSummaryFragment();
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

        mId = WritingSessionHelper.getInstance().getUserName();

        if (getIntent().getExtras() != null) {
            mShowUserId = getIntent().getExtras().getString(EXTRA_SHOW_USER_ID);
        }
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
        startActivity(new Intent(this, SettingsActivity.class));
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

    @Override
    protected void onStart() {
        super.onStart();

        if (!TextUtils.isEmpty(mShowUserId)) {
            Intent intent = new Intent(this, FriendActivity.class);
            intent.putExtra(FriendActivity.EXTRA_ID, mShowUserId);
            intent.putExtra(FriendActivity.EXTRA_USERNAME, mShowUserId);
            startActivity(intent);
            mShowUserId = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusManager.getInstance().getBus().register(this);
        getRemoteData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusManager.getInstance().getBus().unregister(this);
    }

    private void getRemoteData() {
        // Configure http request

        if (mId != null && !TextUtils.isEmpty(mId)) {

            mIsUserLoading = true;
            checkLoader();
            final String url = URLUtils.getUserUrl(WritingSessionHelper.getInstance().getSessionType(), mId);

            JSONObject params = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mIsUserLoading = false;
                    checkLoader();
                    handleResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    mIsUserLoading = false;
                    checkLoader();

                    Log.e("error", error.toString());

                    Cache c = HttpClient.getInstance().getQueue().getCache();
                    Cache.Entry entry = c.get(url);
                    if (entry != null) {
                        // fetch the data from cache
                        try {
                            String data = new String(entry.data, "UTF-8");
                            handleResponse(new JSONObject(data));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


            // Search from cache first, make request in second
            Cache c = HttpClient.getInstance().getQueue().getCache();
            Cache.Entry entry = c.get(url);
            if (entry != null) {
                // fetch the data from cache
                try {
                    String data = new String(entry.data, "UTF-8");
                    handleResponse(new JSONObject(data));

                    c.invalidate(url, true);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            HttpClient.getInstance().add(request, true);
        }
    }

    protected void checkLoader() {
    }

    private void handleResponse(JSONObject response) {
        mUser = new User(response);
        BusManager.getInstance().getBus().post(new UserEvent(mUser));
    }

    @SuppressWarnings("unused")
    public void onEvent(WordcountUpdateEvent event) {
        getRemoteData();
    }
}
