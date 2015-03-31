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
import android.widget.EditText;
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
import java.util.Iterator;

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
public class CompareFragment extends Fragment {

    private WordCountProgress mProgressDaily1;
    private WordCountProgress mProgressGlobal1;

    private WordCountProgress mProgressDaily2;
    private WordCountProgress mProgressGlobal2;
    private LineChart mChart;
    private LineData mLineData;
    private BarChart mChartBar;
    private BarData mBarData;
    private ProgressBar mProgressBar;

    private int nbLoad = 4;
    private Database mDatabase;
    private String mIdUser1;
    private String mUsernameUser1;
    private String mIdUser2;
    private String mUsernameUser2;
    private TextView mTextViewUser1;
    private TextView mTextViewUser2;
    private LineDataSet mHistoryDataSet1;
    private LineDataSet mHistoryDataSet2;
    private BarDataSet mBarHistoryData1;
    private BarDataSet mBarHistoryData2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.mIdUser1 = savedInstanceState.getString("id_user_1");
            this.mUsernameUser1 = savedInstanceState.getString("username_user_1");

            this.mIdUser2 = savedInstanceState.getString("id_user_2");
            this.mUsernameUser2 = savedInstanceState.getString("username_user_2");
        } else {
            String usernameByIntent = getActivity().getIntent().getStringExtra("id_user_1");
            if (usernameByIntent != null && !usernameByIntent.isEmpty()) {
                this.mIdUser1 = usernameByIntent;
                this.mUsernameUser1 = getActivity().getIntent().getStringExtra("username_user_1");

                this.mIdUser2 = getActivity().getIntent().getStringExtra("id_user_2");
                this.mUsernameUser2 = getActivity().getIntent().getStringExtra("username_user_2");
            }
        }

        mDatabase = new Database(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compare, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);

        mTextViewUser1 = (TextView) getView().findViewById(R.id.mTextViewUser1);
        mTextViewUser2 = (TextView) getView().findViewById(R.id.mTextViewUser2);

        mChart = (LineChart) getView().findViewById(R.id.chart);
        mChartBar = (BarChart) getView().findViewById(R.id.chart_bar);

        mChart.setDescription("");
        mChart.setDrawLegend(true);
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
        mChartBar.setDrawLegend(true);
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

        mProgressDaily1 = (WordCountProgress) getView().findViewById(R.id.daily_user_1);
        mProgressDaily2 = (WordCountProgress) getView().findViewById(R.id.daily_user_2);
        mProgressGlobal1 = (WordCountProgress) getView().findViewById(R.id.global_user_1);
        mProgressGlobal2 = (WordCountProgress) getView().findViewById(R.id.global_user_2);


    }

    private void initializeGraphics() {
        ArrayList<String> defaultLineValues = new ArrayList<String>();

        Calendar c = Calendar.getInstance();

        // start november
        c.set(Calendar.MONTH, 10);
        c.set(Calendar.DAY_OF_MONTH, 1);

        SimpleDateFormat f = new SimpleDateFormat("dd-MM");
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM); // use MEDIUM or SHORT according to your needs

        String data = dateFormatter.format(c.getTime());
        // remove year
        String year = String.valueOf(c.get(Calendar.YEAR));
        data = data.replace(year, "").trim();

        defaultLineValues.add(data);

        for (int i = 2; i <= 30; i++) {
            c.set(Calendar.DAY_OF_MONTH, i);

            Date date = c.getTime();

            data = dateFormatter.format(date);
            // remove year
            year = String.valueOf(c.get(Calendar.YEAR));
            data = data.replace(year, "").trim();

            defaultLineValues.add(data);

        }


        ArrayList<Entry> defaultLineEntries = new ArrayList<Entry>();
        defaultLineEntries.add(new Entry(0, 0));
        defaultLineEntries.add(new Entry(50000, defaultLineValues.size() - 1));


        LineDataSet linearProgressionDataSet = new LineDataSet(defaultLineEntries, getString(R.string.linear_progress_label));
        linearProgressionDataSet.enableDashedLine(10, 10, 0);
        linearProgressionDataSet.setCircleSize(0);

        linearProgressionDataSet.setColor(getResources().getColor(android.R.color.holo_orange_dark));
        linearProgressionDataSet.setCircleColor(getResources().getColor(android.R.color.holo_orange_dark));

        mLineData = new LineData(defaultLineValues, linearProgressionDataSet);
        mChart.setData(mLineData);


        mBarData = new BarData(defaultLineValues, new BarDataSet(new ArrayList<BarEntry>(), "default"));
        LimitLine ll = new LimitLine(1667);
        ll.setLineColor(getResources().getColor(android.R.color.holo_orange_dark));
        ll.enableDashedLine(10, 10, 0);


        mBarData.addLimitLine(ll);
        mChartBar.setData(mBarData);

        mTextViewUser1.setText(mUsernameUser1);
        mTextViewUser2.setText(mUsernameUser2);
    }


    @Override
    public void onStart() {
        super.onStart();

        getRemoteData(1, mIdUser1);
        getRemoteData(2, mIdUser2);
    }

    private void getRemoteData(final int user, String userId) {
        // Configure http request

        if (userId != null && !TextUtils.isEmpty(userId)) {

            nbLoad = 4;
            final String url = StringUtils.getUserUrl(userId);

            getHistoricRemoteData(user, url + "/history");

            JSONObject params = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    checkLoader();
                    handleResponse(user, response);
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
                                handleResponse(user, new JSONObject(data));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {


                            WordCountProgress progressDaily = user == 1 ? mProgressDaily1 : mProgressDaily2;
                            WordCountProgress progressGlobal = user == 1 ? mProgressGlobal1 : mProgressGlobal2;

                            progressDaily.setText(getString(R.string.error_network_widget), getString(R.string.error_network_fragment_bottom));
                            progressDaily.getProgressPieView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

                            progressGlobal.setText(getString(R.string.error_network_widget), getString(R.string.error_network_fragment_bottom));
                            progressGlobal.getProgressPieView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

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
                    handleResponse(user, new JSONObject(data));

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

    protected void getHistoricRemoteData(final int user, final String url) {

        JSONObject params = new JSONObject();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                checkLoader();
                HandleHistoryResponse(user, response);
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
                        HandleHistoryResponse(user, new JSONObject(data));
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
                HandleHistoryResponse(user, new JSONObject(data));

                c.invalidate(url, true);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        HttpClient.getInstance().add(request, true);
    }

    private void handleResponse(int u, JSONObject response) {

        User user = new User(response);

        if (getActivity() != null) {

            WordCountProgress progressDaily = u == 1 ? mProgressDaily1 : mProgressDaily2;
            WordCountProgress progressGlobal = u == 1 ? mProgressGlobal1 : mProgressGlobal2;

            progressDaily.compute(user.getWordCountToday(), user.getDailyTarget(), true);
            progressGlobal.compute(user.getWordcount(), user.getGoal(), true);

        }
    }

    private void HandleHistoryResponse(int u, JSONObject response) {

        if (getActivity() != null) {

            Log.d("HISTORY", "HandleHistoryResponse called with " + response.toString());
            Historic user = new Historic(WritingSessionHelper.getInstance().getSessionStart(),response);

            LineDataSet historyDataSet = null;
            BarDataSet set1 = null;

            boolean wasHistoryData1Null = false;
            boolean wasHistoryData2Null = false;

            boolean wasBarHistoryData1Null = false;
            boolean wasBarHistoryData2Null = false;

            if (u == 1) {
                if (mHistoryDataSet1 == null) {
                    wasHistoryData1Null = true;
                    mHistoryDataSet1 = new LineDataSet(user.getValuesCumul(), mUsernameUser1);
                }

                if (mBarHistoryData1 == null) {
                    wasBarHistoryData1Null = true;
                    mBarHistoryData1 = new BarDataSet(user.getValues(), mUsernameUser1);
                }

                historyDataSet = mHistoryDataSet1;
                set1 = mBarHistoryData1;
            }

            if (u == 2) {
                if (mHistoryDataSet2 == null) {
                    wasHistoryData2Null = true;
                    mHistoryDataSet2 = new LineDataSet(user.getValuesCumul(), mUsernameUser2);
                }

                if (mBarHistoryData2 == null) {
                    wasBarHistoryData2Null = true;
                    mBarHistoryData2 = new BarDataSet(user.getValues(), mUsernameUser2);
                }

                historyDataSet = mHistoryDataSet2;
                set1 = mBarHistoryData2;
            }

            // Remove existant
            ArrayList<Entry> entries = historyDataSet.getYVals();
            for (Iterator<Entry> iterator = entries.iterator(); iterator.hasNext(); ) {
                Entry px = iterator.next();
                iterator.remove();
            }


            ArrayList<BarEntry> entriesBar = set1.getYVals();
            for (Iterator<BarEntry> iterator = entriesBar.iterator(); iterator.hasNext(); ) {
                BarEntry px = iterator.next();
                iterator.remove();
            }


            // Add new Value
            for (Entry e : user.getValuesCumul()) {
                historyDataSet.addEntry(e);
            }

            for (BarEntry e : user.getValues()) {
                set1.addEntry(e);
            }

            historyDataSet.setColor(getResources().getColor(u == 1 ? R.color.small_widget_progress_color : android.R.color.holo_orange_dark));
            historyDataSet.setCircleColor(getResources().getColor(u == 1 ? R.color.small_widget_progress_color : android.R.color.holo_orange_dark));

            historyDataSet.setCircleSize(4);
            historyDataSet.setLineWidth(2);

            if (wasHistoryData1Null || wasHistoryData2Null) {
                mLineData.addDataSet(historyDataSet);
            }

            mChart.notifyDataSetChanged();
            mChart.invalidate();


            set1.setColor(getResources().getColor(u == 1 ? R.color.small_widget_progress_color : android.R.color.holo_orange_dark));
            set1.setBarShadowColor(getResources().getColor(android.R.color.transparent));

            if (wasBarHistoryData1Null || wasBarHistoryData2Null) {
                mBarData.addDataSet(set1);
            }

            mChartBar.notifyDataSetChanged();
            mChartBar.invalidate();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("id_user_1", mIdUser1);
        outState.putString("username_user_1", mUsernameUser1);

        outState.putString("id_user_2", mIdUser2);
        outState.putString("username_user_2", mUsernameUser2);
    }

    private void checkLoader() {
        nbLoad--;

        Log.d("compare", "nb request restante " + nbLoad);

        if (nbLoad == 0) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }
}