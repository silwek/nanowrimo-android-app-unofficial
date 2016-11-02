package nanowrimo.onishinji.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.WritingSession;
import nanowrimo.onishinji.ui.activity.HelpUsernameActivity;
import nanowrimo.onishinji.utils.WritingSessionHelper;

public class UserFormFragment extends SlidingFragment {

    protected EditText mTfUsername;
    protected Button mBtValid;
    protected Button mBtHelp;
    protected ImageView mIvSessionLogo;

    public UserFormFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_user_form, container, false);

        ((TextView) v.findViewById(R.id.tv_header)).setText(getString(R.string.welcome_form_title,
                WritingSessionHelper.getInstance().getSessionName()));
        mTfUsername = (EditText) v.findViewById(R.id.tf_user_name);
        mIvSessionLogo = (ImageView) v.findViewById(R.id.iv_session_logo);
        mBtValid = (Button) v.findViewById(R.id.bt_valid);
        mBtHelp = (Button) v.findViewById(R.id.bt_help);
        mBtValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mTfUsername.getText().toString().trim())) {
                    WritingSessionHelper.getInstance().setUserName(mTfUsername.getText().toString().trim());
                    onWantNextSlide();
                }
            }
        });
        mBtHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHelp();
            }
        });

        final int sessionType = WritingSessionHelper.getInstance().getSessionType();
        if (sessionType == WritingSession.CAMP) {
            mIvSessionLogo.setImageResource(R.drawable.drawer_header_campnano);
        } else if (sessionType == WritingSession.NANOWRIMO) {
            mIvSessionLogo.setImageResource(R.drawable.drawer_header_nanowrimo);
        }

        return v;
    }

    protected void onHelp() {
        startActivity(new Intent(getActivity(), HelpUsernameActivity.class));
    }

}
