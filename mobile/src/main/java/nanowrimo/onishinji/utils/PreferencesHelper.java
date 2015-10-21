package nanowrimo.onishinji.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Date;

import nanowrimo.onishinji.model.WritingSession;

/**
 * Created by Silwek on 29/03/2015.
 */
public class PreferencesHelper {


    private static final String PREFS_NAME = "nanowrimo.onishinji.app.prefs";
    private static final String PREFS_FIRST_LAUNCH = "nanowrimo.onishinji.app.prefs.PREFS_FIRST_LAUNCH";
    private static final String PREFS_SESSION_NAME = "nanowrimo.onishinji.app.prefs.PREFS_SESSION_NAME";
    private static final String PREFS_SESSION_START = "nanowrimo.onishinji.app.prefs.PREFS_SESSION_START";
    private static final String PREFS_SESSION_TYPE = "nanowrimo.onishinji.app.prefs.PREFS_SESSION_TYPE";
    private static final String PREFS_USER_NAME = "nanowrimo.onishinji.app.prefs.PREFS_USER_NAME";
    private static final String PREFS_SECRET_KEY = "nanowrimo.onishinji.app.prefs.PREFS_SECRET_KEY";


    public static boolean isFirstLaunch(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        boolean firstLaunch = prefs.getBoolean(PREFS_FIRST_LAUNCH, true);
        return firstLaunch;
    }

    public static void setFirstLaunch(Context context, boolean firstLaunch) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREFS_FIRST_LAUNCH, firstLaunch);
        editor.commit();
    }

    public static String getSessionName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String sessionName = prefs.getString(PREFS_SESSION_NAME, "");
        return sessionName;
    }

    public static void setSessionName(Context context, String sessionName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_SESSION_NAME, sessionName);
        editor.commit();
    }

    public static Date getSessionStart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String sessionStart = prefs.getString(PREFS_SESSION_START, "");
        if (!TextUtils.isEmpty(sessionStart)) {
            return new Date(Long.parseLong(sessionStart));
        }
        return new Date();
    }

    public static void setSessionType(Context context, int sessionType) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREFS_SESSION_TYPE, sessionType);
        editor.commit();
    }

    public static int getSessionType(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int sessionStart = prefs.getInt(PREFS_SESSION_TYPE, WritingSession.CAMP);
        return sessionStart;
    }

    public static void setSessionStart(Context context, Date sessionStart) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_SESSION_START, String.valueOf(sessionStart.getTime()));
        editor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String userName = prefs.getString(PREFS_USER_NAME, "");
        return userName;
    }

    public static void setUserName(Context context, String userName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_USER_NAME, userName);
        editor.commit();
    }

    public static String getSecretKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String userName = prefs.getString(PREFS_SECRET_KEY, "");
        return userName;
    }

    public static void setSecretKey(Context context, String secretKey) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        if (TextUtils.isEmpty(secretKey)) {
            editor.remove(PREFS_SECRET_KEY);
        } else {
            editor.putString(PREFS_SECRET_KEY, secretKey);
        }
        editor.commit();
    }

    public static void clearAllData(Context context) {
        context.getSharedPreferences(PREFS_NAME, 0).edit().clear().commit();
    }
}
