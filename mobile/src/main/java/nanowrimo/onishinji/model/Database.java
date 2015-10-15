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

    public void addUser(String username, String name) {
        if (!this.users.contains(username)) {
            this.users.add(username);
            userInfos.put(username, name);
        }
        saveUsersString();

    }

    public void deleteUser(String username) {

        Log.d("DB", "want to delete " + username + " (local " + getUsersString());

        if (this.users.contains(username)) {
            Log.d("DB", "removeeeee " + username);
            this.users.remove(username);
            this.userInfos.remove(username);
        }

        Log.d("DB", "has delete " + username + " (local " + this.users.toString());

        saveUsersString();
    }

    public boolean isCurrentUser(String username) {
        if (this.users.indexOf(username) == 0) {
            return true;
        }

        return false;
    }

    public boolean userIsMarkedAsFavorite(String username) {
        if (this.users.contains(username)) {
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

        // BusManager.getInstance().getBus().post("userSaved");
    }

    public CharSequence getNiceTitle(int position) {
        return userInfos.get(this.users.get(position));
    }

    public String getNiceTitle(String username) {
        return userInfos.get(username);
    }
}
