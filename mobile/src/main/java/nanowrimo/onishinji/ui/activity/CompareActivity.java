package nanowrimo.onishinji.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import nanowrimo.onishinji.R;

public class CompareActivity extends ToolbarActivity {

    private String mIdUser1;
    private String mIdUser2;
    private String mUsername1;
    private String mUsername2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compare);

        mIdUser1 = getIntent().getStringExtra("id_user_1");
        mIdUser2 = getIntent().getStringExtra("id_user_2");

        mUsername1 = getIntent().getStringExtra("username_user_1");
        mUsername2 = getIntent().getStringExtra("username_user_2");

        setTitle(getString(R.string.title_compare_activity, mUsername1, mUsername2));

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
