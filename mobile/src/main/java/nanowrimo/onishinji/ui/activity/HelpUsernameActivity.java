package nanowrimo.onishinji.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import nanowrimo.onishinji.BuildConfig;
import nanowrimo.onishinji.R;

/**
 * Created by Silwek on 11/10/15.
 */
public class HelpUsernameActivity extends ToolbarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_username_help);

        setTitle(R.string.help_title);

        View.OnClickListener supportListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupport();
            }
        };
        findViewById(R.id.bt_support_1).setOnClickListener(supportListener);
        findViewById(R.id.bt_support_2).setOnClickListener(supportListener);


        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onSupport() {
        String version = BuildConfig.VERSION_NAME;
        String mail = getString(R.string.about_us_contact_email);
        String subject = getString(R.string.app_name) + " " + version;

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mail, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.email_chooser)));
    }
}
