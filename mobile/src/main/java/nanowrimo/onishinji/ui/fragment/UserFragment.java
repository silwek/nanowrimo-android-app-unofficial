package nanowrimo.onishinji.ui.fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.github.mikephil.charting.utils.LimitLine;
import com.github.mikephil.charting.utils.XLabels;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.Historic;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.activity.CompareActivity;
import nanowrimo.onishinji.ui.activity.FriendsActivity;
import nanowrimo.onishinji.ui.widget.MyBarMarkerView;
import nanowrimo.onishinji.ui.widget.MyMarkerView;
import nanowrimo.onishinji.ui.widget.WordCountProgress;
import nanowrimo.onishinji.utils.StringUtils;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserFragment extends Fragment implements PickerUserFragment.EditNameDialogListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView mTextViewUsername;
    private OnRemoveListener mOnRemoveListener;
    private TextView mTextViewGoal;
    private TextView mTextViewWordcount;
    private TextView mTextViewWordcountToday;
    private TextView mTextViewDailyTarget;
    private TextView mTextViewDailyTargetRemaining;
    private TextView mTextViewNbDayRemaining;
    private WordCountProgress mProgressDaily;
    private WordCountProgress mProgressGlobal;
    private LineChart mChart;
    private LineData mLineData;
    private BarChart mChartBar;
    private BarData mBarData;
    private Button mButtonBuddies;
    private ProgressBar mProgressBar;

    private int nbLoad = 2;
    private Button mButtonAction;
    private Database mDatabase;
    private int position;

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

        mIsSessionStarted = Calendar.getInstance().before(WritingSessionHelper.getInstance().getSessionStart());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void onWantRemoveUser() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(getString(R.string.dialog_remove_user_title));
        alert.setMessage(getString(R.string.dialog_remove_user_message, mId));

        alert.setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mOnRemoveListener != null) {
                    mOnRemoveListener.remove(mId);
                } else {
                    mDatabase.deleteUser(mId);
                }

                refreshActionButton();
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void onWantAddUser() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(getString(R.string.dialog_add_user_title));

        alert.setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.addUser(mId, mUsername);
                refreshActionButton();
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mButtonAction = (Button) getView().findViewById(R.id.button_action);
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        mTextViewUsername = (TextView) getView().findViewById(R.id.section_label);
        mTextViewGoal = (TextView) getView().findViewById(R.id.goal);
        mTextViewWordcount = (TextView) getView().findViewById(R.id.wordcount);
        mTextViewWordcountToday = (TextView) getView().findViewById(R.id.wordCountToday);
        mTextViewDailyTarget = (TextView) getView().findViewById(R.id.dailyTarget);
        mTextViewDailyTargetRemaining = (TextView) getView().findViewById(R.id.dailyTargetRemaining);
        mTextViewNbDayRemaining = (TextView) getView().findViewById(R.id.nbDayRemaining);
        mChart = (LineChart) getView().findViewById(R.id.chart);
        mChartBar = (BarChart) getView().findViewById(R.id.chart_bar);

        mButtonBuddies = (Button) getView().findViewById(R.id.show_friends);


        mChart.setDescription("");
        mChart.setDrawLegend(false);
        mChart.setDrawVerticalGrid(false);
        mChart.setDrawGridBackground(false);
        mChart.getYLabels().setFormatter(new LargeValueFormatter());
        mChart.setHighlightIndicatorEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDragEnabled(false);
        mChart.setDoubleTapToZoomEnabled(false);

        XLabels xl = mChart.getXLabels();
        xl.setCenterXLabelText(true);
        xl.setAvoidFirstLastClipping(false);
        xl.setAdjustXLabels(true);
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);

        mChart.setValueFormatter(new LargeValueFormatter());
        mChart.setDrawYValues(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout to use for it
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        // define an offset to change the original position of the marker (optional)
        mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());
        // set the marker to the chart
        mChart.setMarkerView(mv);


        mChartBar.setDescription("");
        mChartBar.setDrawLegend(false);
        mChartBar.setDrawVerticalGrid(false);
        mChartBar.setDrawGridBackground(false);
        mChartBar.getYLabels().setFormatter(new LargeValueFormatter());
        mChartBar.setHighlightIndicatorEnabled(false);
        mChartBar.setPinchZoom(false);
        mChartBar.setDragEnabled(false);
        mChartBar.setDoubleTapToZoomEnabled(false);

        xl = mChartBar.getXLabels();
        xl.setCenterXLabelText(true);
        xl.setAvoidFirstLastClipping(false);
        xl.setAdjustXLabels(true);
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);

        //mChartBar.setValueFormatter(new LargeValueFormatter());
        mChartBar.setDrawYValues(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout to use for it
        MyBarMarkerView mvb = new MyBarMarkerView(getActivity(), R.layout.custom_bar_marker_view);
        // define an offset to change the original position of the marker (optional)
        mvb.setOffsets(-mvb.getMeasuredWidth() / 2, -mvb.getMeasuredHeight());
        // set the marker to the chart
        mChartBar.setMarkerView(mvb);


        initializeGraphics();


        mProgressDaily = (WordCountProgress) getView().findViewById(R.id.daily);
        mProgressGlobal = (WordCountProgress) getView().findViewById(R.id.global);

        mButtonBuddies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                intent.putExtra("id", UserFragment.this.mId);
                intent.putExtra("username", UserFragment.this.mUsername);

                startActivity(intent);
            }
        });


        getView().findViewById(R.id.button_compare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getFragmentManager();

                ArrayList<String> choices = new ArrayList<String>();
                choices.add(0, "");
                for (String u : mDatabase.getUsers()) {
                    choices.add(u);
                }

                // Remove self
                choices.remove(mId);


                PickerUserFragment editNameDialog = PickerUserFragment.newInstance(getString(R.string.dialog_picker_title), choices);
                editNameDialog.setListener(UserFragment.this);
                editNameDialog.show(fm, "dz");
            }
        });

        updateUI();


        refreshActionButton();
    }

    private void refreshActionButton() {

        if(getActivity() != null) {
            if (canRemoveUser()) {
                mButtonAction.setText(getString(R.string.btn_action_remove));
                mButtonAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onWantRemoveUser();
                    }
                });
            } else {
                mButtonAction.setText(getString(R.string.btn_action_add));
                mButtonAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onWantAddUser();
                    }
                });
            }
        }
    }

    private boolean canRemoveUser() {
        return mDatabase.userIsMarkedAsFavorite(mId);
    }

    private void initializeGraphics() {
        if(mUser != null) {
            ArrayList<String> defaultLineValues = new ArrayList<String>();

            Calendar c = Calendar.getInstance();

            // start
            c.setTime(WritingSessionHelper.getInstance().getSessionStart());

            SimpleDateFormat f = new SimpleDateFormat("dd-MM");
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM); // use MEDIUM or SHORT according to your needs

            String year;
            String strDate;

            final int lastDay = WritingSessionHelper.getInstance().getSessionLastDay();
            for(int i = 1; i <= lastDay; i++) {
                c.set(Calendar.DAY_OF_MONTH, i);

                Date date = c.getTime();

                strDate = dateFormatter.format(date);
                // remove year
                year = String.valueOf(c.get(Calendar.YEAR));
                strDate = strDate.replace(year, "").trim();

                defaultLineValues.add(strDate);

            }


            ArrayList<Entry> defaultLineEntries = new ArrayList<Entry>();
            defaultLineEntries.add(new Entry(0, 0));
            defaultLineEntries.add(new Entry(mUser != null ? mUser.getGoal() : 50000, defaultLineValues.size() - 1));


            LineDataSet linearProgressionDataSet = new LineDataSet(defaultLineEntries, "naive linear progression");
            linearProgressionDataSet.enableDashedLine(10, 10, 0);
            linearProgressionDataSet.setCircleSize(0);

            linearProgressionDataSet.setColor(getResources().getColor(android.R.color.holo_orange_dark));
            linearProgressionDataSet.setCircleColor(getResources().getColor(android.R.color.holo_orange_dark));

            mLineData = new LineData(defaultLineValues, linearProgressionDataSet);
            mChart.setData(mLineData);


            mBarData = new BarData(defaultLineValues, new BarDataSet(new ArrayList<BarEntry>(), "default"));
            LimitLine ll = new LimitLine(mUser != null ? mUser.getDailyTarget() : 1667);
            ll.setLineColor(getResources().getColor(android.R.color.holo_orange_dark));
            ll.enableDashedLine(10, 10, 0);


            mBarData.addLimitLine(ll);
            mChartBar.setData(mBarData);
        }
    }

    private void updateUI() {
        Log.d("fragment", "will update UI with " + mId);

        if (mTextViewUsername != null && mOnRemoveListener != null && mId != null) {
            mTextViewUsername.setText(mOnRemoveListener.getNiceTitle(mId));
        }

        if (mUsername != null && !mUsername.isEmpty()) {
            mTextViewUsername.setText(mUsername);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getRemoteData();
    }

    private void getRemoteData() {
        // Configure http request

        if (mId != null && !TextUtils.isEmpty(mId)) {

            nbLoad = 2;
            final String url = StringUtils.getUserUrl(mId);

            JSONObject params = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    checkLoader();
                    handleResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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

                                getHistoricRemoteData(url + "/history");

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mProgressDaily.setText(getString(R.string.error_network_widget), getString(R.string.error_network_fragment_bottom));
                            mProgressDaily.getProgressPieView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

                            mProgressGlobal.setText(getString(R.string.error_network_widget), getString(R.string.error_network_fragment_bottom));
                            mProgressGlobal.getProgressPieView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

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


    protected void getHistoricRemoteData(final String url) {

        JSONObject params = new JSONObject();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                checkLoader();
                HandleHistoryResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                checkLoader();
                Cache c = HttpClient.getInstance().getQueue().getCache();
                Cache.Entry entry = c.get(url);

                Log.i("HISTORY", "1) get from cache " + url + " with " + entry);
                if (entry != null) {
                    // fetch the data from cache
                    try {
                        String data = new String(entry.data, "UTF-8");
                        HandleHistoryResponse(new JSONObject(data));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });

        // Search from cache first, make request in second
        Cache c = HttpClient.getInstance().getQueue().getCache();
        Cache.Entry entry = c.get(url);
        Log.i("HISTORY", "2) get from cache " + url + " with " + entry);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                HandleHistoryResponse(new JSONObject(data));

                c.invalidate(url, true);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        HttpClient.getInstance().add(request, true);
    }

    private void handleResponse(JSONObject response) {

        mUser = new User(response);

        if (getActivity() != null) {

            mTextViewGoal.setText(String.valueOf(mUser.getGoal()));
            mTextViewWordcount.setText(mUser.getWordcount() + "");
            mTextViewWordcountToday.setText(mUser.getWordCountToday() + "");
            mTextViewDailyTarget.setText(mIsSessionStarted ? mUser.getDailyTarget() + "" : "0");
            mTextViewDailyTargetRemaining.setText(mIsSessionStarted?mUser.getDailyTargetRemaining() + "" : "0");
            mTextViewNbDayRemaining.setText(mUser.getNbDayRemaining() + "");

            mProgressDaily.compute(mUser.getWordCountToday(), mIsSessionStarted?mUser.getDailyTarget():0, true);
            mProgressGlobal.compute(mUser.getWordcount(), mUser.getGoal(), true);

        }
    }

    private void HandleHistoryResponse(JSONObject response) {

        if (getActivity() != null) {

            mChart.clear();
            mChartBar.clear();

            initializeGraphics();

            Log.d("HISTORY", "HandleHistoryResponse called with " + response.toString());
            Historic user = new Historic(WritingSessionHelper.getInstance().getSessionStart(), response);

            //ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            LineDataSet historyDataSet = new LineDataSet(user.getValuesCumul(), "Your progression");

            historyDataSet.setColor(getResources().getColor(R.color.small_widget_progress_color));
            historyDataSet.setCircleColor(getResources().getColor(R.color.small_widget_progress_color));

            historyDataSet.setCircleSize(4);
            historyDataSet.setLineWidth(2);
            mLineData.addDataSet(historyDataSet);

            mChart.notifyDataSetChanged();
            mChart.invalidate();


            BarDataSet set1 = new BarDataSet(user.getValues(), "DataSet");
            set1.setColor(getResources().getColor(R.color.small_widget_progress_color));
            set1.setBarShadowColor(getResources().getColor(android.R.color.transparent));

            mBarData.addDataSet(set1);

            mChartBar.notifyDataSetChanged();
            mChartBar.invalidate();
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

    public void setOnRemoveListener(OnRemoveListener listener) {
        mOnRemoveListener = listener;
    }

    public void setUsername(CharSequence pageTitle) {
        mUsername = (String) pageTitle;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getUserId() {

        return this.mId;
    }

    @Override
    public void onFinishEditDialog(User user) {
        Log.d("user", "start compare with " + user.getId() + " " + user.getName());

       Intent i = new Intent(getActivity(), CompareActivity.class);
        i.putExtra("id_user_1", mId);
        i.putExtra("id_user_2", user.getId());
        i.putExtra("username_user_1", mUsername);
        i.putExtra("username_user_2", user.getName());

        startActivity(i);
    }

    public interface OnRemoveListener {
        void remove(String username);

        String getNiceTitle(String username);

    }

    private void checkLoader() {
        nbLoad--;

        if (nbLoad == 0) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }
}