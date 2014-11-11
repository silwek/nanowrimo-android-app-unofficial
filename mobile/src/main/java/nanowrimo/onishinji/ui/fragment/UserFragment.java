package nanowrimo.onishinji.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.github.mikephil.charting.utils.LimitLine;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.XLabels;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.Historic;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.widget.MyBarMarkerView;
import nanowrimo.onishinji.ui.widget.MyMarkerView;
import nanowrimo.onishinji.ui.widget.WordCountProgress;
import nanowrimo.onishinji.utils.StringUtils;

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
    private WordCountProgress mProgressDaily;
    private WordCountProgress mProgressGlobal;
    private LineChart mChart;
    private LineData mLineData;
    private BarChart mChartBar;
    private BarData mBarData;
    private ArrayList<String> mDefaultLineValues;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.username = savedInstanceState.getString("username");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);
        setHasOptionsMenu(true);
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
                if (mOnRemoveListener != null) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                    alert.setTitle(getString(R.string.dialog_remove_user_title));
                    alert.setMessage(getString(R.string.dialog_remove_user_message, username));

                    alert.setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mOnRemoveListener.remove(username);
                        }
                    });

                    alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show();

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
        mChart = (LineChart) getView().findViewById(R.id.chart);
        mChartBar = (BarChart) getView().findViewById(R.id.chart_bar);


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


         mDefaultLineValues = new ArrayList<String>();


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

        mDefaultLineValues.add(data);

        for (int i = 2; i <= 30; i++) {
            c.set(Calendar.DAY_OF_MONTH, i);

            Date date = c.getTime();

            data = dateFormatter.format(date);
            // remove year
            year = String.valueOf(c.get(Calendar.YEAR));
            data = data.replace(year, "").trim();

            mDefaultLineValues.add(data);

        }


        ArrayList<Entry> defaultLineEntries = new ArrayList<Entry>();
        defaultLineEntries.add(new Entry(0, 0));
        defaultLineEntries.add(new Entry(50000, mDefaultLineValues.size() - 1));


        LineDataSet linearProgressionDataSet = new LineDataSet(defaultLineEntries, "naive linear progression");
        linearProgressionDataSet.enableDashedLine(10, 10, 0);
        linearProgressionDataSet.setCircleSize(0);

        linearProgressionDataSet.setColor(getResources().getColor(android.R.color.holo_orange_dark));
        linearProgressionDataSet.setCircleColor(getResources().getColor(android.R.color.holo_orange_dark));

        mLineData = new LineData(mDefaultLineValues, linearProgressionDataSet);
        mChart.setData(mLineData);


        mBarData= new BarData(mDefaultLineValues, new BarDataSet(new ArrayList<BarEntry>(), "default"));
        LimitLine ll = new LimitLine(1667);
        ll.setLineColor(getResources().getColor(android.R.color.holo_orange_dark));
        ll.enableDashedLine(10, 10, 0);


        mBarData.addLimitLine(ll);
        mChartBar.setData(mBarData);


        mProgressDaily = (WordCountProgress) getView().findViewById(R.id.daily);
        mProgressGlobal = (WordCountProgress) getView().findViewById(R.id.global);

        updateUI();
    }

    private void updateUI() {
        Log.d("fragment", "will update UI with " + username);

        if (mTextViewUsername != null)
            mTextViewUsername.setText(mOnRemoveListener.getNiceTitle(username));
    }

    @Override
    public void onStart() {
        super.onStart();

        getRemoteData();
    }

    private void getRemoteData() {
        // Configure http request

        if (username != null && !TextUtils.isEmpty(username)) {
            final String url = StringUtils.getUserUrl(username);

            getHistoricRemoteData(url + "/history");

            JSONObject params = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    User user = new User(response);

                    if (getActivity() != null) {

                        mTextViewWordcount.setText(user.getWordcount() + "");
                        mTextViewWordcountToday.setText(user.getWordCountToday() + "");
                        mTextViewDailyTarget.setText(user.getDailyTarget() + "");
                        mTextViewDailyTargetRemaining.setText(user.getDailyTargetRemaining() + "");
                        mTextViewNbDayRemaining.setText(user.getNbDayRemaining() + "");

                        mProgressDaily.compute(user.getWordCountToday(), user.getDailyTarget(), true);
                        mProgressGlobal.compute(user.getWordcount(), 50000.0f, true);

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (getActivity() != null) {
                        mProgressDaily.setText(getString(R.string.error_network_widget), getString(R.string.error_network_fragment_bottom));
                        mProgressDaily.getProgressPieView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

                        mProgressGlobal.setText(getString(R.string.error_network_widget), getString(R.string.error_network_fragment_bottom));
                        mProgressGlobal.getProgressPieView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                        mChart.clear();
                    }
                }
            });

            HttpClient.getInstance().add(request);
        }
    }

    protected void getHistoricRemoteData(String url) {

        JSONObject params = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (getActivity() != null) {
                    Historic user = new Historic(response);

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

        String getNiceTitle(String username);
    }
}