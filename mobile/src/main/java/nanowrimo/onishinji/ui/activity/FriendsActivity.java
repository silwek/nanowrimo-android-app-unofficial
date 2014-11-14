package nanowrimo.onishinji.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.utils.StringUtils;

public class FriendsActivity  extends FragmentActivity {

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mUsername = getIntent().getStringExtra("username");
        }

        setContentView(R.layout.activity_friends);

        setTitle("friends of " + mUsername);
    }


    private void getRemoteData() {
        // Configure http request

        if (mUsername != null && !TextUtils.isEmpty(mUsername)) {
            final String url = StringUtils.getFriendUserUrl(mUsername);


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

    private void handleResponse(JSONObject response) {

        Log.d("friends", response.toString());
    }

}
