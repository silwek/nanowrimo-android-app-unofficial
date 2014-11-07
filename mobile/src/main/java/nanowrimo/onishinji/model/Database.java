package nanowrimo.onishinji.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by guillaume on 01/11/14.
 */
public class Database {

    private final Context context;

    private static final String PREFS_NAME = "nanowrimo.onishinji.app.users";
    private static final String PREF_PREFIX_KEY = "users";
    private ArrayList<String> users = new ArrayList<String>();

    public Database(Context ctx) {
        this.context = ctx;

        String[] spliced = TextUtils.split(getUsersString(), ",");
        for (int i = 0; i < spliced.length; i++) {
            this.users.add(spliced[i]);
        }
    }

    public void addUser(String username) {
        if (!this.users.contains(username)) {
            this.users.add(username);
        }
        saveUsersString();
    }

    public void deleteUser(String username) {

        Log.d("DB", "want to delete " + username + " (local " + getUsersString());

        if (this.users.contains(username)) {
            this.users.remove(username);
        }
        saveUsersString();
    }

    public ArrayList<String> getUsers() {
        return this.users;
    }

    protected String getUsersString() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String usersString = prefs.getString(PREF_PREFIX_KEY, "");

        return usersString;
    }

    protected void saveUsersString() {

        Log.d("DB", "will save " + TextUtils.join(",", users));
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY, TextUtils.join(",", users));
        prefs.commit();
    }
}
