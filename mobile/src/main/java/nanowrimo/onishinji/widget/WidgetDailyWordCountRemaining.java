package nanowrimo.onishinji.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.activity.MyActivity;
import nanowrimo.onishinji.ui.widget.WordCountProgress;
import nanowrimo.onishinji.utils.StringUtils;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetDailyWordCountRemainingConfigureActivity WidgetDailyWordCountRemainingConfigureActivity}
 */
public class WidgetDailyWordCountRemaining extends AppWidgetProvider {
    private static final String ACTION_UPDATE_CLICK =
            "nanowrimo.onishinji.widget.action.UPDATE_CLICK";


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

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        final CharSequence widgetText = WidgetDailyWordCountRemainingConfigureActivity.loadTitlePref(context, appWidgetId);

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_daily_word_count_remeaning);
        views.setViewVisibility(R.id.loader, View.VISIBLE);

        Intent i = new Intent(context, WidgetDailyWordCountRemaining.class);
        i.setAction(ACTION_UPDATE_CLICK+"_"+appWidgetId);
        i.putExtra("username", widgetText);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
        views.setOnClickPendingIntent(R.id.widget, pi);

        // Configure http request
        HttpClient.getInstance().setContext(context);
        final String url = StringUtils.getUserUrl((String) widgetText);
        JSONObject params = new JSONObject();
        Log.v("WIDGET", appWidgetId + " make an request to " + url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

               User user = new User(response);
               // views.setTextViewText(R.id.appwidget_wordcount, "" + user.getDailyTargetRemaining());

                WordCountProgress pg = new WordCountProgress(context);

                pg.setIconRessource(R.drawable.ic_pen);
                pg.configureView();
                pg.compute(user.getWordCountToday(), user.getDailyTarget(), false);
                pg.setText(""+user.getDailyTarget(), user.getName());

                pg.setLayoutParams(new ViewGroup.LayoutParams(100,100));
                pg.measure(100, 100);
                pg.layout(0, 0, pg.getMeasuredWidth(), pg.getMeasuredHeight());
                pg.forceLayout();

                pg.setDrawingCacheEnabled(true);
                pg.buildDrawingCache(true);

                pg.requestLayout();

                pg.buildDrawingCache();
                Bitmap bm = pg.getDrawingCache();

//                Log.d("widget", bm.toString());

                views.setImageViewBitmap(R.id.appwidget_progress, bm);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, context.getString(R.string.error_network_widget_toast, widgetText, error.getMessage()), Toast.LENGTH_SHORT).show();

                WordCountProgress pg = new WordCountProgress(context);

                pg.configureView();
                pg.compute(0, 1, false);

                pg.setIconRessource(R.drawable.ic_pen);
                pg.setText(context.getString(R.string.error_network_widget), (String) widgetText);
                pg.getProgressPieView().setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));

                int size = 200;
                pg.setLayoutParams(new ViewGroup.LayoutParams(size, size));
                pg.measure(size, size);
                pg.layout(0, 0, pg.getMeasuredWidth(), pg.getMeasuredHeight());
                pg.forceLayout();

                pg.setDrawingCacheEnabled(true);
                pg.buildDrawingCache(true);

                pg.requestLayout();

                pg.buildDrawingCache();
                Bitmap bm = pg.getDrawingCache();

                views.setImageViewBitmap(R.id.appwidget_progress, bm);

                //  views.setTextViewText(R.id.appwidget_text, "error de volley");
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });
        HttpClient.getInstance().add(request);
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

        if (intent.getAction().contains(ACTION_UPDATE_CLICK)) {
            onUpdate(context);

            Intent i;
            PackageManager manager = context.getPackageManager();
            try {
                i = manager.getLaunchIntentForPackage("nanowrimo.onishinji");
                i.putExtra("username", intent.getStringExtra("username"));

                if (i == null)
                    throw new PackageManager.NameNotFoundException();
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                context.startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {

            }

        }
    }
}


