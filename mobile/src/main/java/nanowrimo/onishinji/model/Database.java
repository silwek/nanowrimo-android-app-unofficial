package nanowrimo.onishinji.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import nanowrimo.onishinji.utils.StringUtils;

/**
 * Created by guillaume on 01/11/14.
 */
public class Database {

    private final Context context;

    private static final String PREFS_NAME = "nanowrimo.onishinji.app.users";
    private static final String PREF_PREFIX_KEY = "users";
    private static final String PREF_PREFIX_KEY_USERS_INFOS = "usersInfo";
    private ArrayList<String> users = new ArrayList<String>();
    private HashMap<String, String> userInfos = new HashMap<>();

    private static Database mInstance;

    public static Database getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new Database(ctx);
        }
        return mInstance;
    }

    public Database(Context ctx) {
        this.context = ctx;
        String[] spliced = TextUtils.split(getUsersString(), ",");
        for (int i = 0; i < spliced.length; i++) {
            this.users.add(spliced[i]);
        }

        this.userInfos = getUsersInfo();
    }

    public void addUser(String id, String name) {
        id = id.toLowerCase();
        if (!this.users.contains(id)) {
            this.users.add(id);
            userInfos.put(id, name);
        }
        saveUsersString();

    }

    public void deleteUser(String id) {

        Log.d("DB", "want to delete " + id + " (local " + getUsersString());

        if (this.users.contains(id)) {
            Log.d("DB", "removeeeee " + id);
            this.users.remove(id);
            this.userInfos.remove(id);
        }

        Log.d("DB", "has delete " + id + " (local " + this.users.toString());

        saveUsersString();
    }

    public boolean isCurrentUser(String id) {
        if (this.users.indexOf(id.toLowerCase()) == 0) {
            return true;
        }

        return false;
    }

    public boolean userIsMarkedAsFavorite(String id) {
        if (this.users.contains(id)) {
            return true;
        }

        return false;
    }

    public ArrayList<String> getUsers() {
        return this.users;
    }

    public String getUsersString() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String usersString = prefs.getString(PREF_PREFIX_KEY, "");

        return usersString;
    }

    protected HashMap<String, String> getUsersInfo() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String usersString = prefs.getString(PREF_PREFIX_KEY_USERS_INFOS, "");

        HashMap<String, String> map = new HashMap<String, String>();
        try {
            JSONObject json = new JSONObject(usersString);

            map = (HashMap<String, String>) StringUtils.toMap(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }

    protected void saveUsersString() {

        Log.d("DB", "will save " + TextUtils.join(",", users));
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY, TextUtils.join(",", users));

        JSONObject json = new JSONObject(this.userInfos);
        String infos = json.toString();

        prefs.putString(PREF_PREFIX_KEY_USERS_INFOS, infos);

        prefs.commit();
    }

    public String getNiceTitle(String id) {
        return userInfos.get(id);
    }

    public void clearUsers() {
        Log.d("DB", "will be cleared ");
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY);
        prefs.remove(PREF_PREFIX_KEY_USERS_INFOS);
        prefs.commit();
        mInstance = null;
    }
}
