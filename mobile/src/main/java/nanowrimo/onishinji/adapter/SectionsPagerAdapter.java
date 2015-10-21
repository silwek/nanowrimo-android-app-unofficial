package nanowrimo.onishinji.adapter;


import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.ui.fragment.RankingFragment;
import nanowrimo.onishinji.ui.fragment.UserFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends SortableFragmentStatePagerAdapter {

    protected static final int POSITION_OFFSET = 1; //Ranking tab

    private UserFragment.OnRemoveListener mOnRemoveListener;
    protected RankingFragment.RankingListener mRankingListener;
    private Database dataSource;

    private RankingFragment mRankingFragment;
    private String mRankingTitle;
    protected ArrayList<String> mUsers;


    public SectionsPagerAdapter(Database dataSource, ArrayList<String> users, UserFragment.OnRemoveListener listener, RankingFragment.RankingListener rankingListener, FragmentManager fm, Context context) {
        super(fm);
        this.mOnRemoveListener = listener;
        this.mRankingListener = rankingListener;
        this.dataSource = dataSource;
        mRankingTitle = context.getString(R.string.ranking_title);
        mUsers = users;
    }

    public int getPosition(String username) {
        return mUsers.indexOf(username) + POSITION_OFFSET;//
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;

        if (position == 0) {
            if (mRankingFragment == null) {
                mRankingFragment = new RankingFragment();
                mRankingFragment.setRankingListener(mRankingListener);
            }
            f = mRankingFragment;
        } else {
            String id = mUsers.get(position - POSITION_OFFSET);

            f = new UserFragment();
            ((UserFragment) f).setId(id);
            ((UserFragment) f).setUsername(getPageTitle(position));
            ((UserFragment) f).setOnRemoveListener(mOnRemoveListener);
            ((UserFragment) f).setPosition(position);

            Log.d("fragment", " getItem " + position + " " + id);
        }

        return f;
    }

    @Override
    public long getItemId(int position) {
        if (position == 0) {
            return -1;
        } else
            return mUsers.get(position - POSITION_OFFSET).hashCode();
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        // Log.d("fragment", " getCount " + dataSource.getUsers().size());

        if (dataSource != null) {
            return mUsers.size() + POSITION_OFFSET;
        }

        return POSITION_OFFSET;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mRankingTitle;
        } else
            return dataSource.getNiceTitle(mUsers.get(position - POSITION_OFFSET));
    }

    /*
        @Override
        public int getItemPosition(Object object){

            UserFragment fragment = (UserFragment) object;

            int newPos = dataSource.getUsers().indexOf(fragment.getUserId());

            if(fragment.getPosition() == newPos) {
                Log.d("fragment", " alors l'ancienne position du fragment " + fragment.getPosition() + " est encore à la bonne place" + newPos);
                return POSITION_UNCHANGED;
            }

            Log.d("fragment", " le fragment n'est plus le même pour " + newPos + " vs " + fragment.getPosition());

            return POSITION_NONE;
        }
    */
    public void setDatabase(Database database) {
        this.dataSource = database;
    }

    public String getUserId(int position) {
        if (position >= POSITION_OFFSET) {
            return mUsers.get(position - POSITION_OFFSET);
        }
        return null;
    }
}