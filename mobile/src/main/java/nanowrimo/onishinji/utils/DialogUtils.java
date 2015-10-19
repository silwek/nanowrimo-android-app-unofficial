package nanowrimo.onishinji.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.activity.SettingsActivity;

/**
 * Created by Silwek on 16/10/15.
 */
public class DialogUtils {

    public interface CallbackWithUser {
        void onSuccess(User user);
    }

    public interface Callback {
        void onSuccess();
    }


    public static void displayRemoveUserDialog(final Context context, final String id, final String name, final Callback callback) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(context.getString(R.string.dialog_remove_user_title));
        alert.setMessage(context.getString(R.string.dialog_remove_user_message, name));
        alert.setPositiveButton(context.getString(R.string.unfollow_people), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Database.getInstance(context).deleteUser(id);
                callback.onSuccess();
            }
        });
        alert.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.setCancelable(true);
        alert.show();
    }

    public static void displayAddUserDialog(final Context context, final CallbackWithUser callback) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(context.getString(R.string.dialog_add_user_title, WritingSessionHelper.getInstance().getSessionName()));

        // Set an EditText view to get user input
        final EditText input = new EditText(context);
        input.setHint(context.getString(R.string.default_username));
        alert.setView(input);

        alert.setPositiveButton(context.getString(R.string.follow), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String value = input.getText().toString().trim();

                final ProgressDialog progressDialog = ProgressDialog.show(context, "", context.getString(R.string.please_loading), true);
                progressDialog.show();

                // Test username
                RequestQueue queue = Volley.newRequestQueue(context);
                final String url = URLUtils.getUserUrl(WritingSessionHelper.getInstance().getSessionType(), value);
                JSONObject params = new JSONObject();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (context == null)
                            return;

                        User user = new User(response);

                        Database.getInstance(context).addUser(user.getId(), user.getName());
                        callback.onSuccess(user);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("error", error.toString());

                        if (context == null)
                            return;

                        progressDialog.dismiss();
                        Toast alert = Toast.makeText(context, context.getString(R.string.name_invalid), Toast.LENGTH_SHORT);
                        alert.show();
                    }
                });
                queue.add(request);

            }
        });

        alert.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.setCancelable(true);

        alert.show();
    }

    public static void displayAddUserDialog(final Context context, final User user, final CallbackWithUser callback) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(context.getString(R.string.dialog_add_user_title, WritingSessionHelper.getInstance().getSessionName()));

        alert.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Database.getInstance(context).addUser(user.getId(), user.getName());
                callback.onSuccess(user);
            }
        });

        alert.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }


    public static void displayMissingSecretKey(final Context context, final Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(context.getString(R.string.dashboard_hotstuff_missing_secretkey_title));

        alert.setMessage(context.getString(R.string.dashboard_hotstuff_missing_secretkey_message));

        alert.setPositiveButton(context.getString(R.string.dashboard_hotstuff_missing_secretkey_positive), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(activity, SettingsActivity.class);
                activity.startActivity(intent);
            }
        });

        alert.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
}
