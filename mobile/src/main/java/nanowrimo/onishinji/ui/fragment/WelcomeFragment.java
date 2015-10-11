package nanowrimo.onishinji.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Calendar;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.WritingSession;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 29/03/2015.
 */
public class WelcomeFragment extends SlidingFragment {

    protected Button mBtCampNano;
    protected Button mBtNanowrimo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_welcome, container, false);

        mBtCampNano = (Button) v.findViewById(R.id.bt_campnano);

        Calendar nextCampSession = WritingSessionHelper.getNextCampSession();
        Calendar nextNanowrimoSession = WritingSessionHelper.getNextNanowrimoSession();
        if (WritingSessionHelper.isSessionAvailableToInscription(nextCampSession)) {
            mBtCampNano.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = WritingSessionHelper.getNextCampSession();
                    WritingSessionHelper.getInstance().setNewSession(getString(R.string.camp_session_name), c.getTime(), WritingSession.CAMP);
                    onWantNextSlide();
                }
            });
        } else {
            disableButton(mBtCampNano);
        }

        mBtNanowrimo = (Button) v.findViewById(R.id.bt_nano);

        if (WritingSessionHelper.isSessionAvailableToInscription(nextNanowrimoSession)) {
            mBtNanowrimo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = WritingSessionHelper.getNextNanowrimoSession();
                    WritingSessionHelper.getInstance().setNewSession(getString(R.string.nano_session_name), c.getTime(), WritingSession.NANOWRIMO);
                    onWantNextSlide();
                }
            });
        } else {
            disableButton(mBtNanowrimo);
        }


        return v;
    }

    protected void disableButton(Button bt) {
        bt.setTextColor(getResources().getColor(R.color.welcome_button_text_inactive));
        bt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_send_disabled), null);
    }
}
