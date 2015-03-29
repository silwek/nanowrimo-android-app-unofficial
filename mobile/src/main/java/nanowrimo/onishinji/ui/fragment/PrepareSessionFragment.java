package nanowrimo.onishinji.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class PrepareSessionFragment  extends SlidingFragment {

    protected TextView mTvLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_prepare_session, container, false);

        mTvLoading = (TextView) v.findViewById(R.id.tv_loading);
        mTvLoading.setText(String.format("Préparation de la session d'écriture pour %s...", WritingSessionHelper.getInstance().getUserName()));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Test username
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = StringUtils.getUserUrl(WritingSessionHelper.getInstance().getUserName());
        JSONObject params = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                User user = new User(response);
                WritingSessionHelper.getInstance().setUser(user);
                WritingSessionHelper.getInstance().saveConfig(getActivity());
                onWantNextSlide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
                onWantPreviousSlide();
            }
        });
        queue.add(request);
    }
}

