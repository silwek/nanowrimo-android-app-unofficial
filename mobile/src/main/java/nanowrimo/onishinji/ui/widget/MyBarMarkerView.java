package nanowrimo.onishinji.ui.widget;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.MarkerView;
import com.github.mikephil.charting.utils.Utils;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import nanowrimo.onishinji.R;

public class MyBarMarkerView extends MarkerView {
    private TextView tvContent;

    public MyBarMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.label);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
// content
    @Override
    public void refreshContent(Entry e, int dataSetIndex) {

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            HashMap<String, String> map = (HashMap<String, String>) e.getData();
            String date = map.get("date");
            String nbWorldToday = map.get("today");
            String text = getContext().getResources().getString(R.string.stats_bar_marker_view, numberFormat.format(e.getVal()), date);
            tvContent.setText(text);
        }
    }
}