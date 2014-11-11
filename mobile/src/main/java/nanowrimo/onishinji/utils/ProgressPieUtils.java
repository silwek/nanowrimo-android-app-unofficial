package nanowrimo.onishinji.utils;

import android.content.Context;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.widget.WordCountProgress;

/**
 * Created by guillaume on 11/11/14.
 */
public class ProgressPieUtils {

    public WordCountProgress constructWordCountProgressPie(Context context) {
        WordCountProgress pg = new WordCountProgress(context);
        pg.configureView();
        return pg;
    }

    public WordCountProgress getWordCountProgressPie( Context context, User user) {
        WordCountProgress pg = constructWordCountProgressPie(context);
        return pg;
    }
}
