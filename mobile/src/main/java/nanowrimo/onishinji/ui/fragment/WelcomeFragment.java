package nanowrimo.onishinji.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        mBtCampNano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.set(2015,3,1);
                WritingSessionHelper.getInstance().setNewSession(getString(R.string.camp_session_name),c.getTime());
                onWantNextSlide();
            }
        });


        return v;
    }
}
