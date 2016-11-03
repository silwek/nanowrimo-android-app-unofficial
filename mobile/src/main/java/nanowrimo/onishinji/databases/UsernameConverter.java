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
        encodedName = replaceExtraDash(encodedName);
        encodedName = replaceEndDash(encodedName);
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

    protected static String replaceExtraDash(String text) {
        if (text == null)
            return null;
        String loopText = new String(text);
        while (loopText.contains("--")) {
            loopText = loopText.replaceAll("--", "-");
        }
        return loopText;
    }

    protected static String replaceEndDash(String text) {
        if (text.endsWith("-")) {
            return text.substring(0, text.length() - 1);
        }
        return text;
    }
}
