package nanowrimo.onishinji.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;

public class PickerUserFragment extends DialogFragment {

    private EditText mEditText;
    private EditNameDialogListener mListener;
    private String mCurrentUser;

    public void setListener(EditNameDialogListener listener) {
        mListener = listener;
    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }


    public PickerUserFragment() {
        // Empty constructor required for DialogFragment
    }

    public static PickerUserFragment newInstance(String title, ArrayList<String> choices) {
        PickerUserFragment frag = new PickerUserFragment();
        Bundle args = new Bundle();



        args.putStringArrayList("choices",choices);
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pick_user, container);
        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        String title = getArguments().getString("title", "Enter Name");
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        view.findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {

                    if(!mEditText.getText().toString().equals("")) {
                        mCurrentUser = mEditText.getText().toString();
                    }

                    mListener.onFinishEditDialog(mCurrentUser);
                }

                dismiss();
            }
        });
        return view;
    }

}