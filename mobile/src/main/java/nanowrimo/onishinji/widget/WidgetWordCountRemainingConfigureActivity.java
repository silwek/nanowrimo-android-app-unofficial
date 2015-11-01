package nanowrimo.onishinji.widget;

import nanowrimo.onishinji.R;

/**
 * Created by guillaume on 11/11/14.
 */
public class WidgetWordCountRemainingConfigureActivity extends WidgetDailyWordCountRemainingConfigureActivity {


    protected void setContentViewStub() {

        setContentView(R.layout.widget_word_count_remeaning_configure);
    }

    protected WidgetDailyWordCountRemaining getWidget() {
        return new WidgetWordCountRemaining();
    }
}
