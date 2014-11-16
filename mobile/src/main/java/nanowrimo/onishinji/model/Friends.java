package nanowrimo.onishinji.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by guillaume on 16/11/14.
 */
public class Friends {

    ArrayList<Friend> list = new ArrayList<Friend>();

    public Friends(JSONObject response) {

        JSONArray items = null;
        try {
            items = response.getJSONArray("items");
            for (int i = 0; i < items.length(); i ++) {
                JSONObject o = items.getJSONObject(i);
                list.add(new Friend(o.getString("id"), o.getString("name")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Friend> getAll() {
        return list;
    }

    public Friend get(int i) {
        return list.get(i);
    }
}



