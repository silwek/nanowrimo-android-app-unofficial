package nanowrimo.onishinji.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nanowrimo.onishinji.BuildConfig;
import nanowrimo.onishinji.R;

/**
 * Created by Silwek on 15/10/15.
 */
public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        view.findViewById(R.id.contact_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String version = BuildConfig.VERSION_NAME;
                String mail = getString(R.string.about_us_contact_email);
                String subject = getString(R.string.app_name) + " " + version;

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", mail, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
        return view;
    }

}
