package nanowrimo.onishinji.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.utils.URLUtils;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 11/10/15.
 */
public class UserSummaryFragment extends Fragment {

    private TextView mTextViewGoal;
    private TextView mTextViewWordcount;
    private TextView mTextViewWordcountToday;
    private TextView mTextViewDailyTarget;
    private TextView mTextViewDailyTargetRemaining;
    private TextView mTextViewNbDayRemaining;
    private ProgressBar mProgressBar;

    protected boolean mIsUserLoading = false;

    private Database mDatabase;

    protected boolean mIsSessionStarted = true;

    private String mId;
    private String mUsername;
    protected User mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.mId = savedInstanceState.getString("id");
            this.mUsername = savedInstanceState.getString("username");
        } else {
            String usernameByIntent = getActivity().getIntent().getStringExtra("id");
            if (usernameByIntent != null && !usernameByIntent.isEmpty()) {
                this.mId = usernameByIntent;
                this.mUsername = getActivity().getIntent().getStringExtra("username");
            }
        }

        mDatabase = new Database(getActivity());

        mIsSessionStarted = WritingSessionHelper.getInstance().isSessionStarted();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_dashboard, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        mTextViewGoal = (TextView) getView().findViewById(R.id.goal);
        mTextViewWordcount = (TextView) getView().findViewById(R.id.wordcount);
        mTextViewWordcountToday = (TextView) getView().findViewById(R.id.wordCountToday);
        mTextViewDailyTarget = (TextView) getView().findViewById(R.id.dailyTarget);
        mTextViewDailyTargetRemaining = (TextView) getView().findViewById(R.id.dailyTargetRemaining);
        mTextViewNbDayRemaining = (TextView) getView().findViewById(R.id.nbDayRemaining);

    }

    @Override
    public void onStart() {
        super.onStart();

        getRemoteData();
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

                    if (getActivity() != null) {

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

        mUser = new User(response);

        if (getActivity() != null) {

            mTextViewGoal.setText(String.valueOf(mUser.getGoal()));
            mTextViewWordcount.setText(mUser.getWordcount() + "");
            mTextViewWordcountToday.setText(mUser.getWordCountToday() + "");
            mTextViewDailyTarget.setText(mIsSessionStarted ? mUser.getDailyTarget() + "" : "0");
            mTextViewDailyTargetRemaining.setText(mIsSessionStarted ? mUser.getDailyTargetRemaining() + "" : "0");
            mTextViewNbDayRemaining.setText(mUser.getNbDayRemaining() + "");

        }
    }

    public void setId(String s) {
        this.mId = s;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("id", mId);
        outState.putString("username", mUsername);
    }

    public void setUsername(CharSequence pageTitle) {
        mUsername = (String) pageTitle;
    }

    protected boolean isLoading() {
        return mIsUserLoading;
    }

    private void checkLoader() {
        if (isLoading()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}