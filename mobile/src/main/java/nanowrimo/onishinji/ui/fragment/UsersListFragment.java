package nanowrimo.onishinji.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.event.FriendsEvent;
import nanowrimo.onishinji.model.BusManager;
import nanowrimo.onishinji.model.Friend;
import nanowrimo.onishinji.ui.activity.FriendActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class UsersListFragment extends Fragment {


    private ListView mListView;
    private ArrayList<Friend> mItems;
    private ArrayAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusManager.getInstance().getBus().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusManager.getInstance().getBus().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_users_list, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        mListView = (ListView) getView().findViewById(R.id.listView);

        mItems = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_friend, mItems);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("click", mItems.get(position).toString());
                Intent i = new Intent(getActivity(), FriendActivity.class);
                i.putExtra("id", mItems.get(position).getId());
                i.putExtra("username", mItems.get(position).getName());


                startActivity(i);
            }
        });

    }

    public void setData(ArrayList<Friend> items) {
        mItems.addAll(items);
        mAdapter.notifyDataSetChanged();

        mProgressBar.setVisibility(View.GONE);
    }

    public void onEvent(FriendsEvent event) {
        setData(event.getFriends());
    }
}