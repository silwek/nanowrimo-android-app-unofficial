package nanowrimo.onishinji.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;

import nanowrimo.onishinji.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView section_label;
    private String username;
    private OnRemoveListener mOnRemoveListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);
//        this.username = savedInstanceState.getString("name");
        setHasOptionsMenu(true);

//        this.username = getArguments().getString("name");

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.placeholderfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_user:
                if(mOnRemoveListener != null) {
                    mOnRemoveListener.remove(username);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        section_label = (TextView) getView().findViewById(R.id.section_label);
        section_label.setText(username);
       // updateUI();
    }

    private void updateUI() {
        Log.d("fragment", "will update UI with " + username);
        section_label.setText(username);
    }

    public void setUsername(String s) {
        this.username = s;
    }

    public void setOnRemoveListener(OnRemoveListener listener) {
        mOnRemoveListener = listener;
    }

    public void update(String s) {

        if(s != this.username) {
            this.username = s;
            updateUI();
        }
    }

    public String getUsername() {
        return username;
    }

    public interface OnRemoveListener {
        void remove(String username);
    }
}