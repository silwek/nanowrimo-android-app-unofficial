package nanowrimo.onishinji.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.utils.StringUtils;


/**
 * The configuration screen for the {@link WidgetDailyWordCountRemaining WidgetDailyWordCountRemaining} AppWidget.
 */
public class WidgetDailyWordCountRemainingConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    private static final String PREFS_NAME = "nanowrimo.onishinji.widget.WidgetDailyWordCountRemaining";
    private static final String PREF_PREFIX_KEY = "appwidget_daily_wordcount_remaining_";
    private ProgressBar mLoader;
    private Button mButtonValid;

    public WidgetDailyWordCountRemainingConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentViewStub();

        mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        mButtonValid = (Button) findViewById(R.id.add_button);
        mButtonValid.setOnClickListener(mOnClickListener);

         mLoader = (ProgressBar) findViewById(R.id.loader);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        HttpClient.getInstance().setContext(this);
        //mAppWidgetText.setText(loadTitlePref(WidgetDailyWordCountRemainingConfigureActivity.this, mAppWidgetId));
    }

    protected void setContentViewStub() {

        setContentView(R.layout.widget_daily_word_count_remeaning_configure);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = WidgetDailyWordCountRemainingConfigureActivity.this;

            // When the button is clicked, store the string locally
            String username = mAppWidgetText.getText().toString();

            // Test username
            final String url = StringUtils.getUserUrl(username);
            JSONObject params = new JSONObject();
            mLoader.setVisibility(View.VISIBLE);
            mButtonValid.setClickable(false);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    mButtonValid.setClickable(true);
                    mLoader.setVisibility(View.GONE);
                    User user = new User(response);
                    saveTitlePref(context, mAppWidgetId, user.getId());

                    // It is the responsibility of the configuration activity to update the app widget
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                    WidgetDailyWordCountRemaining c = getWidget();
                    c.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                    // Make sure we pass back the original appWidgetId
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    mButtonValid.setClickable(true);
                    mLoader.setVisibility(View.GONE);
                    Toast alert = Toast.makeText(WidgetDailyWordCountRemainingConfigureActivity.this,getString(R.string.name_invalid), Toast.LENGTH_SHORT);
                    alert.show();
                }
            });

            HttpClient.getInstance().add(request);

        }
    };

    protected WidgetDailyWordCountRemaining getWidget() {
        return new WidgetDailyWordCountRemaining();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return "";
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }
}



