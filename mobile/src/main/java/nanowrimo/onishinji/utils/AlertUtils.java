package nanowrimo.onishinji.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Silwek on 17/10/15.
 */
public class AlertUtils {
    public static void display(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void displayError(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void display(Context context, int messageId) {
        if (context != null)
            Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    public static void displayError(Context context, int messageId) {
        if (context != null)
            Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }
}
