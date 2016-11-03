package nanowrimo.onishinji.databases;

import java.text.Normalizer;

/**
 * Created by amandine on 03/11/2016.
 */

public class UsernameConverter {

    public static String convert(String username) {
        String encodedName = username;
        encodedName = encodedName.toLowerCase();
        encodedName = encodedName.trim();
        encodedName = replaceDot(encodedName);
        encodedName = replaceApostrophe(encodedName);
        encodedName = removeAccents(encodedName);
        return encodedName;
    }

    protected static String replaceDot(String text) {
        return (text == null) ? null : text.replaceAll("[@\\.\\ ]", "-");
    }

    protected static String replaceApostrophe(String text) {
        return (text == null) ? null : text.replaceAll("[@\\'\\ ]", "-");
    }

    protected static String removeAccents(String text) {
        return text == null ? null :
                Normalizer.normalize(text, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
