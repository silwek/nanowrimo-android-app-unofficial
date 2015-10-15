package nanowrimo.onishinji.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 11/10/15.
 */
public class HotStuffFragment extends Fragment {

    protected EditText mWordCount;
    protected ImageButton mWordCountSubmit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotstuff, container, false);

        View cardNotStarted = view.findViewById(R.id.card_not_started_yet);
        View cardEnded = view.findViewById(R.id.card_ended);
        View cardHotstuff = view.findViewById(R.id.card_hotstuff);

        mWordCount = (EditText) view.findViewById(R.id.ed_wordcount);
        mWordCountSubmit = (ImageButton) view.findViewById(R.id.bt_wordcount);

        final boolean isSessionStarted = WritingSessionHelper.getInstance().isSessionStarted();
        final boolean isSessionEnded = WritingSessionHelper.getInstance().isSessionEnded();

        if (isSessionEnded) {
            cardEnded.setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.info_session_ended)).setText(getString(R.string.info_session_ended, WritingSessionHelper.getInstance().getSessionName()));
        } else if (isSessionStarted) {
            cardHotstuff.setVisibility(View.VISIBLE);
            mWordCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                    } else {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            });
            mWordCount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        submitWordcount();
                    }
                    return false;
                }
            });

            mWordCountSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWantSubmit();
                }
            });
        } else {
            cardNotStarted.setVisibility(View.VISIBLE);
            final int timeRemaining = WritingSessionHelper.getInstance().getTimeRemaining();
            if (timeRemaining == 1) {
                ((TextView) view.findViewById(R.id.info_session_not_started)).setText(getString(R.string.info_session_not_started_last_day, WritingSessionHelper.getInstance().getSessionName()));
            } else {
                ((TextView) view.findViewById(R.id.info_session_not_started)).setText(getString(R.string.info_session_not_started, WritingSessionHelper.getInstance().getSessionName(), timeRemaining));
            }
        }

        return view;
    }

    protected void onWantSubmit() {
        if (mWordCount.isFocused()) {
            submitWordcount();
        } else {
            mWordCount.requestFocus();
        }
    }

    protected void submitWordcount() {
        mWordCount.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mWordCount.getWindowToken(), 0);
        final int newWordcount = Integer.parseInt(mWordCount.getText().toString());
        if (newWordcount == newWordcount) {//Not a NaN
            Toast.makeText(getActivity(), "Try to submit : " + newWordcount, Toast.LENGTH_SHORT).show();
        }
    }
}
