package nanowrimo.onishinji.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.User;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetDailyWordCountRemainingConfigureActivity WidgetDailyWordCountRemainingConfigureActivity}
 */
public class WidgetDailyWordCountRemaining extends AppWidgetProvider {
    private static final String ACTION_UPDATE_CLICK =
            "nanowrimo.onishinji.widget.action.UPDATE_CLICK";

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        // An explicit intent directed at the current class (the "self").
        Intent intent = new Intent(context, getClass());
        //intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            WidgetDailyWordCountRemainingConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);

        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager,
                         final int appWidgetId) {

        CharSequence widgetText = WidgetDailyWordCountRemainingConfigureActivity.loadTitlePref(context, appWidgetId);

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_daily_word_count_remeaning);
        views.setViewVisibility(R.id.loader, View.VISIBLE);

        Intent i = new Intent(context, WidgetDailyWordCountRemaining.class);
        i.setAction(ACTION_UPDATE_CLICK);
        PendingIntent pi = PendingIntent.getBroadcast(context,0, i,0);
        views.setOnClickPendingIntent(R.id.button_update, pi);

        // Configure http request
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = context.getString(R.string.base_url) + widgetText;
        JSONObject params = new JSONObject();
        Log.v("WIDGET", appWidgetId + " make an request to " + url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                views.setViewVisibility(R.id.loader, View.GONE);
                Log.d("WIDGET", "receive response from serv " + response.toString());

                views.setViewVisibility(R.id.error_container, View.GONE);
                User user = new User(response);
                views.setTextViewText(R.id.appwidget_username, user.getName());
                views.setTextViewText(R.id.appwidget_wordcount, "" + user.getDailyTargetRemaining());

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                views.setViewVisibility(R.id.loader, View.GONE);
                views.setTextViewText(R.id.error_container, "Erreur");
                views.setViewVisibility(R.id.error_container, View.VISIBLE);
                //  views.setTextViewText(R.id.appwidget_text, "error de volley");
                appWidgetManager.updateAppWidget(appWidgetId, views);
                Log.e("WIDGET", "receive error from serv " + error.getMessage() + " for url " + url);
            }
        });
        queue.add(request);
    }

    /**
     * A general technique for calling the onUpdate method,
     * requiring only the context parameter.
     *
     * @author John Bentley, based on Android-er code.
     * @see <a href="http://android-er.blogspot.com
     * .au/2010/10/update-widget-in-onreceive-method.html">
     * Android-er > 2010-10-19 > Update Widget in onReceive() method</a>
     */
    private void onUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);

        // Uses getClass().getName() rather than MyWidget.class.getName() for
        // portability into any App Widget Provider Class
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(), getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d("WIDGET", " receive intent " + intent.getAction());

        if (ACTION_UPDATE_CLICK.equals(intent.getAction())) {
            onUpdate(context);
        }
    }
}


