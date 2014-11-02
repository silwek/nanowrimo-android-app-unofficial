package nanowrimo.onishinji.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guillaume on 31/10/14.
 */
public class User {

    private String id = "";
    private String name = "";
    private int wordcount = 0;
    private int wordCountToday = 0;
    private int dailyTarget = 0;
    private int dailyTargetRemaining = 0;
    private int nbDayRemaining = 0;

    public User(JSONObject response) {

        try {
            id = response.getString("id");
            name = response.getString("name");
            wordcount = response.getInt("wordcount");
            wordCountToday = response.getInt("wordCountToday");
            dailyTargetRemaining = response.getInt("dailyTargetRemaining");
            dailyTarget = response.getInt("dailyTarget");
            nbDayRemaining = response.getInt("nbDayRemaining");
        } catch (JSONException e) {

        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWordcount() {
        return wordcount;
    }

    public void setWordcount(int wordcount) {
        this.wordcount = wordcount;
    }

    public int getWordCountToday() {
        return wordCountToday;
    }

    public void setWordCountToday(int wordCountToday) {
        this.wordCountToday = wordCountToday;
    }

    public int getDailyTarget() {
        return dailyTarget;
    }

    public void setDailyTarget(int dailyTarget) {
        this.dailyTarget = dailyTarget;
    }

    public int getDailyTargetRemaining() {
        return dailyTargetRemaining;
    }

    public void setDailyTargetRemaining(int dailyTargetRemaining) {
        this.dailyTargetRemaining = dailyTargetRemaining;
    }

    public int getNbDayRemaining() {
        return nbDayRemaining;
    }

    public void setNbDayRemaining(int nbDayRemaining) {
        this.nbDayRemaining = nbDayRemaining;
    }
}
