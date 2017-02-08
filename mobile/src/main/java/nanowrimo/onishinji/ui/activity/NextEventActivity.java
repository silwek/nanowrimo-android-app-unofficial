package nanowrimo.onishinji.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Amandine Ferrand on 08/02/2017.
 */

public class NextEventActivity extends ToolbarActivity {
    TextView mNextNano, mNextCamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_event);

        setTitle("");

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mNextNano = (TextView) findViewById(R.id.next_nanowrimo);
        mNextCamp = (TextView) findViewById(R.id.next_camp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNextEvents();
    }

    protected void showNextEvents() {
        showNextNanoEvent();
        showNextCampEvent();
    }

    protected void showNextNanoEvent() {
        Calendar nano = WritingSessionHelper.getNextNanowrimoSession();
        if (isEventOnGoing(nano)) {
            mNextNano.setText(R.string.nextevent_nanowrimo_ongoing);
        } else {
            final int nextYear = nano.get(Calendar.YEAR);
            mNextNano.setText(getString(R.string.nextevent_nanowrimo_next, nextYear));
        }
    }

    protected void showNextCampEvent() {
        Calendar camp = WritingSessionHelper.getNextCampSession();
        if (isEventOnGoing(camp)) {
            mNextCamp.setText(R.string.nextevent_camp_ongoing);
        } else {
            final int lastDayOfTheMonth = camp.getActualMaximum(Calendar.DAY_OF_MONTH);
            final String month = camp.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            final int nextYear = camp.get(Calendar.YEAR);
            mNextCamp.setText(getString(R.string.nextevent_camp_next, lastDayOfTheMonth, month, nextYear));
        }
    }

    protected boolean isEventOnGoing(Calendar eventDate) {
        Calendar now = WritingSessionHelper.getNow();
        return (now.get(Calendar.MONTH) == eventDate.get(Calendar.MONTH) && now.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR));
    }
}
