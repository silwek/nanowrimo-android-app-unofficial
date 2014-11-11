package nanowrimo.onishinji.model;

import android.text.format.DateUtils;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by guillaume on 10/11/14.
 */
public class Historic {

    ArrayList<BarEntry> mVals = new ArrayList<BarEntry>();
    ArrayList<String> mDates = new ArrayList<String>();
    private ArrayList<Entry> mValsCumul = new ArrayList<Entry>();

    public Historic(JSONObject response) {

        try {
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM); // use MEDIUM or SHORT according to your needs

            JSONArray items = response.getJSONArray("items");

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();

            float previousVal = 0;
            int l = items.length();
            for (int i = 0; i < l; i++) {
                JSONObject item = items.getJSONObject(i);

                float val = Float.parseFloat(item.getString("wordcount"));

                float cumul = val + previousVal;

                Entry entry = new Entry(cumul, i);

                Date date = format.parse(item.getString("date"));

                String data = dateFormatter.format(date);
                // remove year
                String year = String.valueOf(c.get(Calendar.YEAR));
                data = data.replace(year, "").trim();

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("today", ""+(int) val);
                map.put("cumul", ""+(int)cumul);
                map.put("date", data);

                entry.setData(map);

                mValsCumul.add(entry);

                BarEntry barEntry = new BarEntry(val, i);
                barEntry.setData(map);
                mVals.add(barEntry);

                previousVal = cumul;

                mDates.add(item.getString("date"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<BarEntry> getValues() {
        return mVals;
    }
    public ArrayList<Entry> getValuesCumul() {
        return mValsCumul;
    }

    public ArrayList<String> getDates() {
        return mDates;
    }
}
