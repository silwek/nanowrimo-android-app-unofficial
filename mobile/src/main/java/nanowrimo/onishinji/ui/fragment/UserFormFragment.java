package nanowrimo.onishinji.ui.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.utils.WritingSessionHelper;

public class UserFormFragment extends SlidingFragment {

    protected EditText mTfUsername;
    protected Button mBtValid;

    public UserFormFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_user_form, container, false);

        ((TextView) v.findViewById(R.id.tv_header)).setText(getString(R.string.welcome_form_title,
                        WritingSessionHelper.getInstance().getSessionName()));
        mTfUsername = (EditText) v.findViewById(R.id.tf_user_name);
        mBtValid = (Button) v.findViewById(R.id.bt_valid);
        mBtValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mTfUsername.getText().toString().trim())) {
                    WritingSessionHelper.getInstance().setUserName(mTfUsername.getText().toString().trim());
                    onWantNextSlide();
                }
            }
        });

        return v;
    }

}
