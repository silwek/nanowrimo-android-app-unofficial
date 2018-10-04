package nanowrimo.onishinji.utils;

import android.net.Uri;

import java.util.Calendar;

import nanowrimo.onishinji.databases.UsernameConverter;
import nanowrimo.onishinji.model.WritingSession;

/**
 * Created by Silwèk on 08/10/15.
 */
public class URLUtils {

    protected final static String baseUrlCamp = "https://campnanowrimo.herokuapp.com/";
    protected final static String baseUrlNano = "https://nanowrimo.herokuapp.com/";
    protected final static String baseUrlCampV2 = "https://campnanowrimo.herokuapp.com/v2/";
    protected final static String baseUrlNanoV2 = "https://nanowrimo.herokuapp.com/v2/";
    public final static String WRITE_API = "https://nanowrimo.org/api/wordcount";

    public static String getUserUrl(int type, String username) {
        return addTimeZone(getUrlV2(type, "users/", username));
    }

    public static String getProjectUrl(int type, String username) {
        return addTimeZone(getUrlV2(type, "project/", username));
    }

    public static String getHistoryUserUrl(int type, String username) {
        return addTimeZone(getBaseUserUrl(type, username) + "/history");
    }

    public static String getFriendUserUrl(int type, String username) {
        final String baseUrl = getBaseUserUrl(type, username);
        return addTimeZone(baseUrl + "/friends");
    }

    protected static String getUrlV2(int type, String path, String username) {
        final String baseUrlV2 = type == WritingSession.NANOWRIMO ? baseUrlNanoV2 : baseUrlCampV2;
        return baseUrlV2 + path + UsernameConverter.convert(username);
    }

    protected static String getBaseUserUrl(int type, String username) {
        final String baseUrl = type == WritingSession.NANOWRIMO ? baseUrlNano : baseUrlCamp;
        return baseUrl + "users/" + UsernameConverter.convert(username);
    }

    protected static String addTimeZone(String url) {
        Uri uri = Uri.parse(url);
        Calendar c = Calendar.getInstance();
        String timeZone = c.getTimeZone().getID();
        return uri.buildUpon().appendQueryParameter("timeZone", timeZone).build().toString();
    }

}
