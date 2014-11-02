package nanowrimo.onishinji.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView mTextViewUsername;
    private String username;
    private OnRemoveListener mOnRemoveListener;
    private TextView mTextViewWordcount;
    private TextView mTextViewWordcountToday;
    private TextView mTextViewDailyTarget;
    private TextView mTextViewDailyTargetRemaining;
    private TextView mTextViewNbDayRemaining;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            this.username = savedInstanceState.getString("username");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);
//        this.username = savedInstanceState.getString("name");
        setHasOptionsMenu(true);

//        this.username = getArguments().getString("name");

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.placeholderfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_user:
                if(mOnRemoveListener != null) {
                    mOnRemoveListener.remove(username);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextViewUsername = (TextView) getView().findViewById(R.id.section_label);
        mTextViewWordcount = (TextView) getView().findViewById(R.id.wordcount);
        mTextViewWordcountToday = (TextView) getView().findViewById(R.id.wordCountToday);
        mTextViewDailyTarget = (TextView) getView().findViewById(R.id.dailyTarget);
        mTextViewDailyTargetRemaining = (TextView) getView().findViewById(R.id.dailyTargetRemaining);
        mTextViewNbDayRemaining = (TextView) getView().findViewById(R.id.nbDayRemaining);
       updateUI();
    }

    private void updateUI() {
        Log.d("fragment", "will update UI with " + username);
        mTextViewUsername.setText(username);


    }

    @Override
    public void onStart() {
        super.onStart();

        getRemoteData();
    }

    private void getRemoteData() {
        // Configure http request

        final String url = getActivity().getString(R.string.base_url) + username;
        JSONObject params = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("user", response.toString());

                User user = new User(response);

                mTextViewWordcount.setText(user.getWordcount()+"");
                mTextViewWordcountToday.setText(user.getWordCountToday()+"");
                mTextViewDailyTarget.setText(user.getDailyTarget()+"");
                mTextViewDailyTargetRemaining.setText(user.getDailyTargetRemaining()+"");
                mTextViewNbDayRemaining.setText(user.getNbDayRemaining()+"");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        HttpClient.getInstance().add(request);
    }

    public void setUsername(String s) {
        this.username = s;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username", username);
    }

    public void setOnRemoveListener(OnRemoveListener listener) {
        mOnRemoveListener = listener;
    }

    public interface OnRemoveListener {
        void remove(String username);
    }
}