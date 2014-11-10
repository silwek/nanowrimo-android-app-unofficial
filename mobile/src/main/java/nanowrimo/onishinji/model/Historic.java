package nanowrimo.onishinji.model;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by guillaume on 10/11/14.
 */
public class Historic {

    ArrayList<Entry> mVals = new ArrayList<Entry>();
    ArrayList<String> mDates = new ArrayList<String>();

    public Historic(JSONObject response) {

        try {

            JSONArray items = response.getJSONArray("items");

            float previousVal = 0;
            int l = items.length();
            for(int i = 0; i < l; i++) {
                JSONObject item = items.getJSONObject(i);

                float val = Float.parseFloat(item.getString("wordcount"));

                val = val + previousVal;

                Entry entry = new Entry(val, i);
                mVals.add(entry);

                previousVal = val;

                mDates.add(item.getString("date"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Entry> getValues() {
        return mVals;
    }

    public ArrayList<String> getDates() {
        return mDates;
    }
}
