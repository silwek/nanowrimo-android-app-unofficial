package nanowrimo.onishinji.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.task.GetFavoriesRanking;
import nanowrimo.onishinji.ui.activity.FavoriesActivity;
import nanowrimo.onishinji.utils.URLUtils;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 11/10/15.
 */
public class FavsFragment extends Fragment implements GetFavoriesRanking.OnFavoritesSortedListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favs, container, false);

        view.findViewById(R.id.bt_add_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAddUserDialog(true);
            }
        });

        view.findViewById(R.id.bt_see_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FavoriesActivity.class));
            }
        });

        view.findViewById(R.id.section_label_favs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FavoriesActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showRank();
    }

    protected void showRank() {
        if (getActivity() == null)
            return;
        final Database database = Database.getInstance(getActivity());
        GetFavoriesRanking task = new GetFavoriesRanking(this);
        task.execute(database.getUsers());

    }


    public void displayAddUserDialog(final boolean canCloseDialog) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(getString(R.string.dialog_add_user_title, WritingSessionHelper.getInstance().getSessionName()));

        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        input.setHint(getString(R.string.default_username));
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.follow), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String value = input.getText().toString().trim();

                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.please_loading), true);
                progressDialog.show();

                // Test username
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                final String url = URLUtils.getUserUrl(WritingSessionHelper.getInstance().getSessionType(), value);
                JSONObject params = new JSONObject();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (getActivity() == null)
                            return;

                        User user = new User(response);

                        Database.getInstance(getActivity()).addUser(user.getId(), user.getName());
                        onAddUser(user);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("error", error.toString());

                        if (getActivity() == null)
                            return;

                        progressDialog.dismiss();
                        Toast alert = Toast.makeText(getActivity(), getString(R.string.name_invalid), Toast.LENGTH_SHORT);
                        alert.show();

                        if (!canCloseDialog) {
                            displayAddUserDialog(canCloseDialog);
                        }
                    }
                });
                queue.add(request);

            }
        });

        if (canCloseDialog) {
            alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });
        }

        alert.setCancelable(canCloseDialog);

        alert.show();
    }

    protected void onAddUser(User user) {
        if (getActivity() == null)
            return;
        showRank();
    }

    @Override
    public void onFavoritesSorted(ArrayList<User> users) {

    }
}
