package nanowrimo.onishinji.utils;

import nanowrimo.onishinji.model.WritingSession;

/**
 * Created by Silwèk on 08/10/15.
 */
public class URLUtils {

    final static String baseUrlCamp = "https://campnanowrimo.herokuapp.com/users/";
    final static String baseUrlNano = "https://nanowrimo.herokuapp.com/users/";

    public static String getUserUrl(int type, String username) {
        final String baseUrl = type == WritingSession.NANOWRIMO ? baseUrlNano : baseUrlCamp;
        return baseUrl + StringUtils.removeAccents(username).replace(" ", "-");
    }

    public static String getFriendUserUrl(int type, String username) {
        final String baseUrl = type == WritingSession.NANOWRIMO ? baseUrlNano : baseUrlCamp;
        return baseUrl + StringUtils.removeAccents(username).replace(" ", "-") + "/friends";
    }

}
