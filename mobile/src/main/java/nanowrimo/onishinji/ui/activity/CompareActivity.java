package nanowrimo.onishinji.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.BusManager;
import nanowrimo.onishinji.model.Friends;
import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.utils.StringUtils;

public class CompareActivity  extends FragmentActivity {

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

        getActionBar().setDisplayHomeAsUpEnabled(true);
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
