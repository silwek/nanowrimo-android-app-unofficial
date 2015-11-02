package nanowrimo.onishinji.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by guillaume on 31/10/14.
 */
public class User {

    public static final int NO_DATA_WORDCOUNT = -1;

    private String mId = "";
    private String mName = "";
    private int mGoal = 50000;
    private int mWordcount = 0;
    private int mWordCountToday = 0;
    private int mDailyTarget = 0;
    private int mDailyTargetRemaining = 0;
    private int mNbDayRemaining = 0;

    private HashMap<String, String> links = new HashMap<String, String>();

    public User() {

    }

    public User(JSONObject response) {

        try {
            mId = response.getString("id");
            mName = response.getString("name");
            mGoal = response.getInt("userGoal");
            mWordcount = response.getInt("wordcount");
            mWordCountToday = response.getInt("wordCountToday");
            mDailyTargetRemaining = response.getInt("dailyTargetRemaining");
            mDailyTarget = response.getInt("dailyTarget");
            mNbDayRemaining = response.getInt("nbDayRemaining");

            JSONObject _links = response.getJSONObject("links");
            links.put("self", _links.getString("self"));
            links.put("friends", _links.getString("friends"));
            links.put("history", _links.getString("history"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getGoal() {
        return mGoal;
    }

    public void setGoal(int goal) {
        this.mGoal = goal;
    }

    public int getWordcount() {
        return mWordcount;
    }

    public void setWordcount(int wordcount) {
        this.mWordcount = wordcount;
    }

    public int getWordCountToday() {
        return mWordCountToday;
    }

    public void setWordCountToday(int wordCountToday) {
        this.mWordCountToday = wordCountToday;
    }

    public int getDailyTarget() {
        return mDailyTarget;
    }

    public void setDailyTarget(int dailyTarget) {
        this.mDailyTarget = dailyTarget;
    }

    public int getDailyTargetRemaining() {
        return mDailyTargetRemaining;
    }

    public void setDailyTargetRemaining(int dailyTargetRemaining) {
        this.mDailyTargetRemaining = dailyTargetRemaining;
    }

    public HashMap<String, String> getLinks() {
        return links;
    }

    public void setLinks(HashMap<String, String> links) {
        this.links = links;
    }


    public int getNbDayRemaining() {
        return mNbDayRemaining;
    }

    public void setNbDayRemaining(int nbDayRemaining) {
        this.mNbDayRemaining = nbDayRemaining;
    }
}
