package nanowrimo.onishinji.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.model.WritingSession;
import nanowrimo.onishinji.utils.URLUtils;
import nanowrimo.onishinji.utils.VolleyErrorHelper;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 29/03/2015.
 */
public class PrepareSessionFragment extends SlidingFragment {

    protected TextView mTvLoading;
    protected ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_prepare_session, container, false);

        mTvLoading = (TextView) v.findViewById(R.id.tv_loading);
        mTvLoading.setText(getString(R.string.welcome_prepare_session, WritingSessionHelper.getInstance().getUserName()));
        mImageView = (ImageView) v.findViewById(R.id.iv_session_logo);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        final int sessionType = WritingSessionHelper.getInstance().getSessionType();
        switch (sessionType) {
            case WritingSession.NANOWRIMO:
                mImageView.setImageResource(R.drawable.drawer_header_nanowrimo);
                break;
            case WritingSession.CAMP:
                mImageView.setImageResource(R.drawable.drawer_header_campnano);
                break;
        }


        // Test username
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = URLUtils.getUserUrl(WritingSessionHelper.getInstance().getSessionType(), WritingSessionHelper.getInstance().getUserName());
        JSONObject params = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (getActivity() != null) {
                    User user = new User(response);
                    WritingSessionHelper.getInstance().setUser(user);
                    WritingSessionHelper.getInstance().saveConfig(getActivity());
                    onWantNextSlide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (getActivity() != null) {
                    Log.e("error", error.toString());
                    String message = getString(R.string.name_invalid);
                    if (error instanceof TimeoutError) {
                        message = getString(R.string.generic_server_down);
                    } else if (VolleyErrorHelper.isServerProblem(error)) {
                        message = VolleyErrorHelper.handleServerError(error, getActivity());
                    } else if (VolleyErrorHelper.isNetworkProblem(error)) {
                        message = getString(R.string.no_internet);
                    }
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    onWantPreviousSlide();
                }
            }
        });
        queue.add(request);
    }
}

