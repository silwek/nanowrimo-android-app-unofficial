package nanowrimo.onishinji.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.event.UserEvent;
import nanowrimo.onishinji.model.BusManager;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.activity.FriendActivity;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 11/10/15.
 */
public class UserSummaryFragment extends Fragment {

    private TextView mTextViewGoal;
    private TextView mTextViewWordcount;
    private TextView mTextViewWordcountToday;
    private TextView mTextViewDailyTarget;
    private TextView mTextViewDailyTargetRemaining;
    private TextView mTextViewNbDayRemaining;
    private ProgressBar mProgressBar;

    protected boolean mIsUserLoading = false;

    protected boolean mIsSessionStarted = true;

    private String mId;
    private String mUsername;
    protected User mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mId = WritingSessionHelper.getInstance().getUser().getId();
        this.mUsername = WritingSessionHelper.getInstance().getUser().getName();
        mIsSessionStarted = WritingSessionHelper.getInstance().isSessionStarted();
        mIsUserLoading = true;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_dashboard, container, false);
        rootView.findViewById(R.id.bt_my_stats).setOnClickListener(new View.OnClickListener() {
                                                                       @Override
                                                                       public void onClick(View v) {
                                                                           if (getActivity() != null) {
                                                                               Intent intent = new Intent(getActivity(), FriendActivity.class);
                                                                               intent.putExtra(FriendActivity.EXTRA_ID, mId);
                                                                               intent.putExtra(FriendActivity.EXTRA_USERNAME, (mUsername == null) ? mId : mUsername);
                                                                               startActivity(intent);
                                                                           }
                                                                       }
                                                                   }

        );

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        mTextViewGoal = (TextView) getView().findViewById(R.id.goal);
        mTextViewWordcount = (TextView) getView().findViewById(R.id.wordcount);
        mTextViewWordcountToday = (TextView) getView().findViewById(R.id.wordCountToday);
        mTextViewDailyTarget = (TextView) getView().findViewById(R.id.dailyTarget);
        mTextViewDailyTargetRemaining = (TextView) getView().findViewById(R.id.dailyTargetRemaining);
        mTextViewNbDayRemaining = (TextView) getView().findViewById(R.id.nbDayRemaining);

    }

    @Override
    public void onStart() {
        super.onStart();
        checkLoader();
    }

    protected boolean isLoading() {
        return mIsUserLoading;
    }

    private void checkLoader() {
        if (isLoading()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
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

    @SuppressWarnings("unused")
    public void onEvent(UserEvent event) {
        mIsUserLoading = false;
        checkLoader();

        mUser = event.getUser();

        WritingSessionHelper.getInstance().setUser(mUser);

        mId = WritingSessionHelper.getInstance().getUser().getId();
        mUsername = WritingSessionHelper.getInstance().getUser().getName();

            if (getActivity() != null) {
            mTextViewGoal.setText(String.valueOf(mUser.getGoal()));
            mTextViewWordcount.setText(mUser.getWordcount() + "");
            mTextViewWordcountToday.setText(mUser.getWordCountToday() + "");
            mTextViewDailyTarget.setText(mIsSessionStarted ? mUser.getDailyTarget() + "" : "0");
            mTextViewDailyTargetRemaining.setText(mIsSessionStarted ? mUser.getDailyTargetRemaining() + "" : "0");
            mTextViewNbDayRemaining.setText(mUser.getNbDayRemaining() + "");

        }
    }
}