package nanowrimo.onishinji.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.event.WordcountUpdateEvent;
import nanowrimo.onishinji.model.BusManager;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.task.GetFavoriesRankingTask;
import nanowrimo.onishinji.ui.activity.FavoriesActivity;
import nanowrimo.onishinji.ui.activity.FriendActivity;
import nanowrimo.onishinji.utils.DialogUtils;

/**
 * Created by Silwek on 11/10/15.
 */
public class FavsFragment extends Fragment implements GetFavoriesRankingTask.OnFavoritesSortedListener {

    protected View mRankOne, mRankTwo, mRankThree, mRankSeparatorTwo, mRankSeparatorThree, mRankSeparatorMore;
    protected TextView mRankOneWC, mRankTwoWC, mRankThreeWC;
    protected TextView mRankOneName, mRankTwoName, mRankThreeName;
    protected TextView mRankMore;
    protected Button mSeeAll;
    protected ProgressBar mProgressBar;
    protected View mCardNoFav, mCardRanking;

    protected GetFavoriesRankingTask mTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favs, container, false);

        view.findViewById(R.id.bt_add_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAddUserDialog();
            }
        });

        view.findViewById(R.id.section_label_favs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FavoriesActivity.class));
            }
        });

        mProgressBar = (ProgressBar) view.findViewById(R.id.fav_progressBar);
        mSeeAll = (Button) view.findViewById(R.id.bt_see_all);
        mSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FavoriesActivity.class));
            }
        });

        mRankOne = view.findViewById(R.id.rank_one);
        mRankTwo = view.findViewById(R.id.rank_two);
        mRankThree = view.findViewById(R.id.rank_three);
        mRankSeparatorTwo = view.findViewById(R.id.rank_two_separator);
        mRankSeparatorThree = view.findViewById(R.id.rank_three_separator);
        mRankSeparatorMore = view.findViewById(R.id.rank_more_separator);

        mRankOneWC = (TextView) view.findViewById(R.id.rank_one_wordcount);
        mRankTwoWC = (TextView) view.findViewById(R.id.rank_two_wordcount);
        mRankThreeWC = (TextView) view.findViewById(R.id.rank_three_wordcount);
        mRankOneName = (TextView) view.findViewById(R.id.rank_one_name);
        mRankTwoName = (TextView) view.findViewById(R.id.rank_two_name);
        mRankThreeName = (TextView) view.findViewById(R.id.rank_three_name);
        mRankMore = (TextView) view.findViewById(R.id.rank_more);

        mCardRanking = view.findViewById(R.id.card_ranking);
        mCardNoFav = view.findViewById(R.id.card_no_fav);

        mCardNoFav.findViewById(R.id.bt_no_fav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAddUserDialog();
            }
        });
        mProgressBar.setVisibility(View.VISIBLE);

        return view;
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

    protected void showRank() {
        if (getActivity() == null)
            return;
        final Database database = Database.getInstance(getActivity());
        ArrayList<String> list = (ArrayList<String>) database.getUsers().clone();
        list.remove(0);
        if (list.size() == 0) {
            mCardRanking.setVisibility(View.GONE);
            mCardNoFav.setVisibility(View.VISIBLE);
        } else {
            mCardRanking.setVisibility(View.VISIBLE);
            mCardNoFav.setVisibility(View.GONE);
            mTask = new GetFavoriesRankingTask(this);
            mTask.execute(list);
        }

    }


    public void displayAddUserDialog() {
        DialogUtils.displayAddUserDialog(getActivity(), new DialogUtils.CallbackWithUser() {
            @Override
            public void onSuccess(User user) {
                onFavUpdate();
            }
        });
    }

    public void displayRemoveUserDialog(final String id, final String name) {
        DialogUtils.displayRemoveUserDialog(getActivity(), id, name, new DialogUtils.Callback() {
            @Override
            public void onSuccess() {
                onFavUpdate();
            }
        });
    }

    protected void onFavUpdate() {
        if (getActivity() == null)
            return;
        showRank();
    }

    @Override
    public void onFavoritesSorted(ArrayList<User> users) {
        mTask = null;
        final int nbFavs = users.size();
        if (nbFavs > 0) {
            showUserRank(users.get(0), mRankOne, mRankOneWC, mRankOneName);
            if (nbFavs > 1) {
                mRankSeparatorTwo.setVisibility(View.VISIBLE);
                showUserRank(users.get(1), mRankTwo, mRankTwoWC, mRankTwoName);
                if (nbFavs > 2) {
                    mRankSeparatorThree.setVisibility(View.VISIBLE);
                    showUserRank(users.get(2), mRankThree, mRankThreeWC, mRankThreeName);
                } else {
                    mRankThree.setVisibility(View.GONE);
                    mRankSeparatorThree.setVisibility(View.GONE);
                }

            } else {
                mRankTwo.setVisibility(View.GONE);
                mRankThree.setVisibility(View.GONE);
                mRankSeparatorTwo.setVisibility(View.GONE);
                mRankSeparatorThree.setVisibility(View.GONE);
            }
        } else {
            mRankOne.setVisibility(View.GONE);
            mRankTwo.setVisibility(View.GONE);
            mRankThree.setVisibility(View.GONE);
            mRankSeparatorTwo.setVisibility(View.GONE);
            mRankSeparatorThree.setVisibility(View.GONE);
        }

        if (nbFavs > 3) {
            final int more = nbFavs - 3;
            if (more == 1) {
                mRankMore.setText(getString(R.string.dashboard_my_favories_more_one, more));
            } else {
                mRankMore.setText(getString(R.string.dashboard_my_favories_more, more));
            }
            mRankMore.setVisibility(View.VISIBLE);
            mRankSeparatorMore.setVisibility(View.VISIBLE);
        } else {
            mRankMore.setVisibility(View.GONE);
            mRankSeparatorMore.setVisibility(View.GONE);
        }

        mProgressBar.setVisibility(View.GONE);
    }

    protected void showUserRank(User u, View container, TextView tfwordcount, TextView tfname) {
        final String id = u.getId();
        final String name = u.getName();

        container.setVisibility(View.VISIBLE);
        container.setLongClickable(true);
        container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                displayRemoveUserDialog(id, name);
                return true;
            }
        });

        if (u.getWordcount() == User.NO_DATA_WORDCOUNT) {
            tfwordcount.setText("-");
            tfname.setText(name);
            container.setOnClickListener(null);
        } else {
            tfwordcount.setText(String.valueOf(u.getWordcount()));
            tfname.setText(name);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), FriendActivity.class);
                    i.putExtra(FriendActivity.EXTRA_ID, id);
                    i.putExtra(FriendActivity.EXTRA_USERNAME, name);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BusManager.getInstance().getBus().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusManager.getInstance().getBus().unregister(this);
    }

    @SuppressWarnings("unused")
    public void onEvent(WordcountUpdateEvent event) {
        showRank();
    }
}
