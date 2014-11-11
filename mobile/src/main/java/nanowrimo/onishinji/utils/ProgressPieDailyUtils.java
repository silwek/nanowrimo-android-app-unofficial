package nanowrimo.onishinji.utils;

import android.content.Context;

import java.text.NumberFormat;
import java.util.Locale;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.widget.WordCountProgress;

/**
 * Created by guillaume on 11/11/14.
 */
public class ProgressPieDailyUtils extends ProgressPieUtils {


    public WordCountProgress constructWordCountProgressPie(Context context) {
        WordCountProgress pg = new WordCountProgress(context);
        pg.setIconRessource(R.drawable.ic_pen);
        pg.configureView();
        return pg;
    }

    public WordCountProgress getWordCountProgressPie( Context context, User user) {

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());

        WordCountProgress pg = constructWordCountProgressPie(context);

        pg.compute(user.getWordCountToday(), user.getDailyTarget(), false);
        pg.setText("" + numberFormat.format(user.getDailyTarget()), user.getName());
        return pg;
    }
}
