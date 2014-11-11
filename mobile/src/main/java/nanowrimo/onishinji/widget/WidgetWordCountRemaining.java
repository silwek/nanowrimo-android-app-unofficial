package nanowrimo.onishinji.widget;

import android.content.Context;
import android.content.Intent;

import nanowrimo.onishinji.utils.ProgressPieDailyUtils;
import nanowrimo.onishinji.utils.ProgressPieGlobalUtils;
import nanowrimo.onishinji.utils.ProgressPieUtils;

/**
 * Created by guillaume on 11/11/14.
 */
public class WidgetWordCountRemaining extends WidgetDailyWordCountRemaining {


    public WidgetWordCountRemaining() {
        this.pieManager = new ProgressPieGlobalUtils();
    }

    @Override
    public Intent createInternalIntent(Context context) {
        return new Intent(context, WidgetWordCountRemaining.class);
    }
}
