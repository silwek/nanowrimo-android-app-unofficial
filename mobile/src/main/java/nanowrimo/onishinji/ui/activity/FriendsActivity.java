package nanowrimo.onishinji.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.BusManager;
import nanowrimo.onishinji.model.Friends;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.utils.URLUtils;
import nanowrimo.onishinji.utils.WritingSessionHelper;

public class FriendsActivity extends ToolbarActivity {

    private String mUsername;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mUsername = getIntent().getStringExtra("username");
            mId = getIntent().getStringExtra("id");
            setTitle(getString(R.string.title_friends_activity, mUsername));
            getRemoteData();
        }

        setContentView(R.layout.activity_friends);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getRemoteData() {
        // Configure http request

        if (mUsername != null && !TextUtils.isEmpty(mId)) {
            final String url = URLUtils.getFriendUserUrl(WritingSessionHelper.getInstance().getSessionType(), mId);


            JSONObject params = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    handleResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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
                    } else {
                        // TODO error
                    }
                }
            });

            HttpClient.getInstance().add(request, true);

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

        }
    }

    private void handleResponse(JSONObject response) {
        Friends friends = new Friends(response);
        BusManager.getInstance().getBus().post(friends);
    }

}
