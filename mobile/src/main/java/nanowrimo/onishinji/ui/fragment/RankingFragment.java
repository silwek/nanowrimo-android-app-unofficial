package nanowrimo.onishinji.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.adapter.RankingAdapter;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.task.GetFavoriesRankingTask;

/**
 * Created by Silwek on 13/10/15.
 */
public class RankingFragment extends Fragment implements GetFavoriesRankingTask.OnFavoritesSortedListener {

    private ListView mListView;
    private RankingAdapter mAdapter;
    private ProgressBar mProgressBar;
    protected GetFavoriesRankingTask mTask;
    protected RankingListener mRankingListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ranking, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        mListView = (ListView) getView().findViewById(R.id.listView);

        mAdapter = new RankingAdapter(getActivity());

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSelectUser(position);
            }
        });
        mProgressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        showRank();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    public void setRankingListener(RankingListener listener) {
        mRankingListener = listener;
    }

    protected void showRank() {
        if (getActivity() == null)
            return;
        final Database database = Database.getInstance(getActivity());
        ArrayList<String> list = (ArrayList<String>) database.getUsers().clone();
        mAdapter.setUserId(list.get(0));
        if (list.size() == 0) {
            mAdapter.clear();
        } else {
            mAdapter.clear();
            final ArrayList<User> users = mRankingListener.getDefaultUsers();
            if (users != null && users.size() > 0) {
                mProgressBar.setVisibility(View.GONE);
                mAdapter.addAll(users);
            }
            mTask = new GetFavoriesRankingTask(this);
            mTask.execute(list);
        }

    }

    @Override
    public void onFavoritesSorted(ArrayList<User> users) {
        mAdapter.clear();
        mAdapter.addAll(users);
        mProgressBar.setVisibility(View.GONE);
        mTask = null;
        mRankingListener.onSortUsers(users);
    }

    protected void onSelectUser(int position) {
        User u = mAdapter.getItem(position);
        mRankingListener.onSelectUser(u.getId());
    }

    public interface RankingListener {
        void onSelectUser(String username);

        void onSortUsers(ArrayList<User> users);

        ArrayList<User> getDefaultUsers();
    }
}
