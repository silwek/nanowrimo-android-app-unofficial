package nanowrimo.onishinji.utils;

import android.net.Uri;

import java.util.Calendar;

import nanowrimo.onishinji.model.WritingSession;

/**
 * Created by Silwèk on 08/10/15.
 */
public class URLUtils {

    protected final static String baseUrlCamp = "https://campnanowrimo.herokuapp.com/users/";
    protected final static String baseUrlNano = "https://nanowrimo.herokuapp.com/users/";
    public final static String WRITE_API = "http://nanowrimo.org/api/wordcount";

    public static String getUserUrl(int type, String username) {
        return addTimeZone(getBaseUserUrl(type, username));
    }

    public static String getHistoryUserUrl(int type, String username) {
        return addTimeZone(getBaseUserUrl(type, username) + "/history");
    }

    public static String getFriendUserUrl(int type, String username) {
        final String baseUrl = getBaseUserUrl(type, username);
        return addTimeZone(baseUrl + "/friends");
    }

    protected static String getBaseUserUrl(int type, String username) {
        final String baseUrl = type == WritingSession.NANOWRIMO ? baseUrlNano : baseUrlCamp;
        return baseUrl + StringUtils.encodeUserNameForApi(username);
    }

    protected static String addTimeZone(String url) {
        Uri uri = Uri.parse(url);
        Calendar c = Calendar.getInstance();
        String timeZone = c.getTimeZone().getID();
        return uri.buildUpon().appendQueryParameter("timeZone", timeZone).build().toString();
    }

}
