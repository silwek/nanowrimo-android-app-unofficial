package nanowrimo.onishinji.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.ui.fragment.UserFragment;

public class FriendActivity extends ToolbarActivity {

    public final static String EXTRA_ID = UserFragment.EXTRA_ID;
    public final static String EXTRA_USERNAME = UserFragment.EXTRA_USERNAME;

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend);

        setTitle(getIntent().getStringExtra(EXTRA_USERNAME));

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
}
