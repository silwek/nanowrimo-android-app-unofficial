package nanowrimo.onishinji.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.utils.StringUtils;
import nanowrimo.onishinji.widget.WidgetDailyWordCountRemaining;

public class PickerUserFragment extends DialogFragment {

    private EditText mEditText;
    private EditNameDialogListener mListener;
    private String mCurrentUser;
    private Button mButtonValid;
    private ProgressBar mLoader;

    public void setListener(EditNameDialogListener listener) {
        mListener = listener;
    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(User user);
    }


    public PickerUserFragment() {
        // Empty constructor required for DialogFragment
    }

    public static PickerUserFragment newInstance(String title, ArrayList<String> choices) {
        PickerUserFragment frag = new PickerUserFragment();
        Bundle args = new Bundle();

        args.putStringArrayList("choices", choices);
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pick_user, container);
        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        String title = getArguments().getString("title", getString(R.string.dialog_picker_default));
        final ArrayList<String> choices = getArguments().getStringArrayList("choices");

        getDialog().setTitle(title);
        //  getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, choices); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!choices.get(position).equals("")) {
                    mEditText.setText(choices.get(position));
                    spinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mButtonValid = (Button) view.findViewById(R.id.button_save);
        mLoader = (ProgressBar) view.findViewById(R.id.progressBar);

        mButtonValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mEditText.getText().toString().equals("")) {
                    mCurrentUser = mEditText.getText().toString();

                    mLoader.setVisibility(View.VISIBLE);
                    // Test username
                    final String url = StringUtils.getUserUrl(mCurrentUser);
                    JSONObject params = new JSONObject();
                    mLoader.setVisibility(View.VISIBLE);
                    mButtonValid.setClickable(false);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            handleResponse(response);


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            mLoader.setVisibility(View.GONE);
                            mButtonValid.setClickable(true);

                            Toast alert = Toast.makeText(getActivity(), getString(R.string.name_invalid), Toast.LENGTH_SHORT);
                            alert.show();
                        }
                    });

                    // Search from cache first, make request in second
                    Cache c = HttpClient.getInstance().getQueue().getCache();
                    Cache.Entry entry = c.get(url);
                    if (entry != null) {
                        // fetch the data from cache
                        try {
                            String data = new String(entry.data, "UTF-8");
                            handleResponse(new JSONObject(data));

                            c.invalidate(url, true);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        HttpClient.getInstance().add(request, true);
                    }

                }
            }
        });
        return view;
    }

    private void handleResponse(JSONObject response) {
        mLoader.setVisibility(View.GONE);
        User user = new User(response);

        if (mListener != null) {
            mListener.onFinishEditDialog(user);
        }

        dismiss();
    }

}