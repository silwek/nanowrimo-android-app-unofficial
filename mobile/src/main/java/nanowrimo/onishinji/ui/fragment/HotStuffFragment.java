package nanowrimo.onishinji.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.event.UserEvent;
import nanowrimo.onishinji.event.WordcountUpdateEvent;
import nanowrimo.onishinji.model.BusManager;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.task.SubmitWordcountTask;
import nanowrimo.onishinji.utils.AlertUtils;
import nanowrimo.onishinji.utils.DialogUtils;
import nanowrimo.onishinji.utils.PreferencesHelper;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 11/10/15.
 */
public class HotStuffFragment extends Fragment {

    protected EditText mWordCount;
    protected ImageButton mAddToWordcount;
    protected boolean mIsInit;
    protected User mUser;

    protected TextView mRemainingWords, mEndPrompt, mSessionDay;
    protected boolean mIsSessionEnded, mIsSessionStarted;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotstuff, container, false);

        View cardNotStarted = view.findViewById(R.id.card_not_started_yet);
        View cardEnded = view.findViewById(R.id.card_ended);
        View cardHotstuff = view.findViewById(R.id.card_hotstuff);

        mWordCount = (EditText) view.findViewById(R.id.ed_wordcount);
        mAddToWordcount = (ImageButton) view.findViewById(R.id.bt_add_to_wordcount);
        mRemainingWords = (TextView) view.findViewById(R.id.tv_advice);
        mEndPrompt = ((TextView) view.findViewById(R.id.info_session_ended));
        mSessionDay = (TextView) view.findViewById(R.id.session_day);

        mIsSessionStarted = WritingSessionHelper.getInstance().isSessionStarted();
        mIsSessionEnded = WritingSessionHelper.getInstance().isSessionEnded();

        if (mIsSessionEnded) {
            cardEnded.setVisibility(View.VISIBLE);
            mEndPrompt.setText(getString(R.string.dashboard_hotstuff_ended, WritingSessionHelper.getInstance().getSessionName()));
        } else if (mIsSessionStarted) {
            cardHotstuff.setVisibility(View.VISIBLE);
            mWordCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    onWordcountFocus(hasFocus);
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

            mAddToWordcount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWantAddToWordCount();
                }
            });

            mSessionDay.setText(getString(R.string.dashboard_hotstuff_day, WritingSessionHelper.getInstance().getSessionDay()));
        } else {
            cardNotStarted.setVisibility(View.VISIBLE);
            final int timeRemaining = WritingSessionHelper.getInstance().getTimeRemaining();
            if (timeRemaining == 1) {
                ((TextView) view.findViewById(R.id.info_session_not_started)).setText(getString(R.string.info_session_not_started_last_day, WritingSessionHelper.getInstance().getSessionName()));
            } else {
                ((TextView) view.findViewById(R.id.info_session_not_started)).setText(getString(R.string.info_session_not_started, WritingSessionHelper.getInstance().getSessionName(), timeRemaining));
            }
        }

        mIsInit = false;

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BusManager.getInstance().getBus().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusManager.getInstance().getBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWordCount.clearFocus();
    }

    @SuppressWarnings("unused")
    public void onEvent(UserEvent event) {
        mUser = event.getUser();
        showWordcount();
        showAdvice();
        showEndAdviceIfNeeded();

    }

    private void onWordcountFocus(boolean hasFocus) {
        if (!mIsInit) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mWordCount.getWindowToken(), 0);
            mWordCount.clearFocus();
        } else if (!canSubmit()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mWordCount.getWindowToken(), 0);
            mWordCount.clearFocus();
            DialogUtils.displayMissingSecretKey(getActivity(), getActivity());
        } else if (hasFocus) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mWordCount, InputMethodManager.SHOW_IMPLICIT);
        } else {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mWordCount.getWindowToken(), 0);
        }
    }

    protected void onWantAddToWordCount() {
        if (canSubmit()) {
            DialogUtils.displayAddWordcountDialog(getActivity(), new DialogUtils.CallbackWithInteger() {
                @Override
                public void onSuccess(int response) {
                    final int oldWorcount = mUser.getWordcount();
                    final int newWordcount = oldWorcount + response;
                    mWordCount.setText(String.valueOf(newWordcount));
                    submitWordcount();
                }
            });
        }
    }

    protected boolean canSubmit() {
        return mIsInit && !TextUtils.isEmpty(PreferencesHelper.getSecretKey(getActivity()));
    }

    protected void submitWordcount() {
        mWordCount.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mWordCount.getWindowToken(), 0);
        final int newWordcount = Integer.parseInt(mWordCount.getText().toString());
        if (newWordcount == newWordcount) {//Not a NaN
            final int oldWorcount = mUser.getWordcount();
            final String userid = Database.getInstance(getActivity()).getUsers().get(0);
            final String secretkey = PreferencesHelper.getSecretKey(getActivity());
            SubmitWordcountTask task = new SubmitWordcountTask(userid, secretkey, new SubmitWordcountTask.Callback() {
                @Override
                public void onSuccess() {
                    AlertUtils.display(getActivity(), R.string.dashboard_hotstuff_update_wordcount_success);
                    BusManager.getInstance().getBus().post(new WordcountUpdateEvent());
                }

                @Override
                public void onError() {
                    mWordCount.setText(String.valueOf(oldWorcount));
                    AlertUtils.displayError(getActivity(), R.string.dashboard_hotstuff_update_wordcount_error);
                }
            });
            task.execute(new Integer[]{newWordcount});

        }
    }

    protected void showEndAdviceIfNeeded() {
        if (mIsSessionEnded && mUser != null) {
            boolean win = mUser.getGoal() <= mUser.getWordcount();
            if (win) {
                mEndPrompt.setText(getString(R.string.dashboard_hotstuff_ended_win, WritingSessionHelper.getInstance().getSessionName()));
            } else {
                mEndPrompt.setText(getString(R.string.dashboard_hotstuff_ended, WritingSessionHelper.getInstance().getSessionName()));
            }
        }
    }

    protected void showWordcount() {
        if (mUser != null) {
            mWordCount.setText(String.valueOf(mUser.getWordcount()));
            mIsInit = true;
        }
    }

    protected void showAdvice() {
        if (mUser != null) {
            final int remainingWordcount = mUser.getDailyTarget() - mUser.getWordCountToday();
            mRemainingWords.setText(getDailyAdvice(remainingWordcount));
        }
    }

    protected String getDailyAdvice(int remainingWordcount) {
        if (remainingWordcount > 1000) {
            return getString(R.string.dashboard_hotstuff_advice_remaining_a_lot, remainingWordcount);
        } else if (remainingWordcount > 1) {
            return getString(R.string.dashboard_hotstuff_advice_remaining, remainingWordcount);
        } else if (remainingWordcount == 1) {
            return getString(R.string.dashboard_hotstuff_advice_remaining_one);
        } else if (remainingWordcount > -1) {
            return getString(R.string.dashboard_hotstuff_advice_exact_wordcount);
        } else {
            return getString(R.string.dashboard_hotstuff_advice_bonus_wordcount, -remainingWordcount);
        }
    }
}
