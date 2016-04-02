package nanowrimo.onishinji.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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
import android.widget.TextView;

import java.util.List;

import it.sephiroth.android.library.tooltip.TooltipManager;
import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.WritingSession;
import nanowrimo.onishinji.ui.activity.SplashscreenActivity;
import nanowrimo.onishinji.utils.PreferencesHelper;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 15/10/15.
 */
public class SettingsFragment extends Fragment {

    protected static final int TOOLTIP_SECRETKEY = 2151;
    protected static final String LINK_NANOWRIMO_WRITE_API_KEY = "http://nanowrimo.org/api/wordcount";

    protected ViewGroup mView;
    protected EditText mEditTextSecretKey;
    protected View mBtClearSecretKey;
    protected View mHelpSecretKey;

    protected boolean mIsSecretKeyFieldInit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (WritingSessionHelper.getInstance().getSessionType() == WritingSession.NANOWRIMO)
            mView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        else
            mView = (ViewGroup) inflater.inflate(R.layout.fragment_settings_camp, container, false);

        mView.findViewById(R.id.settings_container).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        if (WritingSessionHelper.getInstance().getSessionType() == WritingSession.NANOWRIMO) {

            mHelpSecretKey = mView.findViewById(R.id.bt_help_secretkey);
            mHelpSecretKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onHelpSecretKey();
                }
            });
            mEditTextSecretKey = (EditText) mView.findViewById(R.id.et_secretkey);
            mBtClearSecretKey = mView.findViewById(R.id.bt_clear_secretkey);

            mIsSecretKeyFieldInit = false;
            mBtClearSecretKey.setVisibility(View.GONE);

            mEditTextSecretKey.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                        mEditTextSecretKey.append("");
                        mBtClearSecretKey.setVisibility(View.VISIBLE);
                    } else {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        if (mIsSecretKeyFieldInit) {
                            saveSecretKey();
                        }
                        mBtClearSecretKey.setVisibility(View.GONE);
                    }
                }
            });
            mEditTextSecretKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        v.clearFocus();
                    }
                    return false;
                }
            });

            mView.findViewById(R.id.bt_clear_secretkey).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearSecretKeyTF();
                }
            });

            mView.findViewById(R.id.bt_secretkey_link).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOpenWritingApi();
                }
            });

        }

        WritingSessionHelper wsh = WritingSessionHelper.getInstance();
        ((TextView) mView.findViewById(R.id.account_session)).setText(getString(R.string.setting_account_shortdescription, wsh.getUserName(), wsh.getSessionName()));


        mView.findViewById(R.id.bt_clear_my_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearData();
            }
        });
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mEditTextSecretKey != null) {
            mEditTextSecretKey.setText("");
            final String secretKey = PreferencesHelper.getSecretKey(getActivity());
            mEditTextSecretKey.append(secretKey);
            mIsSecretKeyFieldInit = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mEditTextSecretKey != null)
            mEditTextSecretKey.clearFocus();
    }

    protected void saveSecretKey() {
        String secretKey = mEditTextSecretKey.getText().toString();
        PreferencesHelper.setSecretKey(getActivity(), secretKey);
    }

    protected void clearSecretKeyTF() {
        mEditTextSecretKey.setText("");
    }

    protected void onHelpSecretKey() {
        if (getActivity() == null)
            return;

        TooltipManager.getInstance()
                .create(getActivity(), TOOLTIP_SECRETKEY)
                .anchor(mHelpSecretKey, TooltipManager.Gravity.BOTTOM)
                .closePolicy(TooltipManager.ClosePolicy.TouchOutsideExclusive, 0)
                .activateDelay(800)
                .withCustomView(R.layout.tooltip_text, false)
                .text(getString(R.string.secretkey_long_description))
                .maxWidth(500)
                .withStyleId(R.style.ToolTipLayoutCustomStyle)
                .show();

    }

    protected void onOpenWritingApi() {
        if (getActivity() == null)
            return;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(LINK_NANOWRIMO_WRITE_API_KEY));
        // Verify it resolves
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(i, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (isIntentSafe) {
            startActivity(i);
        } else {
            //TODO show info
        }
    }

    protected void onClearData() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(getString(R.string.setting_account_cleardata_popup_title));
        alert.setMessage(getString(R.string.setting_account_cleardata_popup_message));
        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                onClearDataConfirm();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.setCancelable(true);
        alert.show();
    }

    protected void onClearDataConfirm() {
        if (getActivity() == null)
            return;
        WritingSessionHelper.getInstance().clearConfig(getActivity());
        Intent intent = new Intent(getActivity(), SplashscreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
