package nanowrimo.onishinji.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Silwek on 29/03/2015.
 */
public class PreferencesHelper {


    private static final String PREFS_NAME = "nanowrimo.onishinji.app.prefs";
    private static final String PREFS_SESSION_NAME = "nanowrimo.onishinji.app.prefs.PREFS_SESSION_NAME";
    private static final String PREFS_SESSION_START = "nanowrimo.onishinji.app.prefs.PREFS_SESSION_START";


    public static String getSessionName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String sessionName = prefs.getString(PREFS_SESSION_NAME, "");
        return sessionName;
    }

    public static void setSessionName(Context context, String sessionName){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_SESSION_NAME, sessionName);
        editor.commit();
    }

    public static Date getSessionStart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String sessionStart = prefs.getString(PREFS_SESSION_START, "");
        if(!TextUtils.isEmpty(sessionStart)){
            return new Date(Long.parseLong(sessionStart));
        }
        return new Date();
    }

    public static void setSessionStart(Context context, Date sessionStart){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_SESSION_START, String.valueOf(sessionStart.getTime()));
        editor.commit();
    }
}
